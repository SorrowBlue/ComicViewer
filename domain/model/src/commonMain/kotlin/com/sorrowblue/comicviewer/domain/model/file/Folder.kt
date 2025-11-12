package com.sorrowblue.comicviewer.domain.model.file

import com.sorrowblue.comicviewer.domain.model.InternalDataApi
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId

data class Folder private constructor(
    override val bookshelfId: BookshelfId,
    override val name: String,
    override val parent: String,
    override val path: String,
    override val size: Long,
    override val lastModifier: Long,
    override val isHidden: Boolean,
    override val sortIndex: Int,
    override val cacheKey: String,
    override val count: Int,
) : IFolder {
    fun copy(parent: String = this.parent, sortIndex: Int = this.sortIndex) = copy(
        bookshelfId = bookshelfId,
        parent = parent,
        sortIndex = sortIndex,
    )

    companion object {
        @InternalDataApi
        operator fun invoke(
            bookshelfId: BookshelfId,
            name: String,
            parent: String,
            path: String,
            size: Long,
            lastModifier: Long,
            isHidden: Boolean,
            sortIndex: Int = -1,
            cacheKey: String = "",
            count: Int = 0,
        ) = Folder(
            bookshelfId = bookshelfId,
            name = name,
            parent = parent,
            path = path,
            size = size,
            lastModifier = lastModifier,
            isHidden = isHidden,
            sortIndex = sortIndex,
            cacheKey = cacheKey,
            count = count,
        )
    }
}
