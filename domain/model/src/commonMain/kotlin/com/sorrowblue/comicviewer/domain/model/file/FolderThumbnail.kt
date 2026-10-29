/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.model.file

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId

/**
 * Thumbnail metadata for a [Folder].
 *
 * @property bookshelfId The identifier of the bookshelf containing the folder.
 * @property path The path of the folder within the bookshelf.
 * @property lastModifier The last modified epoch millisecond timestamp of the folder.
 * @property size The size of the folder in bytes.
 */
data class FolderThumbnail private constructor(
    override val bookshelfId: BookshelfId,
    override val path: String,
    override val lastModifier: Long,
    override val size: Long,
) : FileThumbnail {
    companion object {
        /**
         * Creates a [FolderThumbnail] from the given [folder].
         */
        fun from(folder: Folder) = FolderThumbnail(
            bookshelfId = folder.bookshelfId,
            path = folder.path,
            lastModifier = folder.lastModifier,
            size = folder.size,
        )
    }
}
