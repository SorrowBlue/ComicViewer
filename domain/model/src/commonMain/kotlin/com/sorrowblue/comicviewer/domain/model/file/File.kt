/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.model.file

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import kotlinx.serialization.Serializable

/**
 * Type alias representing a file or folder path string.
 */
typealias PathString = String

/**
 * Base interface representing a file system entry (either a [Book] or a [Folder]) managed in a bookshelf.
 */
sealed interface File {
    /** The identifier of the bookshelf containing this entry. */
    val bookshelfId: BookshelfId

    /** The display name of the file or folder. */
    val name: String

    /** The parent folder path. */
    val parent: String

    /** The full path of the file or folder within the bookshelf. */
    val path: PathString

    /** The size of the file or folder in bytes. */
    val size: Long

    /** The last modified epoch millisecond timestamp. */
    val lastModifier: Long

    /** Whether the file or folder is hidden. */
    val isHidden: Boolean

    /** The sort position index within the parent folder. */
    val sortIndex: Int

    /** The key used for thumbnail caching. */
    val cacheKey: String

    /**
     * Returns a composite [Key] uniquely identifying this file entry.
     */
    fun key() = Key(bookshelfId, path, parent)

    /**
     * Composite key uniquely identifying a [File].
     *
     * @property bookshelfId The identifier of the bookshelf.
     * @property path The full path of the file.
     * @property parent The parent folder path.
     */
    @Serializable
    data class Key(val bookshelfId: BookshelfId, val path: String, val parent: String)
}
