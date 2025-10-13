package com.sorrowblue.comicviewer.data.coil.impl

import coil3.disk.DiskCache
import com.sorrowblue.comicviewer.data.coil.CoilDiskCache
import com.sorrowblue.comicviewer.data.coil.pageDiskCache
import com.sorrowblue.comicviewer.data.coil.thumbnailDiskCache
import com.sorrowblue.comicviewer.domain.model.BookPageImageCache
import com.sorrowblue.comicviewer.domain.model.BookshelfImageCacheInfo
import com.sorrowblue.comicviewer.domain.model.ImageCache
import com.sorrowblue.comicviewer.domain.model.OtherImageCache
import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.ThumbnailImageCache
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.service.datasource.ImageCacheDataSource
import jakarta.inject.Singleton

@Singleton
internal class ImageCacheDataSourceImpl(
    private val lazyCoilDiskCache: Lazy<CoilDiskCache>,
    private val imageCacheDiskCache: Lazy<DiskCache>,
) : ImageCacheDataSource {

    override suspend fun deleteThumbnails(list: List<String>) {
        val diskCache = imageCacheDiskCache.value
        if (list.isEmpty()) {
            diskCache.clear()
        } else {
            list.forEach {
                diskCache.remove(it)
            }
        }
    }

    override fun getBookshelfImageCacheInfo(bookshelf: Bookshelf): Resource<BookshelfImageCacheInfo, Resource.SystemError> {
        return kotlin.runCatching {
            val thumbnailImageCache = lazyCoilDiskCache.value.thumbnailDiskCache(bookshelf.id).let {
                ThumbnailImageCache(it.size, it.maxSize)
            }
            val bookPageImageCache = lazyCoilDiskCache.value.pageDiskCache(bookshelf.id).let {
                BookPageImageCache(it.size, it.maxSize)
            }
            BookshelfImageCacheInfo(bookshelf, thumbnailImageCache, bookPageImageCache)
        }.fold(
            onSuccess = {
                Resource.Success(it)
            },
            onFailure = {
                Resource.Error(Resource.SystemError(it))
            }
        )
    }

    override fun getOtherImageCache(): Resource<OtherImageCache, Resource.SystemError> {
        return kotlin.runCatching {
            val diskCache = imageCacheDiskCache.value
            OtherImageCache(diskCache.size, diskCache.maxSize)
        }.fold(
            onSuccess = { Resource.Success(it) },
            onFailure = { Resource.Error(Resource.SystemError(it)) }
        )
    }

    override suspend fun clearImageCache(
        bookshelfId: BookshelfId,
        imageCache: ImageCache,
    ): Resource<Unit, Resource.SystemError> {
        return kotlin.runCatching {
            when (imageCache) {
                is BookPageImageCache -> lazyCoilDiskCache.value.pageDiskCache(bookshelfId).clear()
                is OtherImageCache -> imageCacheDiskCache.value.clear()
                is ThumbnailImageCache -> lazyCoilDiskCache.value.thumbnailDiskCache(bookshelfId)
                    .clear()
            }
        }.fold(
            onSuccess = { Resource.Success(Unit) },
            onFailure = { Resource.Error(Resource.SystemError(it)) }
        )
    }

    override suspend fun clearImageCache() {
        imageCacheDiskCache.value.clear()
    }
}
