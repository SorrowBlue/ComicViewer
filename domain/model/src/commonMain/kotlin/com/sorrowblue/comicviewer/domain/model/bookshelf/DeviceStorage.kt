/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.model.bookshelf

import com.sorrowblue.comicviewer.domain.model.common.InternalDataApi

/**
 * Represents a local device storage [Bookshelf].
 *
 * @property id The unique identifier of the bookshelf.
 * @property displayName The display name of the bookshelf.
 * @property fileCount The number of files in the bookshelf.
 * @property isDeleted Whether the bookshelf is logically deleted.
 */
data class DeviceStorage private constructor(
    override val id: BookshelfId,
    override val displayName: String,
    override val fileCount: Int,
    override val isDeleted: Boolean,
) : Bookshelf {
    override val type = BookshelfType.DEVICE

    /**
     * Returns a copy of this [DeviceStorage] with the specified [displayName].
     */
    fun copy(displayName: String = this.displayName) = copy(id = id, displayName = displayName)

    companion object {
        /**
         * Creates a new [DeviceStorage] instance with the given [displayName].
         */
        operator fun invoke(displayName: String) = DeviceStorage(
            id = BookshelfId(),
            displayName = displayName,
            fileCount = 0,
            isDeleted = false,
        )

        /**
         * Reconstructs a [DeviceStorage] instance from persisted fields.
         */
        @InternalDataApi
        operator fun invoke(
            id: BookshelfId,
            displayName: String,
            fileCount: Int,
            isDeleted: Boolean,
        ) = DeviceStorage(
            id = id,
            displayName = displayName,
            fileCount = fileCount,
            isDeleted = isDeleted,
        )
    }
}
