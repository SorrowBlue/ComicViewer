/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.bookshelf.info.section

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.service.bookshelf.BookshelfScanManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

internal class FakeBookshelfScanManager : BookshelfScanManager {

    val scanningFiles = MutableStateFlow<Set<BookshelfId>>(emptySet())
    val scanningThumbnails = MutableStateFlow<Set<BookshelfId>>(emptySet())
    var scanFileCalled = false
    var scanThumbnailCalled = false

    override fun isScanningFile(bookshelfId: BookshelfId): Flow<Boolean> =
        scanningFiles.map { bookshelfId in it }

    override fun isScanningThumbnail(bookshelfId: BookshelfId): Flow<Boolean> =
        scanningThumbnails.map { bookshelfId in it }

    override fun scanFile(bookshelfId: BookshelfId) {
        scanFileCalled = true
        scanningFiles.value += bookshelfId
    }

    override fun scanThumbnail(bookshelfId: BookshelfId) {
        scanThumbnailCalled = true
        scanningThumbnails.value += bookshelfId
    }
}
