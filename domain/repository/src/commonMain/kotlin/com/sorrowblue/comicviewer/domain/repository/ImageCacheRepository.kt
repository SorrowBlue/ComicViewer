/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.repository

import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.cache.BookshelfImageCacheInfo
import com.sorrowblue.comicviewer.domain.model.cache.ImageCache
import com.sorrowblue.comicviewer.domain.model.cache.OtherImageCache
import com.sorrowblue.comicviewer.domain.model.common.Resource

/**
 * Repository interface for inspecting and clearing image caches (thumbnails and book page caches).
 */
interface ImageCacheRepository {
    /**
     * Deletes cached thumbnail files corresponding to the given [list] of cache keys,
     * or all thumbnails if [list] is empty.
     */
    suspend fun deleteThumbnails(list: List<String> = emptyList())

    /**
     * Clears all cached images across all bookshelves and temporary caches.
     */
    suspend fun clearImageCache()

    /**
     * Retrieves image cache statistics for the specified [bookshelf].
     */
    fun getBookshelfImageCacheInfo(
        bookshelf: Bookshelf,
    ): Resource<BookshelfImageCacheInfo, Resource.SystemError>

    /**
     * Retrieves statistics for image caches not tied to a specific bookshelf.
     */
    fun getOtherImageCache(): Resource<OtherImageCache, Resource.SystemError>

    /**
     * Clears the specified [imageCache] associated with [bookshelfId].
     */
    suspend fun clearImageCache(
        bookshelfId: BookshelfId,
        imageCache: ImageCache,
    ): Resource<Unit, Resource.SystemError>
}
