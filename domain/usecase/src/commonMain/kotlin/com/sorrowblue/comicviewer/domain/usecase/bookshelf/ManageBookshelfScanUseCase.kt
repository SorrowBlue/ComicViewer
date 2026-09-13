/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.bookshelf

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.repository.bookshelf.BookshelfScanManager
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow

/**
 * Manage bookshelf scanning tasks and their execution status.
 */
class ManageBookshelfScanUseCase(
    private val isScanningFileAction: (BookshelfId) -> Flow<Boolean>,
    private val isScanningThumbnailAction: (BookshelfId) -> Flow<Boolean>,
    private val scanFileAction: (BookshelfId) -> Unit,
    private val scanThumbnailAction: (BookshelfId) -> Unit,
) {

    @Inject
    constructor(scanManager: BookshelfScanManager) : this(
        isScanningFileAction = scanManager::isScanningFile,
        isScanningThumbnailAction = scanManager::isScanningThumbnail,
        scanFileAction = scanManager::scanFile,
        scanThumbnailAction = scanManager::scanThumbnail,
    )

    /**
     * Observe whether a file scan is currently running for the specified bookshelf.
     *
     * @param bookshelfId target bookshelf ID
     * @return flow of boolean indicating if file scanning is active
     */
    fun isScanningFile(bookshelfId: BookshelfId): Flow<Boolean> = isScanningFileAction(bookshelfId)

    /**
     * Observe whether a thumbnail generation scan is currently running for the specified bookshelf.
     *
     * @param bookshelfId target bookshelf ID
     * @return flow of boolean indicating if thumbnail scanning is active
     */
    fun isScanningThumbnail(bookshelfId: BookshelfId): Flow<Boolean> =
        isScanningThumbnailAction(bookshelfId)

    /**
     * Request a file scan for the specified bookshelf.
     *
     * @param bookshelfId target bookshelf ID
     */
    fun scanFile(bookshelfId: BookshelfId) {
        scanFileAction(bookshelfId)
    }

    /**
     * Request thumbnail generation for the specified bookshelf.
     *
     * @param bookshelfId target bookshelf ID
     */
    fun scanThumbnail(bookshelfId: BookshelfId) {
        scanThumbnailAction(bookshelfId)
    }
}
