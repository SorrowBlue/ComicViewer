/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.model.file

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId

/**
 * Thumbnail metadata for a [Book].
 *
 * @property bookshelfId The identifier of the bookshelf containing the book.
 * @property path The path of the book within the bookshelf.
 * @property lastModifier The last modified epoch millisecond timestamp of the book.
 * @property size The file size of the book in bytes.
 * @property totalPageCount The total number of pages in the book.
 */
data class BookThumbnail private constructor(
    override val bookshelfId: BookshelfId,
    override val path: String,
    override val lastModifier: Long,
    override val size: Long,
    val totalPageCount: Int,
) : FileThumbnail {

    companion object {

        /**
         * Creates a [BookThumbnail] from the given [book].
         */
        fun from(book: Book) = BookThumbnail(
            bookshelfId = book.bookshelfId,
            path = book.path,
            lastModifier = book.lastModifier,
            size = book.size,
            totalPageCount = book.totalPageCount,
        )
    }
}
