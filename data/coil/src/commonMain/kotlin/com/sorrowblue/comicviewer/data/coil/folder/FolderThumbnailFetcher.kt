package com.sorrowblue.comicviewer.data.coil.folder

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
import com.sorrowblue.comicviewer.domain.model.file.FolderThumbnail
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderThumbnailOrder
import com.sorrowblue.comicviewer.domain.service.datasource.DatastoreDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.FileLocalDataSource
import kotlinx.coroutines.flow.first
import logcat.LogPriority
import logcat.logcat
import okio.BufferedSource
import org.koin.core.annotation.Singleton

internal class FolderThumbnailFetcher(
    options: Options,
    private val diskCache: Lazy<DiskCache>,
    private val data: FolderThumbnail,
    private val fileLocalDataSource: FileLocalDataSource,
    private val datastoreDataSource: DatastoreDataSource,
) : FileFetcher<FolderThumbnailMetadata>(options, diskCache) {

    override val diskCacheKey
        get() = options.diskCacheKey ?: "folder:${data.bookshelfId.value}:${data.path}"

    override suspend fun metadata(): FolderThumbnailMetadata {
        val folderThumbnailOrder =
            datastoreDataSource.folderDisplaySettings.first().folderThumbnailOrder
        val thumbnailCache = getThumbnailCache(folderThumbnailOrder)
        thumbnailCache?.second?.closeQuietly()
        return FolderThumbnailMetadata(
            data.path,
            data.bookshelfId.value,
            data.lastModifier,
            thumbnailCache?.first
        )
    }

    override fun BufferedSource.readMetadata() = CoilMetadata.from<FolderThumbnailMetadata>(this)

    override suspend fun innerFetch(snapshot: DiskCache.Snapshot?): FetchResult {
        val folderThumbnailOrder =
            datastoreDataSource.folderDisplaySettings.first().folderThumbnailOrder
        val thumbnailCache = getThumbnailCache(folderThumbnailOrder)
        if (thumbnailCache == null) {
            throw CoilRuntimeException("There are no book thumbnails in this folder.")
        } else {
            return SourceFetchResult(
                source = thumbnailCache.second.toImageSource(),
                mimeType = null,
                dataSource = DataSource.DISK
            )
        }
    }

    private suspend fun getThumbnailCache(folderThumbnailOrder: FolderThumbnailOrder): CacheKeySnapshot? {
        val thumbnailCache = fileLocalDataSource.getCacheKeys(
            data.bookshelfId,
            data.path,
            10,
            FolderThumbnailOrder.valueOf(folderThumbnailOrder.name)
        )
        if (thumbnailCache.isEmpty()) {
            logcat(LogPriority.INFO) { "Not found thumbnail cache.$data" }
            return null
        }
        return thumbnailCache.firstNotNullOfOrNull { cacheKey ->
            diskCache.value.openSnapshot(cacheKey)?.let {
                cacheKey to it
            } ?: run {
                fileLocalDataSource.removeCacheKey(cacheKey)
                null
            }
        } ?: getThumbnailCache(folderThumbnailOrder)
    }
}

@Singleton
@com.sorrowblue.comicviewer.data.coil.FolderThumbnailFetcher
class FolderThumbnailFetcherFactory(
    private val lazyCoilDiskCache: Lazy<CoilDiskCache>,
    private val fileModelLocalDataSource: FileLocalDataSource,
    private val datastoreDataSource: DatastoreDataSource,
) : Fetcher.Factory<FolderThumbnail> {

    override fun create(
        data: FolderThumbnail,
        options: Options,
        imageLoader: ImageLoader,
    ): Fetcher {
        return FolderThumbnailFetcher(
            options,
            lazy { lazyCoilDiskCache.value.thumbnailDiskCache(data.bookshelfId) },
            data,
            fileModelLocalDataSource,
            datastoreDataSource
        )
    }
}
