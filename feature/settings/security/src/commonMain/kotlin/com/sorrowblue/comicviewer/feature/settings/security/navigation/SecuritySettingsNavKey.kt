/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.settings.security.navigation

import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import com.sorrowblue.comicviewer.feature.authentication.ScreenType
import com.sorrowblue.comicviewer.feature.authentication.navigation.AuthenticationNavKey
import com.sorrowblue.comicviewer.feature.settings.security.SecuritySettingsScreenRoot
import com.sorrowblue.comicviewer.framework.ui.animation.transitionMaterialSharedAxisX
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import com.sorrowblue.comicviewer.framework.ui.navigation3.NavigationEntry
import kotlinx.serialization.Serializable

@Serializable
data object SecuritySettingsNavKey : NavKey

@NavigationEntry
context(scope: EntryProviderScope<NavKey>)
internal fun securitySettingsNavEntry(navigator: Navigator) {
    scope.entry<SecuritySettingsNavKey>(
        metadata = ListDetailSceneStrategy.detailPane("Settings") +
            NavDisplay.transitionMaterialSharedAxisX(),
    ) {
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
