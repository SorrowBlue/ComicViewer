package com.sorrowblue.comicviewer.data.coil

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
import coil.decode.ImageSource
import coil.disk.DiskCache
import coil.fetch.FetchResult
import coil.fetch.Fetcher
import coil.fetch.SourceResult
import coil.request.Options
import coil.size.pxOrElse
import com.sorrowblue.comicviewer.data.coil.meta.BookThumbnailMetaData
import com.sorrowblue.comicviewer.data.coil.meta.FolderThumbnailMetadata
import com.sorrowblue.comicviewer.data.coil.meta.readBookThumbnailMetaData
import com.sorrowblue.comicviewer.data.coil.meta.readFolderThumbnailMetadata
import com.sorrowblue.comicviewer.data.common.FileModel
import com.sorrowblue.comicviewer.data.common.bookshelf.FolderThumbnailOrderModel
import com.sorrowblue.comicviewer.data.common.util.SortUtil
import com.sorrowblue.comicviewer.data.datasource.BookshelfLocalDataSource
import com.sorrowblue.comicviewer.data.datasource.FileModelLocalDataSource
import com.sorrowblue.comicviewer.data.datasource.RemoteDataSource
import com.sorrowblue.comicviewer.data.di.ThumbnailDiskCache
import com.sorrowblue.comicviewer.data.remote.reader.FileReader
import com.sorrowblue.comicviewer.domain.entity.settings.FolderThumbnailOrder
import com.sorrowblue.comicviewer.domain.model.SupportExtension
import com.sorrowblue.comicviewer.domain.repository.SettingsCommonRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject
import kotlin.math.floor
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import logcat.logcat
import okhttp3.internal.closeQuietly
import okio.Buffer
import okio.ByteString.Companion.encodeUtf8

@OptIn(ExperimentalCoilApi::class)
internal class FileThumbnailFetcher(
    private val data: FileModel,
    private val options: Options,
    diskCacheLazy: dagger.Lazy<DiskCache?>,
    private val context: Context,
    private val bookshelfLocalDataSource: BookshelfLocalDataSource,
    private val fileModelLocalDataSource: FileModelLocalDataSource,
    private val remoteDataSourceFactory: RemoteDataSource.Factory,
    private val settingsCommonRepository: SettingsCommonRepository,
) : CoilFetcher<FileModel>(data, options, diskCacheLazy) {

    class Factory @Inject constructor(
        @ThumbnailDiskCache private val diskCache: dagger.Lazy<DiskCache?>,
        @ApplicationContext private val context: Context,
        private val bookshelfLocalDataSource: BookshelfLocalDataSource,
        private val fileModelLocalDataSource: FileModelLocalDataSource,
        private val remoteDataSourceFactory: RemoteDataSource.Factory,
        private val settingsCommonRepository: SettingsCommonRepository
    ) : Fetcher.Factory<FileModel> {
        override fun create(data: FileModel, options: Options, imageLoader: ImageLoader) =
            FileThumbnailFetcher(
                data,
                options,
                diskCache,
                context,
                bookshelfLocalDataSource,
                fileModelLocalDataSource,
                remoteDataSourceFactory,
                settingsCommonRepository
            )
    }

    private val bookshelfModelId get() = data.bookshelfModelId
    private val fileModel get() = data
    private val requestWidth = options.size.width.pxOrElse { 300 }.toFloat()
    private val requestHeight = options.size.height.pxOrElse { 300 }.toFloat()

    override suspend fun fetch(): FetchResult {
        val snapshot = readFromDiskCache()
        if (fileModel is FileModel.Folder) {
            return fetchFolder(snapshot)
        }
        return fetchFile(snapshot, fileModel)
    }

    private suspend fun fetchFolder(snapshot: DiskCache.Snapshot?): FetchResult {
        val size = (requestWidth / (requestHeight / 11).toInt()).toInt() - 6
        val folderThumbnailOrder =
            settingsCommonRepository.displaySettings.first().folderThumbnailOrder
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
                bookshelfModelId,
                fileModel.path,
                size,
                FolderThumbnailOrderModel.valueOf(folderThumbnailOrder.name)
            )
            // 候補が適格である場合、キャッシュから候補を返します。
            if (snapshot.toFolderThumbnailMetadata()?.thumbnail == list) {
                // 最新の場合
                return SourceResult(
                    source = snapshot.toImageSource(),
                    mimeType = null,
                    dataSource = DataSource.DISK
                )
            }
        }
        try {
            // 未作成・古い場合、サムネイルキャッシュから取得する
            val list = cacheList(size, folderThumbnailOrder)
            if (list.isEmpty()) {
                logcat { "${fileModel.path} is not cache list" }
                // キャッシュがない場合、取得する。
                val bookshelfModel = bookshelfLocalDataSource.get(bookshelfModelId).first()!!
                val supportExtensions =
                    settingsCommonRepository.folderSettings.first().supportExtension.map(
                        SupportExtension::extension
                    )
                remoteDataSourceFactory.create(bookshelfModel).listFiles(fileModel, false) {
                    SortUtil.filter(it, supportExtensions)
                }.firstOrNull { it is FileModel.File }?.let {
                    return fetchFile(null, it)
                }
            }
            val snapshot1 = writeToDiskCacheFolder(snapshot, list)
            return if (snapshot1 != null) {
                SourceResult(
                    source = snapshot1.toImageSource(),
                    mimeType = null,
                    dataSource = DataSource.DISK
                )
            } else {
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

    private suspend fun fetchFile(
        snapshot: DiskCache.Snapshot?,
        fileModel: FileModel
    ): FetchResult {
        var fileReader: FileReader? = null
        try {
            if (snapshot != null) {
                val meta = fileSystem.read(snapshot.metadata) {
                    use {
                        readBookThumbnailMetaData(it)
                    }
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
            val server = bookshelfLocalDataSource.get(bookshelfModelId).first()!!
            fileReader = remoteDataSourceFactory.create(server).fileReader(fileModel)
                ?: throw RuntimeException("この拡張子はサポートされていません。")
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

    private fun DiskCache.Snapshot.toFolderThumbnailMetadata(): FolderThumbnailMetadata? {
        return try {
            fileSystem.read(metadata) {
                use {
                    readFolderThumbnailMetadata(it)
                }
            }
        } catch (_: IOException) {
            // If we can't parse the metadata, ignore this entry.
            null
        }
    }

    private suspend fun cacheList(
        size: Int,
        folderThumbnailOrder: FolderThumbnailOrder
    ): List<Pair<String, DiskCache.Snapshot>> {
        val cacheKeyList = fileModelLocalDataSource.getCacheKeys(
            bookshelfModelId,
            fileModel.path,
            size,
            FolderThumbnailOrderModel.valueOf(folderThumbnailOrder.name)
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

    private suspend fun writeToDiskCacheFolder(
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

        val result = Bitmap.createBitmap(
            requestWidth.toInt(),
            requestHeight.toInt(),
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(result)
        canvas.drawColor(Color.TRANSPARENT)
        val step = floor(requestHeight / 12)
        var left = 0f
        list.reversed().forEach {
            it.second.use { snap ->
                combineThumbnail(canvas, snap, left)
                left += step
            }
        }
        return withContext(NonCancellable) {
            fileSystem.write(editor.metadata) {
                outputStream().use { output ->
                    FolderThumbnailMetadata(list.map { it.first }).write(output)
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

    private fun combineThumbnail(canvas: Canvas, snap: DiskCache.Snapshot, rightSpace: Float) {
        val bitmap = BitmapFactory.decodeFile(snap.data.toString())
        val resizeScale =
            if (bitmap.width >= bitmap.height) requestWidth / bitmap.width else requestHeight / bitmap.height
        val scale = bitmap.scale(
            (bitmap.width * resizeScale).toInt(),
            (bitmap.height * resizeScale).toInt(),
            true
        )
        bitmap.recycle()
        canvas.drawBitmap(scale, requestWidth - scale.width - rightSpace, 0f, null)
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
                    outputStream().use {
                        BookThumbnailMetaData(fileModel.lastModifier, fileModel.size).write(it)
                    }
                }
                fileModelLocalDataSource.update(
                    fileModel.path,
                    bookshelfModelId,
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
                inSampleSize = coil.decode.DecodeUtils
                    .calculateInSampleSize(
                        outWidth,
                        outHeight,
                        requestWidth.toInt(),
                        requestHeight.toInt(),
                        coil.size.Scale.FIT
                    )
                inJustDecodeBounds = false
                pageInputStream(0).use {
                    BitmapFactory.decodeStream(it, null, this)
                }
            }
        }
    }

    override val diskCacheKey
        get() = options.diskCacheKey
            ?: "${fileModel.path}?id=${bookshelfModelId.value}&lastModifier=${fileModel.lastModifier}&thumbnail".encodeUtf8()
                .sha256().hex()

    companion object {
        private val COMPRESS_FORMAT =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) Bitmap.CompressFormat.WEBP_LOSSY else Bitmap.CompressFormat.JPEG
    }
}
