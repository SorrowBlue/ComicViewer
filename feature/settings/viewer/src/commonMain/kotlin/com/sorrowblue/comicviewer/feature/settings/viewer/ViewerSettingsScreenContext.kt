package com.sorrowblue.comicviewer.feature.settings.viewer

import com.sorrowblue.comicviewer.domain.usecase.settings.ManageViewerSettingsUseCase
import com.sorrowblue.comicviewer.framework.ui.ScreenContext
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Scope

@Scope
annotation class ViewerSettingsScreenScope

@GraphExtension(ViewerSettingsScreenScope::class)
interface ViewerSettingsScreenContext : ScreenContext {
    val manageViewerSettingsUseCase: ManageViewerSettingsUseCase

    @ContributesTo(AppScope::class)
    @GraphExtension.Factory
    fun interface Factory {
        fun createViewerSettingsScreenContext(): ViewerSettingsScreenContext
    }
}
