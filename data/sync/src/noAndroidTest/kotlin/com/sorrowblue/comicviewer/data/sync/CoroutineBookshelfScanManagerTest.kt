/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.data.sync

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.common.InternalDataApi
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest

@OptIn(InternalDataApi::class)
class CoroutineBookshelfScanManagerTest {

    @Test
    fun scanFile_executesAndCompletes() = runTest {
        var scanCalled = false
        var fileCompleted = false
        val manager = CoroutineBookshelfScanManager(
            scanBookshelf = {
                scanCalled = true
            },
            onFileScanComplete = { fileCompleted = true },
        )
        val id = BookshelfId(1)

        assertFalse(manager.isScanningFile(id).first())
        manager.scanFile(id)

        while (!fileCompleted || manager.isScanningFile(id).first()) {
            delay(10.milliseconds)
        }

        assertTrue(scanCalled)
        assertTrue(fileCompleted)
        assertFalse(manager.isScanningFile(id).first())
    }

    @Test
    fun scanThumbnail_executesAndCompletes() = runTest {
        var regenCalled = false
        var thumbnailCompleted = false
        val manager = CoroutineBookshelfScanManager(
            regenerateThumbnails = {
                regenCalled = true
            },
            onThumbnailScanComplete = { thumbnailCompleted = true },
        )
        val id = BookshelfId(1)

        assertFalse(manager.isScanningThumbnail(id).first())
        manager.scanThumbnail(id)

        while (!thumbnailCompleted || manager.isScanningThumbnail(id).first()) {
            delay(10.milliseconds)
        }

        assertTrue(regenCalled)
        assertTrue(thumbnailCompleted)
        assertFalse(manager.isScanningThumbnail(id).first())
    }
}
