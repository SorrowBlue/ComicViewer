/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.model.file

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId

data class BookThumbnail private constructor(
    override val bookshelfId: BookshelfId,
    override val path: String,
    override val lastModifier: Long,
    override val size: Long,
    val totalPageCount: Int,
) : FileThumbnail {

    companion object {

        fun from(book: Book) = BookThumbnail(
            bookshelfId = book.bookshelfId,
            path = book.path,
            lastModifier = book.lastModifier,
            size = book.size,
            totalPageCount = book.totalPageCount,
        )
    }
}
