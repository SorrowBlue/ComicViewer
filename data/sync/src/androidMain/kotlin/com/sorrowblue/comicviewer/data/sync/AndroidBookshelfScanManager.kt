/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.data.sync

import androidx.work.WorkManager
import com.sorrowblue.comicviewer.data.sync.worker.FileScanWorker
import com.sorrowblue.comicviewer.data.sync.worker.ThumbnailScanWorker
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.service.bookshelf.BookshelfScanManager
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
@Inject
internal class AndroidBookshelfScanManager(private val workManager: WorkManager) :
    BookshelfScanManager {

    override fun isScanningFile(bookshelfId: BookshelfId): Flow<Boolean> =
        FileScanWorker.getWorkInfosFlow(workManager, bookshelfId)
            .map { workInfos -> workInfos.any { !it.state.isFinished } }

    override fun isScanningThumbnail(bookshelfId: BookshelfId): Flow<Boolean> =
        ThumbnailScanWorker.getWorkInfosFlow(workManager, bookshelfId)
            .map { workInfos -> workInfos.any { !it.state.isFinished } }

    override fun scanFile(bookshelfId: BookshelfId) {
        FileScanWorker.enqueueUniqueWork(workManager, bookshelfId)
    }

    override fun scanThumbnail(bookshelfId: BookshelfId) {
        ThumbnailScanWorker.enqueueUniqueWork(workManager, bookshelfId)
    }
}
