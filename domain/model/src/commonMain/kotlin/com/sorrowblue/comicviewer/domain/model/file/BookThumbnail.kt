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

        fun temporary(bookshelfId: BookshelfId, path: String) =
            BookThumbnail(bookshelfId, path, 0, 0, 0)

        fun from(book: Book) = when (book) {
            is BookFile -> BookThumbnail(
                bookshelfId = book.bookshelfId,
                path = book.path,
                lastModifier = book.lastModifier,
                size = book.size,
                totalPageCount = book.totalPageCount
            )

            is BookFolder -> BookThumbnail(
                bookshelfId = book.bookshelfId,
                path = book.path,
                lastModifier = book.lastModifier,
                size = book.size,
                totalPageCount = book.totalPageCount
            )
        }
    }
}
