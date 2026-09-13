/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.service.interactor.bookshelf

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.service.bookshelf.BookshelfScanManager
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.ManageBookshelfScanUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow

@ContributesBinding(AppScope::class)
@Inject
internal class ManageBookshelfScanInteractor(private val scanManager: BookshelfScanManager) :
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
