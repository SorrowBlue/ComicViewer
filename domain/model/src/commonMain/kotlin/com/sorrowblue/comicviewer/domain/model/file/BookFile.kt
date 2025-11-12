package com.sorrowblue.comicviewer.domain.model.file

import com.sorrowblue.comicviewer.domain.model.InternalDataApi
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.extension

data class BookFile private constructor(
    override val bookshelfId: BookshelfId,
    override val name: String,
    override val parent: String,
    override val path: String,
    override val size: Long,
    override val lastModifier: Long,
    override val isHidden: Boolean,
    override val cacheKey: String,
    override val lastPageRead: Int,
    override val totalPageCount: Int,
    override val lastReadTime: Long,
    override val sortIndex: Int,
) : Book {
    val extension get() = name.extension

    fun copy(sortIndex: Int = this.sortIndex, totalPageCount: Int = this.totalPageCount) = copy(
        bookshelfId = bookshelfId,
        totalPageCount = totalPageCount,
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
            cacheKey: String = "",
            lastPageRead: Int = 0,
            totalPageCount: Int = 0,
            lastReadTime: Long = 0,
            sortIndex: Int = -1,
        ) = BookFile(
            bookshelfId = bookshelfId,
            name = name,
            parent = parent,
            path = path,
            size = size,
            lastModifier = lastModifier,
            isHidden = isHidden,
            cacheKey = cacheKey,
            lastPageRead = lastPageRead,
            totalPageCount = totalPageCount,
            lastReadTime = lastReadTime,
            sortIndex = sortIndex,
        )
    }
}
