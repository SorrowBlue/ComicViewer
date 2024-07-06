package com.sorrowblue.comicviewer.data.coil.folder

import android.content.Context
import coil3.ImageLoader
import coil3.decode.DataSource
import coil3.disk.DiskCache
import coil3.fetch.FetchResult
import coil3.fetch.Fetcher
import coil3.fetch.SourceFetchResult
import coil3.request.Options
import com.sorrowblue.comicviewer.data.coil.CacheKeySnapshot
import com.sorrowblue.comicviewer.data.coil.CoilDecoder
import com.sorrowblue.comicviewer.data.coil.CoilRuntimeException
import com.sorrowblue.comicviewer.data.coil.FileFetcher
import com.sorrowblue.comicviewer.data.coil.book.FileModelFetcher.Companion.COMPRESS_FORMAT
import com.sorrowblue.comicviewer.data.coil.closeQuietly
import com.sorrowblue.comicviewer.data.coil.di.CoilDiskCache
import com.sorrowblue.comicviewer.data.coil.from
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.domain.model.settings.FolderThumbnailOrder
import com.sorrowblue.comicviewer.domain.service.datasource.DatastoreDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.FileLocalDataSource
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import okio.BufferedSource
import okio.ByteString.Companion.encodeUtf8
import okio.use

internal class FolderThumbnailFetcher(
    options: Options,
    diskCache: dagger.Lazy<DiskCache?>,
    private val data: Folder,
    private val fileModelLocalDataSource: FileLocalDataSource,
    private val datastoreDataSource: DatastoreDataSource,
) : FileFetcher<FolderThumbnailMetadata>(options, diskCache) {

    override val diskCacheKey
        get() = options.diskCacheKey
            ?: "${data.path}:${data.bookshelfId.value}:${data.lastModifier}".encodeUtf8()
                .sha256().hex()

    override suspend fun metadata(): FolderThumbnailMetadata {
        val folderThumbnailOrder =
            datastoreDataSource.displaySettings.first().folderThumbnailOrder
        val thumbnails = getThumbnailCacheList(5, folderThumbnailOrder)
        thumbnails.forEach { it.second.closeQuietly() }
        return FolderThumbnailMetadata(
            data.path,
            data.bookshelfId.value,
            data.lastModifier,
            thumbnails.map { it.first }
        )
    }

    override fun BufferedSource.readMetadata() =
        FolderThumbnailMetadata.from<FolderThumbnailMetadata>(this)

    override suspend fun innerFetch(snapshot: DiskCache.Snapshot?): FetchResult {
        val folderThumbnailOrder =
            datastoreDataSource.displaySettings.first().folderThumbnailOrder
        val thumbnails = getThumbnailCacheList(5, folderThumbnailOrder)
        if (thumbnails.isEmpty()) {
            throw CoilRuntimeException("There are no book thumbnails in this folder.")
        } else {
            val pxSize =
                CoilDecoder.calculateMaxThumbnailSize(thumbnails, requestWidth, requestHeight)
            val bitmap = CoilDecoder.createShiftedBitmapFromSnapshots(thumbnails, pxSize)
                ?: throw CoilRuntimeException("Thumbnail generation failed.")
            // 応答をディスク キャッシュに書き込み、新しいスナップショットを開きます。
            return writeToDiskCache(snapshot = snapshot, metaData = metadata()) { sink ->
                bitmap.compress(COMPRESS_FORMAT, 75, sink.outputStream())
                bitmap.recycle()
            }?.use {
                SourceFetchResult(
                    source = it.toImageSource(),
                    mimeType = null,
                    dataSource = DataSource.DISK
                )
            } ?: run {
                throw CoilRuntimeException("Failed to write thumbnail.")
            }
        }
    }

    /**
     * サムネイルキャッシュを取得する
     *
     * @param maxSize サムネイルの最大数
     * @param folderThumbnailOrder 取得するサムネイルの順番
     * @return
     */
    private suspend fun getThumbnailCacheList(
        maxSize: Int,
        folderThumbnailOrder: FolderThumbnailOrder,
    ): List<CacheKeySnapshot> {
        val cacheKeyList = fileModelLocalDataSource.getCacheKeys(
            data.bookshelfId,
            data.path,
            maxSize,
            FolderThumbnailOrder.valueOf(folderThumbnailOrder.name)
        )
        val notEnough = cacheKeyList.size < maxSize
        val list = cacheKeyList.mapNotNull { cacheKey ->
            diskCache?.openSnapshot(cacheKey)?.let {
                cacheKey to it
            } ?: null.apply {
                fileModelLocalDataSource.removeCacheKey(cacheKey)
            }
        }
        return if (notEnough || maxSize <= list.size) {
            list.reversed()
        } else {
            getThumbnailCacheList(maxSize, folderThumbnailOrder)
        }
    }

    class Factory @Inject constructor(
        @ApplicationContext private val context: Context,
        private val fileModelLocalDataSource: FileLocalDataSource,
        private val datastoreDataSource: DatastoreDataSource,
    ) : Fetcher.Factory<Folder> {

        override fun create(data: Folder, options: Options, imageLoader: ImageLoader): Fetcher {
            return FolderThumbnailFetcher(
                options,
                { CoilDiskCache.thumbnailDiskCache(context, data.bookshelfId) },
                data,
                fileModelLocalDataSource,
                datastoreDataSource
            )
        }

    }
}
