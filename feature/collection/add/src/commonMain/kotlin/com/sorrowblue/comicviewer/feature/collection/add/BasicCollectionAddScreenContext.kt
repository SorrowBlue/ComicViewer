package com.sorrowblue.comicviewer.feature.collection.add

import com.sorrowblue.comicviewer.domain.usecase.collection.AddCollectionFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.collection.PagingCollectionExistUseCase
import com.sorrowblue.comicviewer.domain.usecase.collection.RemoveCollectionFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.CollectionSettingsUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Scope

@Scope
annotation class BasicCollectionAddScreenScope

@GraphExtension(BasicCollectionAddScreenScope::class)
interface BasicCollectionAddScreenContext {

    val removeCollectionFileUseCase: RemoveCollectionFileUseCase
    val addCollectionFileUseCase: AddCollectionFileUseCase
    val collectionSettingsUseCase: CollectionSettingsUseCase
    val pagingCollectionExistUseCase: PagingCollectionExistUseCase

    @ContributesTo(AppScope::class)
    @GraphExtension.Factory
    fun interface Factory {
        fun createBasicCollectionAddScreenContext(): BasicCollectionAddScreenContext
    }
}
