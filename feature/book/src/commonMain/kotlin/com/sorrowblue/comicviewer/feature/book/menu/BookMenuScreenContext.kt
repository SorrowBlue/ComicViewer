package com.sorrowblue.comicviewer.feature.book.menu

import com.sorrowblue.comicviewer.domain.usecase.settings.ManageBookSettingsUseCase
import com.sorrowblue.comicviewer.framework.ui.ScreenContext
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Scope

@Scope
annotation class BookMenuScreenScope

@GraphExtension(BookMenuScreenScope::class)
interface BookMenuScreenContext : ScreenContext {
    val manageBookSettingsUseCase: ManageBookSettingsUseCase

    @ContributesTo(AppScope::class)
    @GraphExtension.Factory
    fun interface Factory {
        fun createBookMenuScreenContext(): BookMenuScreenContext
    }
}
