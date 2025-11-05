package com.sorrowblue.comicviewer.feature.book.receive

import com.sorrowblue.comicviewer.domain.usecase.file.GetIntentBookUseCase
import com.sorrowblue.comicviewer.framework.ui.ScreenContext
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Scope

@Scope
annotation class ReceiveBookScreenScope

@GraphExtension(ReceiveBookScreenScope::class)
interface ReceiveBookScreenContext : ScreenContext {
    val getIntentBookUseCase: GetIntentBookUseCase

    @ContributesTo(AppScope::class)
    @GraphExtension.Factory
    fun interface Factory {
        fun createReceiveBookScreenContext(): ReceiveBookScreenContext
    }
}
