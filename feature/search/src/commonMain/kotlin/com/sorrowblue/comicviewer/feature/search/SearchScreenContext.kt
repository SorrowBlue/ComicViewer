package com.sorrowblue.comicviewer.feature.search

import com.sorrowblue.comicviewer.domain.usecase.file.PagingQueryFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageFolderDisplaySettingsUseCase
import com.sorrowblue.comicviewer.framework.ui.ScreenContext
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Scope

@Scope
annotation class SearchScreenScope

@GraphExtension(SearchScreenScope::class)
interface SearchScreenContext : ScreenContext {
    val pagingQueryFileUseCase: PagingQueryFileUseCase
    val manageFolderDisplaySettingsUseCase: ManageFolderDisplaySettingsUseCase

    @ContributesTo(AppScope::class)
    @GraphExtension.Factory
    fun interface Factory {
        fun createSearchScreenContext(): SearchScreenContext
    }
}
