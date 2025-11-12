package com.sorrowblue.comicviewer.feature.settings.display

import com.sorrowblue.comicviewer.domain.usecase.settings.ManageDisplaySettingsUseCase
import com.sorrowblue.comicviewer.framework.ui.ScreenContext
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Scope

@Scope
annotation class DarkModeScreenScope

@GraphExtension(DarkModeScreenScope::class)
interface DarkModeScreenContext : ScreenContext {
    val displaySettingsUseCase: ManageDisplaySettingsUseCase

    @ContributesTo(AppScope::class)
    @GraphExtension.Factory
    fun interface Factory {
        fun createDarkModeScreenContext(): DarkModeScreenContext
    }
}
