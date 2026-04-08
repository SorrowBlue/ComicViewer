package com.sorrowblue.comicviewer.feature.bookshelf.info.di

import com.sorrowblue.comicviewer.feature.bookshelf.info.worker.FileScanWorker
import com.sorrowblue.comicviewer.feature.bookshelf.info.worker.ThumbnailScanWorker
import com.sorrowblue.comicviewer.framework.background.MetroWorkerInstanceFactory
import com.sorrowblue.comicviewer.framework.background.WorkerKey
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoMap

@ContributesTo(AppScope::class)
interface AndroidBookshelfInfoModule {

    @WorkerKey(FileScanWorker::class)
    @IntoMap
    @Binds
    private val FileScanWorker.Factory.bind: MetroWorkerInstanceFactory<*> get() = this

    @WorkerKey(ThumbnailScanWorker::class)
    @IntoMap
    @Binds
    private val ThumbnailScanWorker.Factory.bind: MetroWorkerInstanceFactory<*> get() = this
}
