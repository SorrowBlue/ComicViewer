package com.sorrowblue.comicviewer.domain.service.datasource

import com.sorrowblue.comicviewer.domain.model.BookshelfImageCacheInfo
import com.sorrowblue.comicviewer.domain.model.ImageCacheInfo
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId

interface ImageCacheDataSource {

    suspend fun deleteThumbnails(list: List<String> = emptyList())
    suspend fun getImageCacheInfo(): List<ImageCacheInfo>
    suspend fun clearImageCache(bookshelfId: BookshelfId, type: BookshelfImageCacheInfo.Type)
    suspend fun clearImageCache()
}
