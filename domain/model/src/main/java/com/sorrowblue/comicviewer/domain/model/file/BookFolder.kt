package com.sorrowblue.comicviewer.domain.model.file

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import kotlinx.parcelize.Parcelize

@Parcelize
data class BookFolder(
    override val bookshelfId: BookshelfId,
    override val name: String,
    override val parent: String,
    override val path: String,
    override val size: Long,
    override val lastModifier: Long,
    override val isHidden: Boolean,
    override val cacheKey: String = "",
    override val lastPageRead: Int = 0,
    override val totalPageCount: Int = 0,
    override val lastReadTime: Long = 0,
    override val params: Map<String, String?> = emptyMap(),
    override val count: Int = 0,
    override val sortIndex: Int = -1,
) : Book, IFolder {

    override fun areContentsTheSame(file: File): Boolean {
        return if (file is BookFolder) {
            bookshelfId == file.bookshelfId && path == file.path && lastPageRead == file.lastPageRead && lastReadTime == file.lastReadTime
        } else {
            false
        }
    }
}
