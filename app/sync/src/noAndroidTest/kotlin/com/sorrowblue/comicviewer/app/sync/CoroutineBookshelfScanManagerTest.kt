/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.app.sync

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.common.InternalDataApi
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class, InternalDataApi::class)
class CoroutineBookshelfScanManagerTest {

    @Test
    fun scanFileExecutesAndCompletes() = runTest {
        var scanCalled = false
        var fileCompleted = false
        val manager = CoroutineBookshelfScanManager(
            scanBookshelf = {
                scanCalled = true
            },
            onFileScanComplete = { fileCompleted = true },
            coroutineScope = backgroundScope,
        )
        val id = BookshelfId(1)

        assertFalse(manager.isScanningFile(id).first())
        manager.scanFile(id)
        assertTrue(manager.isScanningFile(id).first())
        runCurrent()

        assertTrue(scanCalled)
        assertTrue(fileCompleted)
        assertFalse(manager.isScanningFile(id).first())
    }

    @Test
    fun scanThumbnailExecutesAndCompletes() = runTest {
        var regenCalled = false
        var thumbnailCompleted = false
        val manager = CoroutineBookshelfScanManager(
            regenerateThumbnails = {
                regenCalled = true
            },
            onThumbnailScanComplete = { thumbnailCompleted = true },
            coroutineScope = backgroundScope,
        )
        val id = BookshelfId(1)

        assertFalse(manager.isScanningThumbnail(id).first())
        manager.scanThumbnail(id)
        assertTrue(manager.isScanningThumbnail(id).first())
        runCurrent()

        assertTrue(regenCalled)
        assertTrue(thumbnailCompleted)
        assertFalse(manager.isScanningThumbnail(id).first())
    }
}
