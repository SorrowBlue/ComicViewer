/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.model.bookshelf

import com.sorrowblue.comicviewer.domain.model.common.InternalDataApi
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull

@OptIn(InternalDataApi::class)
class BookshelfTest {

    @Test
    fun deviceStorage_defaultFactoryInitializesDefaultsAndType() {
        val storage = DeviceStorage(displayName = "Internal Storage")

        assertEquals(BookshelfId(0), storage.id)
        assertEquals("Internal Storage", storage.displayName)
        assertEquals(0, storage.fileCount)
        assertFalse(storage.isDeleted)
        assertEquals(BookshelfType.DEVICE, storage.type)
    }

    @Test
    fun deviceStorage_copyUpdatesDisplayNameAndPreservesOtherFields() {
        val storage = DeviceStorage(
            id = BookshelfId(7),
            displayName = "Old Name",
            fileCount = 42,
            isDeleted = false,
        )

        val updated = storage.copy(displayName = "New Name")

        assertEquals(BookshelfId(7), updated.id)
        assertEquals("New Name", updated.displayName)
        assertEquals(42, updated.fileCount)
        assertFalse(updated.isDeleted)
        assertEquals(BookshelfType.DEVICE, updated.type)
    }

    @Test
    fun shareContents_hasExpectedStaticPropertiesAndNullType() {
        assertEquals(BookshelfId(0), ShareContents.id)
        assertEquals("ShareContents", ShareContents.displayName)
        assertEquals(0, ShareContents.fileCount)
        assertFalse(ShareContents.isDeleted)
        assertNull(ShareContents.type)
    }
}
