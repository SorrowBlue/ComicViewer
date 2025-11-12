package com.sorrowblue.comicviewer.feature.collection.editor.smart

import com.sorrowblue.comicviewer.domain.usecase.bookshelf.FlowBookshelfListUseCase
import com.sorrowblue.comicviewer.domain.usecase.collection.CreateCollectionUseCase
import com.sorrowblue.comicviewer.framework.ui.ScreenContext
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Scope

@Scope
annotation class SmartCollectionCreateScreenScope

@GraphExtension(SmartCollectionCreateScreenScope::class)
interface SmartCollectionCreateScreenContext : ScreenContext {
    val flowBookshelfListUseCase: FlowBookshelfListUseCase
    val createCollectionUseCase: CreateCollectionUseCase

    @ContributesTo(AppScope::class)
    @GraphExtension.Factory
    fun interface Factory {
        fun createSmartCollectionCreateScreenContext(): SmartCollectionCreateScreenContext
    }
}
