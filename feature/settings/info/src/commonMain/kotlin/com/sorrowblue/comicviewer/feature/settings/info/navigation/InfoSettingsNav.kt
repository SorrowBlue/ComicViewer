package com.sorrowblue.comicviewer.feature.settings.info.navigation

import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import com.sorrowblue.comicviewer.feature.settings.info.AppInfoSettingsScreenRoot
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
        AppInfoSettingsScreenRoot(
            onBackClick = navigator::goBack,
            onLicenceClick = { navigator.navigate(LicenseNavKey) },
        )
    }
}
