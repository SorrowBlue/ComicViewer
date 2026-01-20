package com.sorrowblue.comicviewer.feature.collection.editor.basic

import com.sorrowblue.comicviewer.domain.usecase.collection.AddCollectionFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.collection.CreateCollectionUseCase
import com.sorrowblue.comicviewer.framework.ui.ScreenContext
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Scope

@Scope
annotation class BasicCollectionCreateScreenScope

@GraphExtension(BasicCollectionCreateScreenScope::class)
interface BasicCollectionCreateScreenContext : ScreenContext {
    val createCollectionUseCase: CreateCollectionUseCase
    val addCollectionFileUseCase: AddCollectionFileUseCase

    @ContributesTo(AppScope::class)
    @GraphExtension.Factory
    fun interface Factory {
        fun createBasicCollectionCreateScreenContext(): BasicCollectionCreateScreenContext
    }
}
