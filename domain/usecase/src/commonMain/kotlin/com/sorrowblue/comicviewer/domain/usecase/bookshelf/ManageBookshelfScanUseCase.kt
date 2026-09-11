/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.bookshelf

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
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
