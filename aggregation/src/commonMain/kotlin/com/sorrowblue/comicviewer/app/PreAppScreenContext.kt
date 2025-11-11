package com.sorrowblue.comicviewer.app

import androidx.compose.runtime.Composable
import com.sorrowblue.comicviewer.ComicViewerUIContext
import com.sorrowblue.comicviewer.domain.usecase.settings.LoadSettingsUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageSecuritySettingsUseCase
import com.sorrowblue.comicviewer.feature.authentication.AuthenticationScreenContext
import com.sorrowblue.comicviewer.feature.tutorial.TutorialScreenContext
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import com.sorrowblue.comicviewer.framework.common.require
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import io.github.takahirom.rin.rememberRetained

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

@Composable
context(context: PlatformContext)
internal fun rememberPreAppScreenContext() = rememberRetained {
    context.require<PreAppScreenContext.Factory>().createPreAppScreenContext()
}
