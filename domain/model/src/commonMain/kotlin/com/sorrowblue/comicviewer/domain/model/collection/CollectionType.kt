/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.model.collection

/**
 * Represents the type of a [Collection].
 */
enum class CollectionType {
    /** Dynamic collection populated by search criteria. */
    Smart,

    /** Static collection of manually added files. */
    Basic,

    /** Includes both smart and basic collections. */
    All,
}
