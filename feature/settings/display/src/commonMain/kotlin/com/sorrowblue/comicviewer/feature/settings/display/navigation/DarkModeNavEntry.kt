package com.sorrowblue.comicviewer.feature.settings.display.navigation

import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.DialogSceneStrategy
import androidx.navigation3.ui.NavDisplay
import com.sorrowblue.comicviewer.feature.settings.display.DarkModeScreenContext
import com.sorrowblue.comicviewer.feature.settings.display.DarkModeScreenRoot
import com.sorrowblue.comicviewer.feature.settings.display.DisplaySettingsScreenContext
import com.sorrowblue.comicviewer.feature.settings.display.DisplaySettingsScreenRoot
import com.sorrowblue.comicviewer.framework.ui.animation.transitionMaterialSharedAxisX
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import io.github.takahirom.rin.rememberRetained

context(factory: DarkModeScreenContext.Factory)
internal fun EntryProviderScope<NavKey>.darkModeNavEntry(navigator: Navigator) {
    entry<DarkModeNavKey>(metadata = DialogSceneStrategy.dialog()) {
        with(rememberRetained { factory.createDarkModeScreenContext() }) {
            DarkModeScreenRoot(
                onDismissRequest = navigator::goBack,
                onComplete = navigator::goBack,
            )
        }
    }
}

context(factory: DisplaySettingsScreenContext.Factory)
internal fun EntryProviderScope<NavKey>.displaySettingsNavEntry(navigator: Navigator) {
    entry<DisplaySettingsNavKey>(
        metadata = ListDetailSceneStrategy.detailPane("Settings")
            + NavDisplay.transitionMaterialSharedAxisX(),
    ) {
        with(rememberRetained { factory.createDisplaySettingsScreenContext() }) {
            DisplaySettingsScreenRoot(
                onBackClick = navigator::goBack,
                onDarkModeClick = { navigator.navigate(DarkModeNavKey) },
            )
        }
    }
}
