package com.sorrowblue.comicviewer.domain.model.bookshelf

/**
 * A sealed interface representing a bookshelf.
 *
 * A bookshelf represents a storage entity for managing comic files.
 * This interface allows for different types of bookshelf implementations,
 * such as local storage or network storage (SMB).
 */
sealed interface Bookshelf {

    /**
     * The unique identifier of the bookshelf.
     */
    val id: BookshelfId

    /**
     * The display name of the bookshelf.
     */
    val displayName: String

    /**
     * The number of files in the bookshelf.
     */
    val fileCount: Int

    /**
     * Flag indicating whether the bookshelf is deleted.
     *
     * When `true`, the bookshelf is logically deleted.
     */
    val isDeleted: Boolean

    /**
     * The type of the bookshelf (local, network storage, etc.).
     *
     * When `null`, the type is undefined or not supported.
     */
    val type: BookshelfType?
}
