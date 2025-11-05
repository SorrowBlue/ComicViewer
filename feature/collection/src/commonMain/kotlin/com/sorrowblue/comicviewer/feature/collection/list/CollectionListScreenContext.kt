package com.sorrowblue.comicviewer.feature.collection.list

import com.sorrowblue.comicviewer.domain.usecase.collection.PagingCollectionUseCase
import com.sorrowblue.comicviewer.framework.ui.ScreenContext
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Scope

@Scope
annotation class CollectionListScreenScope

@GraphExtension(CollectionListScreenScope::class)
interface CollectionListScreenContext : ScreenContext {

    val pagingCollectionUseCase: PagingCollectionUseCase

    @ContributesTo(AppScope::class)
    @GraphExtension.Factory
    fun interface Factory {
        fun createCollectionListScreenContext(): CollectionListScreenContext
    }
}
