/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.model.file

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.common.InternalDataApi

/**
 * Represents a directory folder within a bookshelf.
 *
 * @property bookshelfId The identifier of the bookshelf containing this folder.
 * @property name The folder name.
 * @property parent The parent folder path.
 * @property path The full path of the folder within the bookshelf.
 * @property size The folder size in bytes.
 * @property lastModifier The last modified epoch millisecond timestamp.
 * @property isHidden Whether the folder is hidden.
 * @property sortIndex The sort position index within the parent folder.
 * @property cacheKey The key used for thumbnail caching.
 * @property count The number of child entries in the folder.
 */
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
    /**
     * Returns a copy of this [Folder] with updated structural or metadata properties.
     */
    fun copy(
        bookshelfId: BookshelfId = this.bookshelfId,
        parent: String = this.parent,
        sortIndex: Int = this.sortIndex,
        size: Long = this.size,
    ) = copy(
        bookshelfId = bookshelfId,
        name = name,
        parent = parent,
        size = size,
        sortIndex = sortIndex,
    )

    companion object {
        /**
         * Creates or reconstructs a [Folder] instance.
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
