/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.model.cache

import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf

/**
 * Represents image cache usage information.
 */
sealed interface ImageCache {
    /** Current cache size in bytes. */
    val size: Long

    /** Maximum allowed cache size in bytes. */
    val maxSize: Long
}

/**
 * Cache usage information for thumbnail images.
 *
 * @property size Current cache size in bytes.
 * @property maxSize Maximum allowed cache size in bytes.
 */
data class ThumbnailImageCache(override val size: Long, override val maxSize: Long) : ImageCache

/**
 * Cache usage information for book page images.
 *
 * @property size Current cache size in bytes.
 * @property maxSize Maximum allowed cache size in bytes.
 */
data class BookPageImageCache(override val size: Long, override val maxSize: Long) : ImageCache

/**
 * Cache usage information for other images.
 *
 * @property size Current cache size in bytes.
 * @property maxSize Maximum allowed cache size in bytes.
 */
data class OtherImageCache(override val size: Long, override val maxSize: Long) : ImageCache

/**
 * Aggregated image cache information for a specific [Bookshelf].
 *
 * @property bookshelf The associated bookshelf.
 * @property thumbnailImageCache Thumbnail image cache usage.
 * @property bookPageImageCache Book page image cache usage.
 */
data class BookshelfImageCacheInfo(
    val bookshelf: Bookshelf,
    val thumbnailImageCache: ThumbnailImageCache,
    val bookPageImageCache: BookPageImageCache,
)
