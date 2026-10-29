/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.model.collection

/**
 * Criteria for querying and filtering [Collection] items.
 *
 * @property type The type of collections to include.
 * @property recent Whether to sort collections by most recently updated.
 */
data class CollectionCriteria(
    val type: CollectionType = CollectionType.All,
    val recent: Boolean = false,
)
