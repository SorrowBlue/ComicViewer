package com.sorrowblue.comicviewer.domain.service.datasource

import com.sorrowblue.comicviewer.domain.model.BookshelfImageCacheInfo
import com.sorrowblue.comicviewer.domain.model.ImageCache
import com.sorrowblue.comicviewer.domain.model.OtherImageCache
import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId

interface ImageCacheDataSource {

    suspend fun deleteThumbnails(list: List<String> = emptyList())

    suspend fun clearImageCache()

    fun getBookshelfImageCacheInfo(bookshelf: Bookshelf): Resource<BookshelfImageCacheInfo, Resource.SystemError>
    fun getOtherImageCache(): Resource<OtherImageCache, Resource.SystemError>
    suspend fun clearImageCache(
        bookshelfId: BookshelfId,
        imageCache: ImageCache,
    ): Resource<Unit, Resource.SystemError>
}
