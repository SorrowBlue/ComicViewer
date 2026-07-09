/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.model.collection

data class CollectionCriteria(
    val type: CollectionType = CollectionType.All,
    val recent: Boolean = false,
)
