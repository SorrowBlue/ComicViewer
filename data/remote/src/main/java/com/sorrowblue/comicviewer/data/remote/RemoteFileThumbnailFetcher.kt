package com.sorrowblue.comicviewer.data.remote

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.os.Build
import androidx.core.graphics.scale
import androidx.exifinterface.media.ExifInterface
import coil.ImageLoader
import coil.annotation.ExperimentalCoilApi
import coil.decode.DataSource
import coil.decode.DecodeUtils
import coil.decode.ImageSource
import coil.disk.DiskCache
import coil.fetch.FetchResult
import coil.fetch.Fetcher
import coil.fetch.SourceResult
import coil.request.Options
import coil.size.Dimension
import coil.size.Scale
import com.sorrowblue.comicviewer.data.coil.RemoteFileThumbnailFetcherFactory
import com.sorrowblue.comicviewer.data.common.FileModel
import com.sorrowblue.comicviewer.data.common.FolderThumbnailOrderModel
import com.sorrowblue.comicviewer.data.common.ServerFileModel
import com.sorrowblue.comicviewer.data.common.ServerModel
import com.sorrowblue.comicviewer.data.datasource.FileModelLocalDataSource
import com.sorrowblue.comicviewer.data.remote.client.FileClientFactory
import com.sorrowblue.comicviewer.data.remote.di.ThumbnailDiskCache
import com.sorrowblue.comicviewer.data.remote.reader.FileReader
import com.sorrowblue.comicviewer.data.remote.reader.FileReaderFactory
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
import java.io.InputStream
import kotlin.math.floor
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToStream
import logcat.logcat
import okhttp3.internal.closeQuietly
import okio.Buffer
import okio.BufferedSource
import okio.ByteString.Companion.encodeUtf8

@Serializable
internal class CacheResponse(var thumbnail: List<String> = emptyList()) {
    constructor(bufferedSource: BufferedSource) : this(
        kotlin.runCatching { Json.decodeFromString<CacheResponse>(bufferedSource.readUtf8()) }
            .getOrNull()?.thumbnail.orEmpty()
    )
}

@Serializable
data class ComicThumbnailMeta(
    val comicFileLastModified: Long,
    val comicFileSize: Long
)

@OptIn(ExperimentalCoilApi::class, ExperimentalSerializationApi::class)
internal class RemoteFileThumbnailFetcher(
    private val data: ServerFileModel,
    private val options: Options,
    lazyDiskCache: dagger.Lazy<DiskCache?>,
    private val context: Context,
    private val fileModelLocalDataSource: FileModelLocalDataSource,
    private val fileClientFactory: FileClientFactory,
    private val fileReaderFactory: FileReaderFactory,
    private val folderThumbnailOrder: suspend () -> FolderThumbnailOrderModel
) : Fetcher {

    private val serverModel get() = data.server
    private val fileModel get() = data.file
    private val width = (options.size.width as? Dimension.Pixels)?.px?.toFloat() ?: 300f
    private val height = (options.size.height as? Dimension.Pixels)?.px?.toFloat() ?: 300f

    private val diskCache by lazyDiskCache

    override suspend fun fetch(): FetchResult {
        val snapshot = readFromDiskCache()
        if (fileModel is FileModel.Folder) {
            return fetchFolder(snapshot)
        }
        return fetchFile(snapshot)
    }

    private fun DiskCache.Snapshot.toCacheResponse(): CacheResponse? {
        return try {
            fileSystem.read(metadata) {
                CacheResponse(this)
            }
        } catch (_: IOException) {
            // If we can't parse the metadata, ignore this entry.
            null
        }
    }

    private suspend fun fetchFolder(snapshot: DiskCache.Snapshot?): FetchResult {
        val size = (width / (height / 11).toInt()).toInt() - 6
        if (snapshot != null) {
            // キャッシュされた画像は手動で追加された可能性が高いため、常にメタデータが空の状態で返されます。
            if (fileSystem.metadata(snapshot.metadata).size == 0L) {
                return SourceResult(
                    source = snapshot.toImageSource(),
                    mimeType = null,
                    dataSource = DataSource.DISK
                )
            }
            // 作成済みの場合
            // サムネイル候補キャッシュを取得
            val list = fileModelLocalDataSource.getCacheKeys(
                serverModel.id,
                fileModel.path,
                size,
                folderThumbnailOrder.invoke()
            )
            logcat { "${fileModel.name} 作成済み" }
            // 候補が適格である場合、キャッシュから候補を返します。
            if (snapshot.toCacheResponse()?.thumbnail == list) {
                logcat { "${fileModel.name} 作成済み キャッシュから返却" }
                // 最新の場合
                return SourceResult(
                    source = snapshot.toImageSource(),
                    mimeType = null,
                    dataSource = DataSource.DISK
                )
            } else {
                logcat { "${fileModel.name} 作成済み 古い" }
            }
        } else {
            logcat { "${fileModel.name} 未作成" }
        }
        try {
            // 未作成・古い場合、サムネイルキャッシュから取得する
            val list = cacheList(size, folderThumbnailOrder.invoke())
            logcat { "${fileModel.name} list=${list.map { it.first }}" }
            val snapshot1 = writeToDiskCacheBookshelf(snapshot, list)
            return if (snapshot1 != null) {
                logcat { "Return from Disk. ${fileModel.name}" }
                SourceResult(
                    source = snapshot1.toImageSource(),
                    mimeType = null,
                    dataSource = DataSource.DISK
                )
            } else {
                logcat { "未作成の場合、サムネイルキャッシュから取得する。作成失敗。path=${fileModel.name}" }
                SourceResult(
                    source = readFromDiskCache()!!.toImageSource(),
                    mimeType = null,
                    dataSource = DataSource.DISK
                )
            }
        } catch (e: Exception) {
            snapshot?.closeQuietly()
            e.printStackTrace()
            throw e
        }
    }

    private suspend fun fetchFile(snapshot: DiskCache.Snapshot?): FetchResult {
        var fileReader: FileReader? = null
        try {
            if (snapshot != null) {
                val meta = fileSystem.read(snapshot.metadata) {
                    kotlin.runCatching {
                        Json.decodeFromString<ComicThumbnailMeta>(readUtf8())
                    }.getOrNull() ?: ComicThumbnailMeta(0, 0)
                }

                if (meta.comicFileLastModified == fileModel.lastModifier && meta.comicFileSize == fileModel.size) {
                    // キャッシュから返す。
                    return SourceResult(
                        source = snapshot.toImageSource(),
                        mimeType = null,
                        dataSource = DataSource.DISK
                    )
                }
            }
            delay(750)
            fileReader =
                fileReader(serverModel, fileModel)
                    ?: throw Exception("ファイルが見つかりません。serverModelId=${serverModel.id}, path=${fileModel.path}")
            if (fileReader.pageCount() == 0) {
                throw Exception("Page count is 0 ${fileModel.path}.")
            }
            try {
                val snapshot1 = writeToDiskCache(snapshot = snapshot, fileReader = fileReader)
                if (snapshot1 != null) {
                    return SourceResult(
                        source = snapshot1.toImageSource(),
                        mimeType = null,
                        dataSource = DataSource.NETWORK
                    )
                }
                var bytes = fileReader.pageInputStream(0).use(InputStream::readBytes)
                return if (bytes.isNotEmpty()) {
                    SourceResult(
                        source = ImageSource(Buffer().apply { write(bytes) }, context),
                        mimeType = null,
                        dataSource = DataSource.NETWORK
                    )
                } else {
                    bytes = fileReader.pageInputStream(0).use(InputStream::readBytes)
                    SourceResult(
                        source = ImageSource(Buffer().apply { write(bytes) }, context),
                        mimeType = null,
                        dataSource = DataSource.NETWORK
                    )
                }
            } catch (e: Exception) {
                throw e
            }
        } catch (e: Exception) {
            snapshot?.closeQuietly()
            throw e
        } finally {
            fileReader?.closeQuietly()
        }
    }

    private fun readFromDiskCache(): DiskCache.Snapshot? {
        return if (options.diskCachePolicy.readEnabled) {
            diskCache?.get(diskCacheKey)
        } else {
            null
        }
    }

    private suspend fun writeToDiskCacheBookshelf(
        snapshot: DiskCache.Snapshot?,
        list: List<Pair<String, DiskCache.Snapshot>>
    ): DiskCache.Snapshot? {
        // Open a new editor.
        val editor = if (snapshot != null) {
            snapshot.closeAndEdit()
        } else {
            diskCache?.edit(diskCacheKey)
        }
        // Return `null` if we're unable to write to this entry.
        if (editor == null) return null

        val result = Bitmap.createBitmap(width.toInt(), height.toInt(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)
        canvas.drawColor(Color.TRANSPARENT)
        val step = floor(height / 12)
        var left = 0f
        list.reversed().forEach {
            it.second.use { snap ->
                combineThumbnail(canvas, snap, left)
                left += step
            }
        }
        return withContext(NonCancellable) {
            fileSystem.write(editor.metadata) {
                outputStream().use { outputStream ->
                    Json.encodeToStream(CacheResponse(list.map { it.first }), outputStream)
                }
            }
            fileSystem.write(editor.data) {
                outputStream().use {
                    result.compress(COMPRESS_FORMAT, 75, it)
                }
                result.recycle()
            }
            editor.commitAndGet()
        }
    }

    private suspend fun cacheList(
        size: Int,
        folderThumbnailOrder: FolderThumbnailOrderModel
    ): List<Pair<String, DiskCache.Snapshot>> {
        val cacheKeyList = fileModelLocalDataSource.getCacheKeys(
            serverModel.id,
            fileModel.path,
            size,
            folderThumbnailOrder
        )
        val notEnough = cacheKeyList.size < size
        val list = cacheKeyList.mapNotNull { cacheKey ->
            diskCache?.get(cacheKey)?.let {
                cacheKey to it
            } ?: null.apply {
                fileModelLocalDataSource.removeCacheKey(cacheKey)
            }
        }
        return if (notEnough || size <= list.size) {
            list
        } else {
            list.forEach { it.second.closeQuietly() }
            cacheList(size, folderThumbnailOrder)
        }
    }

    private fun combineThumbnail(canvas: Canvas, snap: DiskCache.Snapshot, rightSpace: Float) {
        val bitmap = BitmapFactory.decodeFile(snap.data.toString())
        val resizeScale =
            if (bitmap.width >= bitmap.height) width / bitmap.width else height / bitmap.height
        val scale = bitmap.scale(
            (bitmap.width * resizeScale).toInt(),
            (bitmap.height * resizeScale).toInt(),
            true
        )
        bitmap.recycle()
        canvas.drawBitmap(scale, width - scale.width - rightSpace, 0f, null)
        scale.recycle()
    }

    private suspend fun writeToDiskCache(
        snapshot: DiskCache.Snapshot?,
        fileReader: FileReader
    ): DiskCache.Snapshot? {

        // Open a new editor.
        val editor = if (snapshot != null) {
            snapshot.closeAndEdit()
        } else {
            diskCache?.edit(diskCacheKey)
        }

        // Return `null` if we're unable to write to this entry.
        if (editor == null) {
            return null
        }

        try {
            // Write the response to the disk cache.
            val thumbnail = fileReader.thumbnailBitmap()
            if (thumbnail != null) {
                fileSystem.write(editor.data) {
                    thumbnail.compress(COMPRESS_FORMAT, 75, outputStream())
                }
                fileSystem.write(editor.metadata) {
                    Json.encodeToStream(
                        ComicThumbnailMeta(fileModel.lastModifier, fileModel.size),
                        outputStream()
                    )
                }
                fileModelLocalDataSource.update(
                    fileModel.path,
                    serverModel.id,
                    diskCacheKey,
                    fileReader.pageCount()
                )
            }
            return editor.commitAndGet()
        } catch (e: Exception) {
            editor.abortQuietly()
            throw e
        } finally {
            fileReader.closeQuietly()
        }
    }

    private suspend fun FileReader.thumbnailBitmap(): Bitmap? {
        return pageInputStream(0).use {
            val exif = ExifInterface(it)
            if (exif.hasThumbnail()) exif.thumbnailBitmap else null
        } ?: kotlin.run {
            BitmapFactory.Options().run {
                inJustDecodeBounds = true
                pageInputStream(0).use {
                    BitmapFactory.decodeStream(it, null, this)
                }
                inSampleSize = DecodeUtils
                    .calculateInSampleSize(
                        outWidth,
                        outHeight,
                        width.toInt(),
                        height.toInt(),
                        Scale.FIT
                    )
                inJustDecodeBounds = false
                pageInputStream(0).use {
                    BitmapFactory.decodeStream(it, null, this)
                }
            }
        }
    }

    private fun DiskCache.Snapshot.toImageSource(): ImageSource {
        return ImageSource(data, fileSystem, diskCacheKey, this)
    }

    private val diskCacheKey
        get() = options.diskCacheKey
            ?: "${fileModel.path}?id=${serverModel.id}&lastModifier=${fileModel.lastModifier}&thumbnail".encodeUtf8()
                .sha256().hex()

    private val fileSystem get() = diskCache!!.fileSystem

    suspend fun fileReader(serverModel: ServerModel, fileModel: FileModel): FileReader? {
        val fileClient = fileClientFactory.create(serverModel)
        return if (fileClient.exists(fileModel)) {
            fileReaderFactory.create(fileClient, fileModel)
        } else {
            null
        }
    }

    class Factory @AssistedInject constructor(
        @ThumbnailDiskCache private val diskCache: dagger.Lazy<DiskCache?>,
        @ApplicationContext private val context: Context,
        private val fileModelLocalDataSource: FileModelLocalDataSource,
        private val fileClientFactory: FileClientFactory,
        private val fileReaderFactory: FileReaderFactory,
        @Assisted private val folderThumbnailOrder: suspend () -> FolderThumbnailOrderModel
    ) : RemoteFileThumbnailFetcherFactory {

        @AssistedFactory
        interface Factory : RemoteFileThumbnailFetcherFactory.Factory {
            override fun create(folderThumbnailOrder: suspend () -> FolderThumbnailOrderModel): RemoteFileThumbnailFetcher.Factory
        }

        override fun create(
            data: ServerFileModel,
            options: Options,
            imageLoader: ImageLoader
        ): Fetcher {
            return RemoteFileThumbnailFetcher(
                data,
                options,
                diskCache,
                context,
                fileModelLocalDataSource,
                fileClientFactory,
                fileReaderFactory,
                folderThumbnailOrder
            )
        }
    }
}

private val COMPRESS_FORMAT =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) Bitmap.CompressFormat.WEBP_LOSSY else Bitmap.CompressFormat.JPEG
