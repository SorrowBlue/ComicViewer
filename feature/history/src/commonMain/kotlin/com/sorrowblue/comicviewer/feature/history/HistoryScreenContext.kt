package com.sorrowblue.comicviewer.feature.history

import com.sorrowblue.comicviewer.domain.usecase.file.ClearAllHistoryUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.PagingHistoryBookUseCase
import com.sorrowblue.comicviewer.framework.ui.ScreenContext
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Scope

@Scope
annotation class HistoryScreenScope

@GraphExtension(HistoryScreenScope::class)
interface HistoryScreenContext : ScreenContext {

    val clearAllHistoryUseCase: ClearAllHistoryUseCase
    val pagingHistoryBookUseCase: PagingHistoryBookUseCase

    @ContributesTo(AppScope::class)
    @GraphExtension.Factory
    fun interface Factory {
        fun createHistoryScreenContext(): HistoryScreenContext
    }
}
