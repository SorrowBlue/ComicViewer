package com.sorrowblue.comicviewer.feature.settings.security

import com.sorrowblue.comicviewer.domain.usecase.settings.ManageSecuritySettingsUseCase
import com.sorrowblue.comicviewer.framework.ui.ScreenContext
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Scope

@Scope
annotation class SecuritySettingsScreenScope

@GraphExtension(SecuritySettingsScreenScope::class)
interface SecuritySettingsScreenContext : ScreenContext {
    val manageSecuritySettingsUseCase: ManageSecuritySettingsUseCase

    @ContributesTo(AppScope::class)
    @GraphExtension.Factory
    fun interface Factory {
        fun createSecuritySettingsScreenContext(): SecuritySettingsScreenContext
    }
}
