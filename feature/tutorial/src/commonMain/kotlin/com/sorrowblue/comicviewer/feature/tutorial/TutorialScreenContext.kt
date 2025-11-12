package com.sorrowblue.comicviewer.feature.tutorial

import com.sorrowblue.comicviewer.domain.usecase.settings.ManagePdfPluginSettingsUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageViewerOperationSettingsUseCase
import com.sorrowblue.comicviewer.framework.ui.ScreenContext
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Scope

@Scope
annotation class TutorialScreenScope

@GraphExtension(TutorialScreenScope::class)
interface TutorialScreenContext : ScreenContext {
    val manageViewerOperationSettingsUseCase: ManageViewerOperationSettingsUseCase
    val managePdfPluginSettingsUseCase: ManagePdfPluginSettingsUseCase

    @ContributesTo(AppScope::class)
    @GraphExtension.Factory
    fun interface Factory {
        fun createTutorialScreenContext(): TutorialScreenContext
    }
}
