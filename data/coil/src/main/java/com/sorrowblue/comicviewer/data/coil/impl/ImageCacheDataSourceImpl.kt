package com.sorrowblue.comicviewer.data.coil.impl

import android.content.Context
import coil3.disk.DiskCache
import com.sorrowblue.comicviewer.data.coil.di.CoilDiskCache
import com.sorrowblue.comicviewer.data.coil.di.FavoriteThumbnailDiskCache
import com.sorrowblue.comicviewer.domain.model.BookshelfImageCacheInfo
import com.sorrowblue.comicviewer.domain.model.FavoriteImageCacheInfo
import com.sorrowblue.comicviewer.domain.model.ImageCacheInfo
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.service.datasource.BookshelfLocalDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.ImageCacheDataSource
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

internal class ImageCacheDataSourceImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val bookshelfLocalDataSource: BookshelfLocalDataSource,
    @FavoriteThumbnailDiskCache private val favoriteThumbnailDiskCache: dagger.Lazy<DiskCache>,
) : ImageCacheDataSource {

    override suspend fun deleteThumbnails(list: List<String>) {
        val diskCache = favoriteThumbnailDiskCache.get() ?: return
        if (list.isEmpty()) {
            diskCache.clear()
        } else {
            list.forEach {
                diskCache.remove(it)
            }
        }
    }

    override suspend fun clearImageCache(
        bookshelfId: BookshelfId,
        type: BookshelfImageCacheInfo.Type,
    ) {
        when (type) {
            BookshelfImageCacheInfo.Type.Thumbnail ->
                CoilDiskCache.thumbnailDiskCache(context, bookshelfId)

            BookshelfImageCacheInfo.Type.Page ->
                CoilDiskCache.pageDiskCache(context, bookshelfId)
        }.clear()
    }

    override suspend fun clearImageCache() {
        favoriteThumbnailDiskCache.get().clear()
    }

    override suspend fun getImageCacheInfo(): List<ImageCacheInfo> {
        return bookshelfLocalDataSource.allBookshelf().flatMap {
            listOf(
                CoilDiskCache.thumbnailDiskCache(context, it.id).let { diskCache ->
                    BookshelfImageCacheInfo(
                        it,
                        BookshelfImageCacheInfo.Type.Thumbnail,
                        diskCache.size,
                        diskCache.maxSize
                    )
                },
                CoilDiskCache.pageDiskCache(context, it.id).let { diskCache ->
                    BookshelfImageCacheInfo(
                        it,
                        BookshelfImageCacheInfo.Type.Page,
                        diskCache.size,
                        diskCache.maxSize
                    )
                }
            )
        } + favoriteThumbnailDiskCache.get().let {
            FavoriteImageCacheInfo(it.size, it.maxSize)
        }
    }
}
