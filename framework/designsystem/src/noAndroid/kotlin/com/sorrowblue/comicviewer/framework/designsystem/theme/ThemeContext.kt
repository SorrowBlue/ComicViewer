package com.sorrowblue.comicviewer.framework.designsystem.theme

import com.sorrowblue.comicviewer.domain.usecase.settings.ManageDisplaySettingsUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Scope

@Scope
annotation class ThemeContextScope

@GraphExtension(ThemeContextScope::class)
interface ThemeContext {
    val manageDisplaySettingsUseCase: ManageDisplaySettingsUseCase

    @ContributesTo(AppScope::class)
    @GraphExtension.Factory
    fun interface Factory {
        fun createThemeContext(): ThemeContext
    }
}
