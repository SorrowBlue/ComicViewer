/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.bookshelf.info.section

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.ManageBookshelfScanUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

internal class FakeManageBookshelfScanUseCase {

    val scanningFiles = MutableStateFlow<Set<BookshelfId>>(emptySet())
    val scanningThumbnails = MutableStateFlow<Set<BookshelfId>>(emptySet())
    var scanFileCalled = false
    var scanThumbnailCalled = false

    val useCase: ManageBookshelfScanUseCase = ManageBookshelfScanUseCase(
        isScanningFileAction = { bookshelfId -> scanningFiles.map { bookshelfId in it } },
        isScanningThumbnailAction = { bookshelfId -> scanningThumbnails.map { bookshelfId in it } },
        scanFileAction = { bookshelfId ->
            scanFileCalled = true
            scanningFiles.value += bookshelfId
        },
        scanThumbnailAction = { bookshelfId ->
            scanThumbnailCalled = true
            scanningThumbnails.value += bookshelfId
        },
    )

    fun isScanningFile(bookshelfId: BookshelfId): Flow<Boolean> =
        useCase.isScanningFile(bookshelfId)

    fun isScanningThumbnail(bookshelfId: BookshelfId): Flow<Boolean> =
        useCase.isScanningThumbnail(bookshelfId)

    fun scanFile(bookshelfId: BookshelfId) {
        useCase.scanFile(bookshelfId)
    }

    fun scanThumbnail(bookshelfId: BookshelfId) {
        useCase.scanThumbnail(bookshelfId)
    }
}
