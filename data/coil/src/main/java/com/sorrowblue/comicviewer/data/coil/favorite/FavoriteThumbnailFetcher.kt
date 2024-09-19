package com.sorrowblue.comicviewer.data.coil.favorite

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
import com.sorrowblue.comicviewer.data.coil.di.ImageCacheDiskCache
import com.sorrowblue.comicviewer.data.coil.from
import com.sorrowblue.comicviewer.domain.model.favorite.Favorite
import com.sorrowblue.comicviewer.domain.service.datasource.FavoriteFileLocalDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.FileLocalDataSource
import dagger.Lazy
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import okio.BufferedSource
import okio.ByteString.Companion.encodeUtf8

internal class FavoriteThumbnailFetcher(
    options: Options,
    diskCache: Lazy<DiskCache?>,
    private val context: Context,
    private val data: Favorite,
    private val favoriteFileLocalDataSource: FavoriteFileLocalDataSource,
    private val fileModelLocalDataSource: FileLocalDataSource,
) : FileFetcher<FavoriteThumbnailMetadata>(options, diskCache) {

    override val diskCacheKey
        get() = options.diskCacheKey ?: "id:${data.id.value}".encodeUtf8().sha256().hex()

    override suspend fun metadata(): FavoriteThumbnailMetadata {
        val thumbnails = getThumbnailCacheList(5)
        thumbnails.forEach { it.second.closeQuietly() }
        return FavoriteThumbnailMetadata(data.id.value, thumbnails.map { it.first })
    }

    override fun BufferedSource.readMetadata() =
        FavoriteThumbnailMetadata.from<FavoriteThumbnailMetadata>(this)

    override suspend fun innerFetch(snapshot: DiskCache.Snapshot?): FetchResult {
        val thumbnails = getThumbnailCacheList(5)
        if (thumbnails.isEmpty()) {
            throw CoilRuntimeException("No thumbnails were generated for this favorite file.")
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
     * @return
     */
    private suspend fun getThumbnailCacheList(maxSize: Int): List<CacheKeySnapshot> {
        val cacheKeyList = favoriteFileLocalDataSource.getCacheKeyList(data.id, 5)
        val notEnough = cacheKeyList.size < maxSize
        val list = cacheKeyList.mapNotNull { (bookshelfId, cacheKey) ->
            CoilDiskCache.thumbnailDiskCache(context, bookshelfId).openSnapshot(cacheKey)?.let {
                cacheKey to it
            } ?: null.apply {
                fileModelLocalDataSource.removeCacheKey(cacheKey)
            }
        }
        return if (notEnough || maxSize <= list.size) {
            list.reversed()
        } else {
            getThumbnailCacheList(maxSize)
        }
    }

    class Factory @Inject constructor(
        @ApplicationContext private val context: Context,
        @ImageCacheDiskCache private val diskCache: Lazy<DiskCache?>,
        private val favoriteFileLocalDataSource: FavoriteFileLocalDataSource,
        private val fileModelLocalDataSource: FileLocalDataSource,
    ) : Fetcher.Factory<Favorite> {

        override fun create(data: Favorite, options: Options, imageLoader: ImageLoader) =
            FavoriteThumbnailFetcher(
                options,
                diskCache,
                context,
                data,
                favoriteFileLocalDataSource,
                fileModelLocalDataSource
            )
    }
}
