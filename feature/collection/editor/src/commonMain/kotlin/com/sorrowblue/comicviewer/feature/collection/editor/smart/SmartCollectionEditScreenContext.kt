package com.sorrowblue.comicviewer.feature.collection.editor.smart

import com.sorrowblue.comicviewer.domain.usecase.bookshelf.FlowBookshelfListUseCase
import com.sorrowblue.comicviewer.domain.usecase.collection.GetCollectionUseCase
import com.sorrowblue.comicviewer.domain.usecase.collection.UpdateCollectionUseCase
import com.sorrowblue.comicviewer.framework.ui.ScreenContext
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Scope

@Scope
annotation class SmartCollectionEditScreenScope

@GraphExtension(SmartCollectionEditScreenScope::class)
interface SmartCollectionEditScreenContext : ScreenContext {

    val flowBookshelfListUseCase: FlowBookshelfListUseCase
    val updateCollectionUseCase: UpdateCollectionUseCase
    val getCollectionUseCase: GetCollectionUseCase

    @ContributesTo(AppScope::class)
    @GraphExtension.Factory
    fun interface Factory {
        fun createSmartCollectionEditScreenContext(): SmartCollectionEditScreenContext
    }
}
