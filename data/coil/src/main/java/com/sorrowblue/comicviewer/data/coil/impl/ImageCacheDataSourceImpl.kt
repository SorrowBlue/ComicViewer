package com.sorrowblue.comicviewer.data.coil.impl

import android.content.Context
import coil3.disk.DiskCache
import com.sorrowblue.comicviewer.data.coil.di.CoilDiskCache
import com.sorrowblue.comicviewer.data.coil.di.ImageCacheDiskCache
import com.sorrowblue.comicviewer.domain.model.BookPageImageCache
import com.sorrowblue.comicviewer.domain.model.BookshelfImageCacheInfo
import com.sorrowblue.comicviewer.domain.model.ImageCache
import com.sorrowblue.comicviewer.domain.model.OtherImageCache
import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.ThumbnailImageCache
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.service.datasource.ImageCacheDataSource
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

internal class ImageCacheDataSourceImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    @ImageCacheDiskCache private val imageCacheDiskCache: dagger.Lazy<DiskCache>,
) : ImageCacheDataSource {

    override suspend fun deleteThumbnails(list: List<String>) {
        val diskCache = imageCacheDiskCache.get() ?: return
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
            val thumbnailImageCache = CoilDiskCache.thumbnailDiskCache(context, bookshelf.id).let {
                ThumbnailImageCache(it.size, it.maxSize)
            }
            val bookPageImageCache = CoilDiskCache.pageDiskCache(context, bookshelf.id).let {
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
            val diskCache = imageCacheDiskCache.get()
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
                is BookPageImageCache -> CoilDiskCache.pageDiskCache(context, bookshelfId).clear()
                is OtherImageCache -> imageCacheDiskCache.get().clear()
                is ThumbnailImageCache -> CoilDiskCache.thumbnailDiskCache(context, bookshelfId)
                    .clear()
            }
        }.fold(
            onSuccess = { Resource.Success(Unit) },
            onFailure = { Resource.Error(Resource.SystemError(it)) }
        )
    }

    override suspend fun clearImageCache() {
        imageCacheDiskCache.get().clear()
    }
}
