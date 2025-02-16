package com.sorrowblue.comicviewer.data.coil.favorite

import coil3.ImageLoader
import coil3.decode.DataSource
import coil3.disk.DiskCache
import coil3.fetch.FetchResult
import coil3.fetch.Fetcher
import coil3.fetch.SourceFetchResult
import coil3.request.Options
import com.sorrowblue.comicviewer.data.coil.CacheKeySnapshot
import com.sorrowblue.comicviewer.data.coil.CoilDiskCache
import com.sorrowblue.comicviewer.data.coil.CoilMetadata
import com.sorrowblue.comicviewer.data.coil.CoilRuntimeException
import com.sorrowblue.comicviewer.data.coil.FileFetcher
import com.sorrowblue.comicviewer.data.coil.closeQuietly
import com.sorrowblue.comicviewer.data.coil.thumbnailDiskCache
import com.sorrowblue.comicviewer.domain.model.favorite.Favorite
import com.sorrowblue.comicviewer.domain.service.datasource.FavoriteFileLocalDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.FileLocalDataSource
import logcat.LogPriority
import logcat.logcat
import okio.BufferedSource
import org.koin.core.annotation.Singleton

internal class FavoriteThumbnailFetcher(
    options: Options,
    diskCache: Lazy<DiskCache>,
    private val coilDiskCacheLazy: Lazy<CoilDiskCache>,
    private val data: Favorite,
    private val favoriteFileLocalDataSource: FavoriteFileLocalDataSource,
    private val fileModelLocalDataSource: FileLocalDataSource,
) : FileFetcher<FavoriteThumbnailMetadata>(options, diskCache) {

    override val diskCacheKey get() = options.diskCacheKey ?: "favorite:${data.id.value}"

    override suspend fun metadata(): FavoriteThumbnailMetadata {
        val thumbnails = getThumbnailCache()
        thumbnails?.second?.closeQuietly()
        return FavoriteThumbnailMetadata(data.id.value, thumbnails?.first)
    }

    override fun BufferedSource.readMetadata() = CoilMetadata.from<FavoriteThumbnailMetadata>(this)

    override suspend fun innerFetch(snapshot: DiskCache.Snapshot?): FetchResult {
        val thumbnailCache = getThumbnailCache()
        if (thumbnailCache == null) {
            throw CoilRuntimeException("No thumbnails were generated for this favorite file.")
        } else {
            // 応答をディスク キャッシュに書き込み、新しいスナップショットを開きます。
            return SourceFetchResult(
                source = thumbnailCache.second.toImageSource(),
                mimeType = null,
                dataSource = DataSource.DISK
            )
        }
    }

    private suspend fun getThumbnailCache(): CacheKeySnapshot? {
        val cacheKeyList = favoriteFileLocalDataSource.getCacheKeyList(data.id, 10)
        if (cacheKeyList.isEmpty()) {
            logcat(LogPriority.INFO) { "Not found thumbnail cache.$data" }
            return null
        }
        return cacheKeyList.firstNotNullOfOrNull { (bookshelfId, cacheKey) ->
            coilDiskCacheLazy.value.thumbnailDiskCache(bookshelfId).openSnapshot(cacheKey)?.let {
                cacheKey to it
            } ?: run {
                fileModelLocalDataSource.removeCacheKey(cacheKey)
                null
            }
        } ?: getThumbnailCache()
    }
}

@Singleton
@com.sorrowblue.comicviewer.data.coil.FavoriteFetcher
internal class FavoriteThumbnailFetcherFactory(
    private val diskCache: Lazy<DiskCache>,
    private val coilDiskCacheLazy: Lazy<CoilDiskCache>,
    private val favoriteFileLocalDataSource: FavoriteFileLocalDataSource,
    private val fileModelLocalDataSource: FileLocalDataSource,
) : Fetcher.Factory<Favorite> {

    override fun create(data: Favorite, options: Options, imageLoader: ImageLoader) =
        FavoriteThumbnailFetcher(
            options,
            diskCache,
            coilDiskCacheLazy,
            data,
            favoriteFileLocalDataSource,
            fileModelLocalDataSource
        )
}
