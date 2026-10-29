/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.model.file

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId

/**
 * Base interface for thumbnail metadata associated with a [File].
 */
sealed interface FileThumbnail {
    /** The identifier of the bookshelf containing the file. */
    val bookshelfId: BookshelfId

    /** The full path of the file within the bookshelf. */
    val path: String

    /** The last modified epoch millisecond timestamp of the file. */
    val lastModifier: Long

    /** The size of the file in bytes. */
    val size: Long

    companion object {
        /**
         * Creates a [FileThumbnail] appropriate for the given [file].
         */
        fun from(file: File) = when (file) {
            is Book -> BookThumbnail.from(file)
            is Folder -> FolderThumbnail.from(file)
        }
    }
}
