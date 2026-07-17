/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.settings.display.navigation

import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import com.sorrowblue.comicviewer.feature.settings.display.DisplaySettingsScreenRoot
import com.sorrowblue.comicviewer.framework.ui.animation.transitionMaterialSharedAxisX
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import com.sorrowblue.comicviewer.framework.ui.navigation3.NavigationEntry
import kotlinx.serialization.Serializable

@Serializable
data object DisplaySettingsNavKey : NavKey

@NavigationEntry
context(scope: EntryProviderScope<NavKey>)
internal fun displaySettingsNavEntry(navigator: Navigator) {
    scope.entry<DisplaySettingsNavKey>(
        metadata = ListDetailSceneStrategy.detailPane("Settings") +
            NavDisplay.transitionMaterialSharedAxisX(),
    ) {
        DisplaySettingsScreenRoot(
            onBackClick = dropUnlessResumed { navigator.goBack() },
            onDarkModeClick = dropUnlessResumed { navigator.navigate(DarkModeNavKey) },
        )
    }
}
