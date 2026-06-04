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
import com.sorrowblue.comicviewer.data.coil.fetcher.BaseFetcher
import com.sorrowblue.comicviewer.data.coil.fetcher.CacheKeySnapshot
import com.sorrowblue.comicviewer.data.coil.fetcher.CoilMetadata
import com.sorrowblue.comicviewer.domain.model.collection.Collection
import com.sorrowblue.comicviewer.domain.service.datasource.CollectionFileLocalDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.FileLocalDataSource
import dev.zacsweers.metro.ClassKey
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.binding
import kotlinx.io.Source
import logcat.LogPriority
import logcat.logcat

internal class CollectionThumbnailFetcher(
    private val data: Collection,
    options: Options,
    diskCache: Lazy<DiskCache>,
    private val coilDiskCacheLazy: Lazy<CoilDiskCache>,
    private val collectionFileLocalDataSource: CollectionFileLocalDataSource,
    private val fileLocalDataSource: FileLocalDataSource,
) : BaseFetcher<Collection, CollectionThumbnailMetadata>(data, options, diskCache) {

    override suspend fun doFetch(): FetchResult {
        val snapshot = readFromDiskCache()
        runCatching {
            // Fast path: fetch the image from the disk cache without performing a network request.
            val result = fastPath(snapshot)
            if (result != null) {
                return result
            }
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
        }.onFailure {
            snapshot?.closeQuietly()
        }.getOrThrow()
    }

    override val diskCacheKey get() = options.diskCacheKey ?: "collection:${data.id.value}"

    override suspend fun metadata(): CollectionThumbnailMetadata {
        val thumbnails = getThumbnailCache()
        thumbnails?.second?.closeQuietly()
        return CollectionThumbnailMetadata(data.id, thumbnails?.first)
    }

    override fun readFrom(source: Source): CollectionThumbnailMetadata =
        CoilMetadata.from<CollectionThumbnailMetadata>(source)

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
                data = data,
                options = options,
                diskCache = diskCache,
                coilDiskCacheLazy = coilDiskCacheLazy,
                collectionFileLocalDataSource = collectionFileLocalDataSource,
                fileLocalDataSource = fileModelLocalDataSource,
            )
    }
}

private const val CachesFetchCount = 4
