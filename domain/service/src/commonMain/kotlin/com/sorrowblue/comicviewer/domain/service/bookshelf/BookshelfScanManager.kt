/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.service.bookshelf

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import kotlinx.coroutines.flow.Flow

/**
 * Manages bookshelf scanning and observation.
 */
interface BookshelfScanManager {

    /**
     * Observes whether the bookshelf file scan is currently running.
     *
     * @param bookshelfId Target bookshelf ID
     * @return Flow emitting true when scan is in progress
     */
    fun isScanningFile(bookshelfId: BookshelfId): Flow<Boolean>

    /**
     * Observes whether the bookshelf thumbnail scan is currently running.
     *
     * @param bookshelfId Target bookshelf ID
     * @return Flow emitting true when scan is in progress
     */
    fun isScanningThumbnail(bookshelfId: BookshelfId): Flow<Boolean>

    /**
     * Requests a background scan for the specified bookshelf files.
     *
     * @param bookshelfId Target bookshelf ID
     */
    fun scanFile(bookshelfId: BookshelfId)

    /**
     * Requests a background scan for the specified bookshelf thumbnails.
     *
     * @param bookshelfId Target bookshelf ID
     */
    fun scanThumbnail(bookshelfId: BookshelfId)
}
