package com.sorrowblue.comicviewer.feature.collection

import com.sorrowblue.comicviewer.domain.usecase.collection.GetCollectionUseCase
import com.sorrowblue.comicviewer.domain.usecase.collection.PagingCollectionFileUseCase
import com.sorrowblue.comicviewer.framework.ui.ScreenContext
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Scope

@Scope
annotation class CollectionScreenScope

@GraphExtension(CollectionScreenScope::class)
interface CollectionScreenContext : ScreenContext {

    val pagingCollectionFileUseCase: PagingCollectionFileUseCase
    val getCollectionUseCase: GetCollectionUseCase

    @ContributesTo(AppScope::class)
    @GraphExtension.Factory
    fun interface Factory {
        fun createCollectionScreenContext(): CollectionScreenContext
    }
}
