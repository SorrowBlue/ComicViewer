package com.sorrowblue.comicviewer.domain.entity.file

import com.sorrowblue.comicviewer.domain.entity.bookshelf.BookshelfId

data class Folder(
    override val bookshelfId: BookshelfId,
    override val name: String,
    override val parent: String,
    override val path: String,
    override val size: Long,
    override val lastModifier: Long,
    override val params: Map<String, String?> = emptyMap(),
    override val count: Int = 0
) : IFolder {

    override fun areContentsTheSame(file: File): Boolean {
        return if (file is Folder) {
            bookshelfId == file.bookshelfId && path == file.path
        } else {
            false
        }
    }
}

sealed interface IFolder : File {
    val count: Int
}
