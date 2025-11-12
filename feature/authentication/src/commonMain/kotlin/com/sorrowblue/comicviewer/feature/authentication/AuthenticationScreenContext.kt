package com.sorrowblue.comicviewer.feature.authentication

import com.sorrowblue.comicviewer.domain.usecase.settings.ManageSecuritySettingsUseCase
import com.sorrowblue.comicviewer.framework.ui.ScreenContext
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Scope

@Scope
annotation class AuthenticationScreenScope

@GraphExtension(AuthenticationScreenScope::class)
interface AuthenticationScreenContext : ScreenContext {
    val securitySettingsUseCase: ManageSecuritySettingsUseCase

    @ContributesTo(AppScope::class)
    @GraphExtension.Factory
    fun interface Factory {
        fun createAuthenticationScreenContext(): AuthenticationScreenContext
    }
}
