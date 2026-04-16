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
import kotlinx.serialization.Serializable

@Serializable
data object InfoSettingsNavKey : NavKey

internal fun EntryProviderScope<NavKey>.infoSettingsNavEntry(navigator: Navigator) {
    entry<InfoSettingsNavKey>(
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
