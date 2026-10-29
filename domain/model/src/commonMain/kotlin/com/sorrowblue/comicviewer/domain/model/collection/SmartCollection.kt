/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.model.collection

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.common.InternalDataApi
import com.sorrowblue.comicviewer.domain.model.search.SearchCondition
import kotlin.time.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * Represents a dynamic [Collection] populated based on a [SearchCondition].
 *
 * @property id The unique identifier of the collection.
 * @property name The display name of the collection.
 * @property count The number of items in the collection.
 * @property createdAt The timestamp when the collection was created.
 * @property updatedAt The timestamp when the collection was last updated.
 * @property bookshelfId Optional bookshelf filter for the search condition.
 * @property searchCondition The search condition defining the collection contents.
 */
data class SmartCollection private constructor(
    override val id: CollectionId,
    override val name: String,
    override val count: Int,
    override val createdAt: LocalDateTime,
    override val updatedAt: LocalDateTime,
    val bookshelfId: BookshelfId?,
    val searchCondition: SearchCondition,
) : Collection {

    /**
     * Returns a copy of this [SmartCollection] with updated properties.
     */
    fun copy(
        name: String = this.name,
        bookshelfId: BookshelfId? = this.bookshelfId,
        searchCondition: SearchCondition = this.searchCondition,
    ): SmartCollection = copy(
        id = id,
        name = name,
        updatedAt = updatedAt,
        bookshelfId = bookshelfId,
        searchCondition = searchCondition,
    )

    companion object {
        /**
         * Creates a new [SmartCollection] with the specified search parameters.
         */
        operator fun invoke(
            name: String,
            bookshelfId: BookshelfId?,
            searchCondition: SearchCondition,
            clock: Clock = Clock.System,
            timeZone: TimeZone = TimeZone.currentSystemDefault(),
        ): SmartCollection {
            val now = clock.now().toLocalDateTime(timeZone)
            return SmartCollection(
                id = CollectionId(),
                name = name,
                count = 0,
                createdAt = now,
                updatedAt = now,
                bookshelfId = bookshelfId,
                searchCondition = searchCondition,
            )
        }

        /**
         * Reconstructs a [SmartCollection] instance from persisted fields.
         */
        @InternalDataApi
        operator fun invoke(
            id: CollectionId,
            name: String,
            count: Int,
            createdAt: LocalDateTime,
            updatedAt: LocalDateTime,
            bookshelfId: BookshelfId?,
            searchCondition: SearchCondition,
        ) = SmartCollection(
            id = id,
            name = name,
            count = count,
            createdAt = createdAt,
            updatedAt = updatedAt,
            bookshelfId = bookshelfId,
            searchCondition = searchCondition,
        )
    }
}
