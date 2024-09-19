package com.sorrowblue.comicviewer.domain.model.file

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId

sealed interface Book : File {
    val lastPageRead: Int
    val totalPageCount: Int
    val lastReadTime: Long
}

sealed interface FileThumbnail {
    val bookshelfId: BookshelfId
    val path: String
    val lastModifier: Long
    val size: Long

    companion object {
        fun from(file: File) = when (file) {
            is Book -> BookThumbnail.from(file)
            is Folder -> FolderThumbnail.from(file)
        }
    }
}

data class BookThumbnail(
    override val bookshelfId: BookshelfId,
    override val path: String,
    override val lastModifier: Long,
    override val size: Long,
    val totalPageCount: Int,
) : FileThumbnail {

    companion object {
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

data class FolderThumbnail(
    override val bookshelfId: BookshelfId,
    override val path: String,
    override val lastModifier: Long,
    override val size: Long,
) : FileThumbnail {

    companion object {
        fun from(folder: Folder) = FolderThumbnail(
            bookshelfId = folder.bookshelfId,
            path = folder.path,
            lastModifier = folder.lastModifier,
            size = folder.size
        )
    }
}
