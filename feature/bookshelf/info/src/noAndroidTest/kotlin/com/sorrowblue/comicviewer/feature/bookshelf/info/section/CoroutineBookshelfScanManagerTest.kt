/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.bookshelf.info.section

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.common.InternalDataApi
import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.RegenerateThumbnailsUseCase
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.ScanBookshelfUseCase
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest

@OptIn(InternalDataApi::class)
class CoroutineBookshelfScanManagerTest {

    @Test
    fun scanFile_executesAndCompletes() = runTest {
        var scanCalled = false
        var fileCompleted = false
        val scanUseCase = object : ScanBookshelfUseCase() {
            override suspend fun run(request: Request): Resource<List<File>, Error> {
                scanCalled = true
                return Resource.Success(emptyList())
            }
        }
        val regenUseCase = object : RegenerateThumbnailsUseCase() {
            override suspend fun run(request: Request): Resource<Unit, Error> =
                Resource.Success(Unit)
        }
        val manager = CoroutineBookshelfScanManager(
            scanBookshelfUseCase = scanUseCase,
            regenerateThumbnailsUseCase = regenUseCase,
            onFileScanComplete = { fileCompleted = true },
        )
        val id = BookshelfId(1)

        assertFalse(manager.isScanningFile(id).first())
        manager.scanFile(id)

        while (!fileCompleted) {
            delay(10)
        }

        assertTrue(scanCalled)
        assertTrue(fileCompleted)
        assertFalse(manager.isScanningFile(id).first())
    }

    @Test
    fun scanThumbnail_executesAndCompletes() = runTest {
        var regenCalled = false
        var thumbnailCompleted = false
        val scanUseCase = object : ScanBookshelfUseCase() {
            override suspend fun run(request: Request): Resource<List<File>, Error> =
                Resource.Success(emptyList())
        }
        val regenUseCase = object : RegenerateThumbnailsUseCase() {
            override suspend fun run(request: Request): Resource<Unit, Error> {
                regenCalled = true
                return Resource.Success(Unit)
            }
        }
        val manager = CoroutineBookshelfScanManager(
            scanBookshelfUseCase = scanUseCase,
            regenerateThumbnailsUseCase = regenUseCase,
            onThumbnailScanComplete = { thumbnailCompleted = true },
        )
        val id = BookshelfId(1)

        assertFalse(manager.isScanningThumbnail(id).first())
        manager.scanThumbnail(id)

        while (!thumbnailCompleted) {
            delay(10)
        }

        assertTrue(regenCalled)
        assertTrue(thumbnailCompleted)
        assertFalse(manager.isScanningThumbnail(id).first())
    }
}
