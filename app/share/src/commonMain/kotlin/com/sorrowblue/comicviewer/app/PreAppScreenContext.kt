package com.sorrowblue.comicviewer.app

import com.sorrowblue.comicviewer.domain.usecase.settings.LoadSettingsUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageSecuritySettingsUseCase
import com.sorrowblue.comicviewer.feature.authentication.AuthenticationScreenContext
import com.sorrowblue.comicviewer.feature.tutorial.TutorialScreenContext
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension

@GraphExtension(RootScreenWrapperScope::class)
interface PreAppScreenContext {
    val loadSettingsUseCase: LoadSettingsUseCase
    val manageSecuritySettingsUseCase: ManageSecuritySettingsUseCase

    val tutorialScreenContext: TutorialScreenContext.Factory
    val authenticationScreenContext: AuthenticationScreenContext.Factory

    @ContributesTo(AppScope::class)
    @GraphExtension.Factory
    fun interface Factory {
        fun createPreAppScreenContext(): PreAppScreenContext
    }
}
