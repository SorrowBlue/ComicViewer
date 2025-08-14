package com.sorrowblue.comicviewer.domain.model.file

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId

data class FolderThumbnail private constructor(
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
