package com.sorrowblue.comicviewer.domain.model

import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf

sealed interface ImageCache {
    val size: Long
    val maxSize: Long
}

data class ThumbnailImageCache(override val size: Long, override val maxSize: Long) : ImageCache

data class BookPageImageCache(override val size: Long, override val maxSize: Long) : ImageCache

data class OtherImageCache(override val size: Long, override val maxSize: Long) : ImageCache

data class BookshelfImageCacheInfo(
    val bookshelf: Bookshelf,
    val thumbnailImageCache: ThumbnailImageCache,
    val bookPageImageCache: BookPageImageCache,
)
