package com.sorrowblue.comicviewer.data.coil.fetcher.collection

import coil3.ImageLoader
import coil3.decode.DataSource
import coil3.disk.DiskCache
import coil3.fetch.FetchResult
import coil3.fetch.Fetcher
import coil3.fetch.SourceFetchResult
import coil3.request.Options
import com.sorrowblue.comicviewer.data.coil.CoilRuntimeException
import com.sorrowblue.comicviewer.data.coil.cache.CoilDiskCache
import com.sorrowblue.comicviewer.data.coil.cache.thumbnailDiskCache
import com.sorrowblue.comicviewer.data.coil.closeQuietly
import com.sorrowblue.comicviewer.data.coil.di.CoilScope
import com.sorrowblue.comicviewer.data.coil.fetcher.CacheKeySnapshot
import com.sorrowblue.comicviewer.data.coil.fetcher.CoilMetadata
import com.sorrowblue.comicviewer.data.coil.fetcher.FileFetcher
import com.sorrowblue.comicviewer.domain.model.collection.Collection
import com.sorrowblue.comicviewer.domain.service.datasource.CollectionFileLocalDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.FileLocalDataSource
import dev.zacsweers.metro.ClassKey
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.binding
import logcat.LogPriority
import logcat.logcat
import okio.BufferedSource

internal class CollectionThumbnailFetcher(
    options: Options,
    diskCache: Lazy<DiskCache>,
    private val coilDiskCacheLazy: Lazy<CoilDiskCache>,
    private val data: Collection,
    private val collectionFileLocalDataSource: CollectionFileLocalDataSource,
    private val fileLocalDataSource: FileLocalDataSource,
) : FileFetcher<CollectionThumbnailMetadata>(options, diskCache) {

    @ClassKey(Collection::class)
    @ContributesIntoMap(CoilScope::class, binding = binding<coil3.key.Keyer<*>>())
    class Keyer : coil3.key.Keyer<Collection> {
        override fun key(data: Collection, options: Options) = "collection:${data.id.value}"
    }

    @ClassKey(Collection::class)
    @ContributesIntoMap(CoilScope::class, binding = binding<Fetcher.Factory<*>>())
    class Factory(
        private val diskCache: Lazy<DiskCache>,
        private val coilDiskCacheLazy: Lazy<CoilDiskCache>,
        private val collectionFileLocalDataSource: CollectionFileLocalDataSource,
        private val fileModelLocalDataSource: FileLocalDataSource,
    ) : Fetcher.Factory<Collection> {
        override fun create(data: Collection, options: Options, imageLoader: ImageLoader) =
            CollectionThumbnailFetcher(
                options = options,
                diskCache = diskCache,
                coilDiskCacheLazy = coilDiskCacheLazy,
                data = data,
                collectionFileLocalDataSource = collectionFileLocalDataSource,
                fileLocalDataSource = fileModelLocalDataSource,
            )
    }

    override val diskCacheKey get() = options.diskCacheKey ?: "collection:${data.id.value}"

    override suspend fun metadata(): CollectionThumbnailMetadata {
        val thumbnails = getThumbnailCache()
        thumbnails?.second?.closeQuietly()
        return CollectionThumbnailMetadata(data.id, thumbnails?.first)
    }

    override fun BufferedSource.readMetadata() =
        CoilMetadata.from<CollectionThumbnailMetadata>(this)

    override suspend fun innerFetch(snapshot: DiskCache.Snapshot?): FetchResult {
        val thumbnailCache = getThumbnailCache()
        if (thumbnailCache == null) {
            throw CoilRuntimeException("No thumbnails were generated for this collection file.")
        } else {
            // 応答をディスク キャッシュに書き込み、新しいスナップショットを開きます。
            return SourceFetchResult(
                source = thumbnailCache.second.toImageSource(),
                mimeType = null,
                dataSource = DataSource.DISK,
            )
        }
    }

    private suspend fun getThumbnailCache(): CacheKeySnapshot? {
        val cacheKeyList = collectionFileLocalDataSource.getCacheKeyList(
            data.id,
            CachesFetchCount,
        )
        if (cacheKeyList.isEmpty()) {
            logcat(LogPriority.INFO) { "Not found thumbnail cache.$data" }
            return null
        }
        return cacheKeyList.firstNotNullOfOrNull { (bookshelfId, cacheKey) ->
            coilDiskCacheLazy.value.thumbnailDiskCache(bookshelfId).openSnapshot(cacheKey)?.let {
                cacheKey to it
            } ?: run {
                fileLocalDataSource.removeCacheKey(cacheKey)
                null
            }
        } ?: getThumbnailCache()
    }
}

private const val CachesFetchCount = 4
