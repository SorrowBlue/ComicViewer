package com.sorrowblue.comicviewer.feature.settings.security.navigation

import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import com.sorrowblue.comicviewer.feature.authentication.ScreenType
import com.sorrowblue.comicviewer.feature.authentication.navigation.AuthenticationNavKey
import com.sorrowblue.comicviewer.feature.settings.security.SecuritySettingsScreenContext
import com.sorrowblue.comicviewer.feature.settings.security.SecuritySettingsScreenRoot
import com.sorrowblue.comicviewer.framework.ui.animation.transitionMaterialSharedAxisX
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import io.github.takahirom.rin.rememberRetained
import kotlinx.serialization.Serializable

@Serializable
data object SecuritySettingsNavKey : NavKey

context(factory: SecuritySettingsScreenContext.Factory)
internal fun EntryProviderScope<NavKey>.securitySettingsNavEntry(navigator: Navigator) {
    entry<SecuritySettingsNavKey>(
        metadata = ListDetailSceneStrategy.detailPane("Settings") +
            NavDisplay.transitionMaterialSharedAxisX(),
    ) {
        with(rememberRetained { factory.createSecuritySettingsScreenContext() }) {
            SecuritySettingsScreenRoot(
                onBackClick = navigator::goBack,
                onChangeAuthEnable = {
                    if (it) {
                        navigator.navigate(AuthenticationNavKey(ScreenType.Register))
                    } else {
                        navigator.navigate(AuthenticationNavKey(ScreenType.Erase))
                    }
                },
                onPasswordChangeClick = {
                    navigator.navigate(AuthenticationNavKey(ScreenType.Change))
                },
            )
        }
    }
}
