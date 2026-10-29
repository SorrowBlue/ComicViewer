/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.model.file

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.common.InternalDataApi

/**
 * Represents a folder of images treated directly as a [Book].
 *
 * @property bookshelfId The identifier of the bookshelf containing this book folder.
 * @property name The folder name.
 * @property parent The parent folder path.
 * @property path The full path of the folder within the bookshelf.
 * @property size The folder size in bytes.
 * @property lastModifier The last modified epoch millisecond timestamp.
 * @property isHidden Whether the folder is hidden.
 * @property cacheKey The key used for thumbnail caching.
 * @property lastPageRead The zero-based index of the last read page.
 * @property totalPageCount The total number of pages in the book folder.
 * @property lastReadTime The epoch millisecond timestamp when the book folder was last read.
 * @property sortIndex The sort position index within the parent folder.
 * @property count The number of child entries in the folder.
 */
data class BookFolder private constructor(
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
    override val count: Int,
) : Book,
    IFolder {
    /**
     * Returns a copy of this [BookFolder] with updated structural or metadata properties.
     */
    fun copy(
        bookshelfId: BookshelfId = this.bookshelfId,
        parent: String = this.parent,
        size: Long = this.size,
        totalPageCount: Int = this.totalPageCount,
        sortIndex: Int = this.sortIndex,
    ) = copy(
        bookshelfId = bookshelfId,
        name = name,
        parent = parent,
        size = size,
        totalPageCount = totalPageCount,
        sortIndex = sortIndex,
    )

    companion object {
        /**
         * Creates or reconstructs a [BookFolder] instance.
         */
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
            count: Int = 0,
        ) = BookFolder(
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
            count = count,
        )
    }
}
