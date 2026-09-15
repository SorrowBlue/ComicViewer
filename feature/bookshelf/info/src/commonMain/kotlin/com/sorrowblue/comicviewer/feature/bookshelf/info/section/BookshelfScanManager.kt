/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.bookshelf.info.section

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import kotlinx.coroutines.flow.Flow

internal interface BookshelfScanManager {

    fun isScanningFile(bookshelfId: BookshelfId): Flow<Boolean>

    fun isScanningThumbnail(bookshelfId: BookshelfId): Flow<Boolean>

    fun scanFile(bookshelfId: BookshelfId)

    fun scanThumbnail(bookshelfId: BookshelfId)
}
