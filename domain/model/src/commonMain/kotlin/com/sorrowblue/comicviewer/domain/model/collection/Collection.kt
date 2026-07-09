/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.model.collection

import kotlinx.datetime.LocalDateTime

sealed interface Collection {
    val id: CollectionId
    val name: String
    val count: Int
    val createdAt: LocalDateTime
    val updatedAt: LocalDateTime
}
