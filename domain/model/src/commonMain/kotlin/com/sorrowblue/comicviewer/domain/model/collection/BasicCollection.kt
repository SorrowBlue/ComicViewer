/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.model.collection

import com.sorrowblue.comicviewer.domain.model.common.InternalDataApi
import kotlin.time.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * Represents a manually curated [Collection] of files.
 *
 * @property id The unique identifier of the collection.
 * @property name The display name of the collection.
 * @property count The number of items in the collection.
 * @property createdAt The timestamp when the collection was created.
 * @property updatedAt The timestamp when the collection was last updated.
 */
data class BasicCollection private constructor(
    override val id: CollectionId,
    override val name: String,
    override val count: Int,
    override val createdAt: LocalDateTime,
    override val updatedAt: LocalDateTime,
) : Collection {

    /**
     * Returns a copy of this [BasicCollection] with updated [name].
     */
    fun copy(name: String = this.name) = copy(id = id, name = name)

    companion object {
        /**
         * Creates a new [BasicCollection] with the specified [name].
         */
        operator fun invoke(
            name: String,
            clock: Clock = Clock.System,
            timeZone: TimeZone = TimeZone.currentSystemDefault(),
        ): BasicCollection {
            val now = clock.now().toLocalDateTime(timeZone)
            return BasicCollection(
                id = CollectionId(),
                name = name,
                count = 0,
                createdAt = now,
                updatedAt = now,
            )
        }

        /**
         * Reconstructs a [BasicCollection] instance from persisted fields.
         */
        @InternalDataApi
        operator fun invoke(
            id: CollectionId,
            name: String,
            count: Int,
            createdAt: LocalDateTime,
            updatedAt: LocalDateTime,
        ) = BasicCollection(
            id = id,
            name = name,
            count = count,
            createdAt = createdAt,
            updatedAt = updatedAt,
        )
    }
}
