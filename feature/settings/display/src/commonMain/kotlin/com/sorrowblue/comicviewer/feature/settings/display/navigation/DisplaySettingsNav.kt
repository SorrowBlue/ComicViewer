package com.sorrowblue.comicviewer.feature.settings.display.navigation

import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import com.sorrowblue.comicviewer.feature.settings.display.DisplaySettingsScreenContext
import com.sorrowblue.comicviewer.feature.settings.display.DisplaySettingsScreenRoot
import com.sorrowblue.comicviewer.framework.ui.animation.transitionMaterialSharedAxisX
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import com.sorrowblue.comicviewer.framework.ui.navigation.ScreenKey
import io.github.takahirom.rin.rememberRetained
import kotlinx.serialization.Serializable

@Serializable
data object DisplaySettingsNavKey : ScreenKey

context(factory: DisplaySettingsScreenContext.Factory)
internal fun EntryProviderScope<NavKey>.displaySettingsNavEntry(navigator: Navigator) {
    entry<DisplaySettingsNavKey>(
        metadata = ListDetailSceneStrategy.detailPane("Settings") +
            NavDisplay.transitionMaterialSharedAxisX(),
    ) {
        with(rememberRetained { factory.createDisplaySettingsScreenContext() }) {
            DisplaySettingsScreenRoot(
                onBackClick = navigator::goBack,
                onDarkModeClick = { navigator.navigate(DarkModeNavKey) },
            )
        }
    }
}
