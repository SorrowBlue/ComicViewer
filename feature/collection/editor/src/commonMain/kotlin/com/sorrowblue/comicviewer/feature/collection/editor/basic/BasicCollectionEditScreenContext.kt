package com.sorrowblue.comicviewer.feature.collection.editor.basic

import com.sorrowblue.comicviewer.domain.usecase.collection.GetCollectionUseCase
import com.sorrowblue.comicviewer.domain.usecase.collection.PagingCollectionFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.collection.RemoveCollectionFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.collection.UpdateCollectionUseCase
import com.sorrowblue.comicviewer.framework.ui.ScreenContext
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Scope

@Scope
annotation class BasicCollectionEditScreenScope

@GraphExtension(BasicCollectionEditScreenScope::class)
interface BasicCollectionEditScreenContext : ScreenContext {

    val pagingCollectionFileUseCase: PagingCollectionFileUseCase
    val getCollectionUseCase: GetCollectionUseCase
    val updateCollectionUseCase: UpdateCollectionUseCase
    val removeCollectionFileUseCase: RemoveCollectionFileUseCase

    @ContributesTo(AppScope::class)
    @GraphExtension.Factory
    fun interface Factory {
        fun createBasicCollectionEditScreenContext(): BasicCollectionEditScreenContext
    }
}
