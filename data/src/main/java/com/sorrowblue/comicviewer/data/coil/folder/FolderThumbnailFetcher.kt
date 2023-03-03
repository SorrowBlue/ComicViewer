package com.sorrowblue.comicviewer.data.coil.folder

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import androidx.core.graphics.scale
import coil.ImageLoader
import coil.annotation.ExperimentalCoilApi
import coil.decode.DataSource
import coil.disk.DiskCache
import coil.fetch.FetchResult
import coil.fetch.Fetcher
import coil.fetch.SourceResult
import coil.request.Options
import com.sorrowblue.comicviewer.data.coil.abortQuietly
import com.sorrowblue.comicviewer.data.coil.book.FileModelFetcher
import com.sorrowblue.comicviewer.data.coil.book.thumbnailBitmap
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
import javax.inject.Inject
import kotlin.math.floor
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import okhttp3.internal.closeQuietly
import okio.ByteString.Companion.encodeUtf8

@OptIn(ExperimentalCoilApi::class)
internal class FolderThumbnailFetcher(
    private val folder: FileModel.Folder,
    options: Options,
    diskCache: dagger.Lazy<DiskCache?>,
    private val context: Context,
    private val remoteDataSourceFactory: RemoteDataSource.Factory,
    private val bookshelfLocalDataSource: BookshelfLocalDataSource,
    private val fileModelLocalDataSource: FileModelLocalDataSource,
    private val settingsCommonRepository: SettingsCommonRepository,
) : FileModelFetcher(options, diskCache) {

    override suspend fun fetch(): FetchResult {
        val size = (requestWidth / (requestHeight / 11).toInt()).toInt() - 6
        val folderThumbnailOrder =
            settingsCommonRepository.displaySettings.first().folderThumbnailOrder
        var snapshot = readFromDiskCache()
        try {
            if (snapshot != null) {
                // キャッシュされた画像は手動で追加された可能性が高いため、常にメタデータが空の状態で返されます。
                if (fileSystem.metadata(snapshot.metadata).size == 0L) {
                    return SourceResult(
                        source = snapshot.toImageSource(),
                        mimeType = null,
                        dataSource = DataSource.DISK
                    )
                }
                // サムネイル候補キャッシュを取得
                val thumbnails = fileModelLocalDataSource.getCacheKeys(
                    folder.bookshelfModelId,
                    folder.path,
                    size,
                    FolderThumbnailOrderModel.valueOf(folderThumbnailOrder.name)
                )
                // 候補が適格である場合、キャッシュから候補を返します。
                if (snapshot.toFolderThumbnailMetadata() == FolderThumbnailMetadata(
                        folder.path, folder.bookshelfModelId.value, folder.lastModifier, thumbnails
                    )
                ) {
                    return SourceResult(
                        source = snapshot.toImageSource(),
                        mimeType = null,
                        dataSource = DataSource.DISK
                    )
                }
            }
            try {
                var thumbnails = cacheList(size, folderThumbnailOrder)
                if (thumbnails.isEmpty()) {
                    // キャッシュがない場合、取得する。
                    val bookshelfModel =
                        bookshelfLocalDataSource.get(folder.bookshelfModelId).first()!!
                    val supportExtensions =
                        settingsCommonRepository.folderSettings.first().supportExtension.map(
                            SupportExtension::extension
                        )
                    snapshot = remoteDataSourceFactory.create(bookshelfModel)
                        .listFiles(folder, false) { SortUtil.filter(it, supportExtensions) }
                        .firstOrNull { it is FileModel.File }?.let {
                            var fileReader =
                                remoteDataSourceFactory.create(bookshelfModel).fileReader(it)
                                    ?: throw RuntimeException("FileReaderが取得できない")
                            val bitmap = fileReader.thumbnailBitmap(
                                requestWidth.toInt(), requestHeight.toInt()
                            ) ?: throw RuntimeException("画像を取得できない")
                            writeToDiskCache(
                                snapshot = snapshot, fileReader = fileReader, bitmap = bitmap
                            )
                        } ?: throw RuntimeException("フォルダにファイルなし。")
                    return SourceResult(
                        source = snapshot.toImageSource(),
                        mimeType = null,
                        dataSource = DataSource.DISK
                    )
                } else {
                    // 応答をディスク キャッシュに書き込み、新しいスナップショットを開きます。
                    snapshot = writeToDiskCache(snapshot = snapshot, list = thumbnails)
                    return if (snapshot != null) {
                        SourceResult(
                            source = snapshot.toImageSource(),
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
                }
            } catch (e: Exception) {
                throw e
            }
        } catch (e: Exception) {
            snapshot?.closeQuietly()
            throw e
        }
    }

    private suspend fun cacheList(
        size: Int, folderThumbnailOrder: FolderThumbnailOrder
    ): List<Pair<String, DiskCache.Snapshot>> {
        val cacheKeyList = fileModelLocalDataSource.getCacheKeys(
            folder.bookshelfModelId,
            folder.path,
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

    private suspend fun writeToDiskCache(
        snapshot: DiskCache.Snapshot?, list: List<Pair<String, DiskCache.Snapshot>>
    ): DiskCache.Snapshot? {
        // この応答をキャッシュすることが許可されていない場合は短絡します。
        if (!isCacheable()) {
            snapshot?.closeQuietly()
            return null
        }

        // 新しいエディターを開きます。
        val editor = if (snapshot != null) {
            snapshot.closeAndEdit()
        } else {
            diskCache?.edit(diskCacheKey)
        }

        // このエントリに書き込めない場合は「null」を返します。
        if (editor == null) return null

        try {

            val result = Bitmap.createBitmap(
                requestWidth.toInt(), requestHeight.toInt(), Bitmap.Config.ARGB_8888
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
                    FolderThumbnailMetadata(folder.path,
                        folder.bookshelfModelId.value,
                        folder.lastModifier,
                        list.map { it.first }).writeTo(this)
                }
                fileSystem.write(editor.data) {
                    outputStream().use {
                        result.compress(COMPRESS_FORMAT, 75, it)
                    }
                    result.recycle()
                }
                editor.commitAndGet()
            }
        } catch (e: Exception) {
            editor.abortQuietly()
            throw e
        }
    }

    private fun writeToDiskCache(
        snapshot: DiskCache.Snapshot?, fileReader: FileReader, bitmap: Bitmap
    ): DiskCache.Snapshot? {
        // この応答をキャッシュすることが許可されていない場合は短絡します。
        if (!isCacheable()) {
            snapshot?.closeQuietly()
            return null
        }

        // 新しいエディターを開きます。
        val editor = if (snapshot != null) {
            snapshot.closeAndEdit()
        } else {
            diskCache?.edit(diskCacheKey)
        }

        // このエントリに書き込めない場合は「null」を返します。
        if (editor == null) return null

        try {
            // 応答をディスク キャッシュに書き込みます。
            // メタデータと画像データを更新します。
            fileSystem.write(editor.metadata) {
                FolderThumbnailMetadata(
                    folder.path, folder.bookshelfModelId.value, folder.lastModifier, emptyList()
                ).writeTo(this)
            }
            fileSystem.write(editor.data) {
                bitmap.compress(COMPRESS_FORMAT, 75, outputStream())
            }
            return editor.commitAndGet()
        } catch (e: Exception) {
            editor.abortQuietly()
            throw e
        } finally {
            bitmap.recycle()
            fileReader.closeQuietly()
        }
    }

    private fun combineThumbnail(canvas: Canvas, snap: DiskCache.Snapshot, rightSpace: Float) {
        val bitmap = BitmapFactory.decodeFile(snap.data.toString())
        val resizeScale =
            if (bitmap.width >= bitmap.height) requestWidth / bitmap.width else requestHeight / bitmap.height
        val scale = bitmap.scale(
            (bitmap.width * resizeScale).toInt(), (bitmap.height * resizeScale).toInt(), true
        )
        bitmap.recycle()
        canvas.drawBitmap(scale, requestWidth - scale.width - rightSpace, 0f, null)
        scale.recycle()
    }

    override val diskCacheKey
        get() = options.diskCacheKey
            ?: "${folder.path}:${folder.bookshelfModelId.value}:${folder.lastModifier}".encodeUtf8()
                .sha256().hex()

    private fun DiskCache.Snapshot.toFolderThumbnailMetadata(): FolderThumbnailMetadata? {
        return try {
            fileSystem.read(metadata) {
                FolderThumbnailMetadata.from(this)
            }
        } catch (_: Exception) {
            // メタデータを解析できない場合は、このエントリを無視してください。
            null
        }
    }

    class Factory @Inject constructor(
        @ThumbnailDiskCache private val diskCache: dagger.Lazy<DiskCache?>,
        @ApplicationContext private val context: Context,
        private val remoteDataSourceFactory: RemoteDataSource.Factory,
        private val bookshelfLocalDataSource: BookshelfLocalDataSource,
        private val fileModelLocalDataSource: FileModelLocalDataSource,
        private val settingsCommonRepository: SettingsCommonRepository,
    ) : Fetcher.Factory<FileModel.Folder> {

        override fun create(
            data: FileModel.Folder, options: Options, imageLoader: ImageLoader
        ): Fetcher {
            return FolderThumbnailFetcher(
                data,
                options,
                diskCache,
                context,
                remoteDataSourceFactory,
                bookshelfLocalDataSource,
                fileModelLocalDataSource,
                settingsCommonRepository
            )
        }
    }
}
