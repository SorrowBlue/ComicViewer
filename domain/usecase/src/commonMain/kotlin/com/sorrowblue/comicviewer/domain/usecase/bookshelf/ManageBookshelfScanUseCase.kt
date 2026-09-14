/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.bookshelf

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.repository.bookshelf.BookshelfScanManager
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow

/**
 * Manage bookshelf scanning tasks and their execution status.
 */
interface ManageBookshelfScanUseCase {

    /**
     * Observe whether a file scan is currently running for the specified bookshelf.
     *
     * @param bookshelfId target bookshelf ID
     * @return flow of boolean indicating if file scanning is active
     */
    fun isScanningFile(bookshelfId: BookshelfId): Flow<Boolean>

    /**
     * Observe whether a thumbnail generation scan is currently running for the specified bookshelf.
     *
     * @param bookshelfId target bookshelf ID
     * @return flow of boolean indicating if thumbnail scanning is active
     */
    fun isScanningThumbnail(bookshelfId: BookshelfId): Flow<Boolean>

    /**
     * Request a file scan for the specified bookshelf.
     *
     * @param bookshelfId target bookshelf ID
     */
    fun scanFile(bookshelfId: BookshelfId)

    /**
     * Request thumbnail generation for the specified bookshelf.
     *
     * @param bookshelfId target bookshelf ID
     */
    fun scanThumbnail(bookshelfId: BookshelfId)
}

@Inject
@ContributesBinding(AppScope::class)
internal class ManageBookshelfScanUseCaseImpl(private val scanManager: BookshelfScanManager) :
    ManageBookshelfScanUseCase {

    override fun isScanningFile(bookshelfId: BookshelfId): Flow<Boolean> =
        scanManager.isScanningFile(bookshelfId)

    override fun isScanningThumbnail(bookshelfId: BookshelfId): Flow<Boolean> =
        scanManager.isScanningThumbnail(bookshelfId)

    override fun scanFile(bookshelfId: BookshelfId) {
        scanManager.scanFile(bookshelfId)
    }

    override fun scanThumbnail(bookshelfId: BookshelfId) {
        scanManager.scanThumbnail(bookshelfId)
    }
}
