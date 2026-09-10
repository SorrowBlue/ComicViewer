/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.data.coil.fetcher.folder

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
import com.sorrowblue.comicviewer.data.coil.fetcher.BaseFetcher
import com.sorrowblue.comicviewer.data.coil.fetcher.CacheKeySnapshot
import com.sorrowblue.comicviewer.data.coil.fetcher.CoilMetadata
import com.sorrowblue.comicviewer.domain.model.file.BookFolder
import com.sorrowblue.comicviewer.domain.model.file.FolderThumbnail
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderThumbnailOrder
import com.sorrowblue.comicviewer.domain.repository.BookshelfRepository
import com.sorrowblue.comicviewer.domain.repository.FileRepository
import com.sorrowblue.comicviewer.domain.repository.SettingsRepository
import com.sorrowblue.comicviewer.domain.service.datasource.RemoteDataSource
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ClassKey
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.binding
import kotlinx.coroutines.flow.first
import kotlinx.io.Source

internal class FolderThumbnailFetcher(
    private val data: FolderThumbnail,
    options: Options,
    private val diskCache: Lazy<DiskCache>,
    private val fileRepository: FileRepository,
    private val settingsRepository: SettingsRepository,
    private val bookshelfRepository: BookshelfRepository,
    private val remoteDataSourceFactory: RemoteDataSource.Factory,
) : BaseFetcher<FolderThumbnail, FolderThumbnailMetadata>(data, options, diskCache) {

    override suspend fun doFetch(): FetchResult {
        val snapshot = readFromDiskCache()
        runCatching {
            // Fast path: fetch the image from the disk cache without performing a network request.
            val result = fastPath(snapshot)
            if (result != null) {
                return result
            }

            // Slow path: fetch the image from the network.
            val folderSettings = settingsRepository.folderSettings.first()
            val resolveImageFolder = folderSettings.resolveImageFolder
            val folder =
                checkNotNull(
                    fileRepository.flow(data.bookshelfId, data.path).first(),
                ) { "Folder not found. id: ${data.bookshelfId}, path: ${data.path}" }
            if (folder.parent != "" && resolveImageFolder) {
                val bookshelf =
                    checkNotNull(bookshelfRepository.flow(data.bookshelfId).first()) {
                        "Bookshelf not found. id: ${data.bookshelfId}"
                    }
                val remoteDataSource = remoteDataSourceFactory.create(bookshelf)
                val currentFile =
                    remoteDataSource.file(folder.path, resolveImageFolder = resolveImageFolder)
                if (currentFile is BookFolder) {
                    fileRepository.updateFileType(currentFile)
                }
            }
            val folderThumbnailOrder =
                settingsRepository.folderDisplaySettings.first().folderThumbnailOrder
            val thumbnailCache = getThumbnailCache(folderThumbnailOrder)
            if (thumbnailCache == null) {
                throw CoilRuntimeException("There are no book thumbnails in this folder.")
            } else {
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

    override val diskCacheKey
        get() = options.diskCacheKey ?: "folder:${data.bookshelfId.value}:${data.path}"

    override suspend fun metadata(): FolderThumbnailMetadata {
        val folderThumbnailOrder =
            settingsRepository.folderDisplaySettings.first().folderThumbnailOrder
        val thumbnailCache = getThumbnailCache(folderThumbnailOrder)
        thumbnailCache?.second?.closeQuietly()
        return FolderThumbnailMetadata(
            data.path,
            data.bookshelfId.value,
            data.lastModifier,
            thumbnailCache?.first,
        )
    }

    override fun readFrom(source: Source): FolderThumbnailMetadata =
        CoilMetadata.from<FolderThumbnailMetadata>(source)

    private suspend fun getThumbnailCache(
        folderThumbnailOrder: FolderThumbnailOrder,
    ): CacheKeySnapshot? {
        val thumbnailCache = fileRepository.getCacheKeys(
            data.bookshelfId,
            data.path,
            CachesFetchCount,
            FolderThumbnailOrder.valueOf(folderThumbnailOrder.name),
        )
        if (thumbnailCache.isEmpty()) {
            return null
        }
        return thumbnailCache.firstNotNullOfOrNull { cacheKey ->
            diskCache.value.openSnapshot(cacheKey)?.let {
                cacheKey to it
            } ?: run {
                fileRepository.removeCacheKey(cacheKey)
                null
            }
        } ?: getThumbnailCache(folderThumbnailOrder)
    }

    @ClassKey(FolderThumbnail::class)
    @ContributesIntoMap(AppScope::class, binding = binding<coil3.key.Keyer<*>>())
    class Keyer : coil3.key.Keyer<FolderThumbnail> {
        override fun key(data: FolderThumbnail, options: Options) =
            "folder:${data.bookshelfId.value}:${data.path}"
    }

    @ClassKey(FolderThumbnail::class)
    @ContributesIntoMap(AppScope::class, binding = binding<Fetcher.Factory<*>>())
    class Factory(
        private val lazyCoilDiskCache: Lazy<CoilDiskCache>,
        private val fileRepository: FileRepository,
        private val settingsRepository: SettingsRepository,
        private val bookshelfRepository: BookshelfRepository,
        private val remoteDataSourceFactory: RemoteDataSource.Factory,
    ) : Fetcher.Factory<FolderThumbnail> {
        override fun create(
            data: FolderThumbnail,
            options: Options,
            imageLoader: ImageLoader,
        ): Fetcher = FolderThumbnailFetcher(
            data,
            options,
            lazy { lazyCoilDiskCache.value.thumbnailDiskCache(data.bookshelfId) },
            fileRepository,
            settingsRepository,
            bookshelfRepository,
            remoteDataSourceFactory,
        )
    }
}

private const val CachesFetchCount = 4
