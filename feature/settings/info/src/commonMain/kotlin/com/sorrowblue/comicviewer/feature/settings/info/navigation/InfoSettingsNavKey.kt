/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.settings.info.navigation

import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import com.sorrowblue.comicviewer.feature.settings.info.InfoSettingsScreenRoot
import com.sorrowblue.comicviewer.feature.tutorial.navigation.TutorialNavKey
import com.sorrowblue.comicviewer.framework.ui.animation.transitionMaterialSharedAxisX
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import com.sorrowblue.comicviewer.framework.ui.navigation3.NavigationEntry
import kotlinx.serialization.Serializable

@Serializable
data object InfoSettingsNavKey : NavKey

@NavigationEntry
context(scope: EntryProviderScope<NavKey>)
internal fun infoSettingsNavEntry(navigator: Navigator) {
    scope.entry<InfoSettingsNavKey>(
        metadata = ListDetailSceneStrategy.detailPane("Settings") +
            NavDisplay.transitionMaterialSharedAxisX(),
    ) {
        InfoSettingsScreenRoot(
            onBackClick = dropUnlessResumed { navigator.goBack() },
            onTutorialClick = dropUnlessResumed { navigator.navigate(TutorialNavKey) },
            onLicenceClick = dropUnlessResumed { navigator.navigate(LicenseNavKey) },
        )
    }
}
