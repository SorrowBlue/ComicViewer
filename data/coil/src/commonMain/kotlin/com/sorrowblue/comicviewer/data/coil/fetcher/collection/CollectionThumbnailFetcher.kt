/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

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
import com.sorrowblue.comicviewer.data.coil.fetcher.toImageSource
import com.sorrowblue.comicviewer.domain.model.collection.Collection
import com.sorrowblue.comicviewer.domain.repository.CollectionFileRepository
import com.sorrowblue.comicviewer.domain.repository.FileRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ClassKey
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.binding
import logcat.LogPriority
import logcat.logcat

internal class CollectionThumbnailFetcher(
    private val data: Collection,
    private val options: Options,
    private val coilDiskCacheLazy: Lazy<CoilDiskCache>,
    private val collectionFileRepository: CollectionFileRepository,
    private val fileRepository: FileRepository,
) : Fetcher {

    private val diskCacheKey: String
        get() = options.diskCacheKey ?: "collection:${data.id.value}"

    override suspend fun fetch(): FetchResult {
        val thumbnailCache = getThumbnailCache()
            ?: throw CoilRuntimeException("No thumbnails were generated for this collection file.")
        return SourceFetchResult(
            source = thumbnailCache.toImageSource(diskCacheKey),
            mimeType = null,
            dataSource = DataSource.DISK,
        )
    }

    private suspend fun getThumbnailCache(): DiskCache.Snapshot? {
        val cacheKeyList = collectionFileRepository.getCacheKeyList(
            data.id,
            CachesFetchCount,
        )
        if (cacheKeyList.isEmpty()) {
            logcat(LogPriority.INFO) { "Not found thumbnail cache.$data" }
            return null
        }
        return cacheKeyList.firstNotNullOfOrNull { (bookshelfId, cacheKey) ->
            coilDiskCacheLazy.value.thumbnailDiskCache(bookshelfId).openSnapshot(cacheKey) ?: run {
                fileRepository.removeCacheKey(cacheKey)
                null
            }
        } ?: getThumbnailCache()
    }

    @ClassKey(Collection::class)
    @ContributesIntoMap(AppScope::class, binding = binding<coil3.key.Keyer<*>>())
    class Keyer : coil3.key.Keyer<Collection> {
        override fun key(data: Collection, options: Options) = "collection:${data.id.value}"
    }

    @ClassKey(Collection::class)
    @ContributesIntoMap(AppScope::class, binding = binding<Fetcher.Factory<*>>())
    class Factory(
        private val coilDiskCacheLazy: Lazy<CoilDiskCache>,
        private val collectionFileRepository: CollectionFileRepository,
        private val fileRepository: FileRepository,
    ) : Fetcher.Factory<Collection> {
        override fun create(data: Collection, options: Options, imageLoader: ImageLoader) =
            CollectionThumbnailFetcher(
                data = data,
                options = options,
                coilDiskCacheLazy = coilDiskCacheLazy,
                collectionFileRepository = collectionFileRepository,
                fileRepository = fileRepository,
            )
    }
}

private const val CachesFetchCount = 4
