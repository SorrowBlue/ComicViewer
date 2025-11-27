package com.sorrowblue.comicviewer.feature.bookshelf.info.worker

import com.sorrowblue.comicviewer.domain.usecase.bookshelf.GetBookshelfInfoUseCase
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.ScanBookshelfUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Scope

@Scope
annotation class ScanFileWorkerScope

@GraphExtension(ScanFileWorkerScope::class)
interface ScanFileWorkerContext {
    val getBookshelfInfoUseCase: GetBookshelfInfoUseCase
    val scanBookshelfUseCase: ScanBookshelfUseCase

    @ContributesTo(AppScope::class)
    @GraphExtension.Factory
    fun interface Factory {
        fun createScanFileWorkerContext(): ScanFileWorkerContext
    }
}
