package com.sorrowblue.comicviewer.feature.collection.delete

import com.sorrowblue.comicviewer.domain.usecase.collection.DeleteCollectionUseCase
import com.sorrowblue.comicviewer.domain.usecase.collection.GetCollectionUseCase
import com.sorrowblue.comicviewer.framework.ui.ScreenContext
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Scope

@Scope
annotation class DeleteCollectionScreenScope

@GraphExtension(DeleteCollectionScreenScope::class)
interface DeleteCollectionScreenContext : ScreenContext {
    val deleteCollectionUseCase: DeleteCollectionUseCase
    val getCollectionUseCase: GetCollectionUseCase

    @ContributesTo(AppScope::class)
    @GraphExtension.Factory
    fun interface Factory {
        fun createDeleteCollectionScreenContext(): DeleteCollectionScreenContext
    }
}
