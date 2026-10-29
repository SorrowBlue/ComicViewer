/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.model.collection

import kotlinx.datetime.LocalDateTime

/**
 * Represents a user-defined or smart collection of files.
 */
sealed interface Collection {
    /** The unique identifier of the collection. */
    val id: CollectionId

    /** The display name of the collection. */
    val name: String

    /** The number of items in the collection. */
    val count: Int

    /** The timestamp when the collection was created. */
    val createdAt: LocalDateTime

    /** The timestamp when the collection was last updated. */
    val updatedAt: LocalDateTime
}
