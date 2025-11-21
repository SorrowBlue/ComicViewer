package com.sorrowblue.comicviewer.feature.settings.plugin

import com.sorrowblue.comicviewer.domain.usecase.settings.ManagePdfPluginSettingsUseCase
import com.sorrowblue.comicviewer.framework.ui.ScreenContext
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Scope

@Scope
annotation class PluginScreenScope

@GraphExtension(PluginScreenScope::class)
interface PluginScreenContext : ScreenContext {
    val managePdfPluginSettingsUseCase: ManagePdfPluginSettingsUseCase

    @ContributesTo(AppScope::class)
    @GraphExtension.Factory
    fun interface Factory {
        fun createPluginScreenContext(): PluginScreenContext
    }
}
