/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.model.collection

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.common.InternalDataApi
import com.sorrowblue.comicviewer.domain.model.search.SearchCondition
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Clock
import kotlin.time.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone

@OptIn(InternalDataApi::class)
class CollectionTest {

    private class FixedClock(private val instant: Instant) : Clock {
        override fun now(): Instant = instant
    }

    @Test
    fun basicCollection_invokeAndCopy_usesDeterministicTimestampsAndUpdatesUpdatedAt() {
        val initialInstant = Instant.fromEpochMilliseconds(1_700_000_000_000L)
        val created = BasicCollection(
            name = "Favorites",
            clock = FixedClock(initialInstant),
            timeZone = TimeZone.UTC,
        )

        val expectedCreatedAt = LocalDateTime(2023, 11, 14, 22, 13, 20)
        assertEquals(expectedCreatedAt, created.createdAt)
        assertEquals(expectedCreatedAt, created.updatedAt)

        val preserved = created.copy(name = "Preserved")
        assertEquals("Preserved", preserved.name)
        assertEquals(expectedCreatedAt, preserved.updatedAt)
    }

    @Test
    fun smartCollection_invokeAndCopy_usesDeterministicTimestampsAndUpdatesUpdatedAt() {
        val initialInstant = Instant.fromEpochMilliseconds(1_700_000_000_000L)
        val created = SmartCollection(
            name = "Smart",
            bookshelfId = BookshelfId(1),
            searchCondition = SearchCondition(),
            clock = FixedClock(initialInstant),
            timeZone = TimeZone.UTC,
        )

        val expectedCreatedAt = LocalDateTime(2023, 11, 14, 22, 13, 20)
        assertEquals(expectedCreatedAt, created.createdAt)
        assertEquals(expectedCreatedAt, created.updatedAt)

        val preserved = created.copy(name = "Smart Preserved")
        assertEquals("Smart Preserved", preserved.name)
        assertEquals(expectedCreatedAt, preserved.updatedAt)
    }
}
