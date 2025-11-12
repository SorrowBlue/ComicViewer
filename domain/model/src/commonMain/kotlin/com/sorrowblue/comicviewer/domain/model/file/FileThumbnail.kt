package com.sorrowblue.comicviewer.domain.model.file

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId

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
