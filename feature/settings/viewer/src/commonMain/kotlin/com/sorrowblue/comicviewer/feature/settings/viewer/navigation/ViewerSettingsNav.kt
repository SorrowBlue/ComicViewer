package com.sorrowblue.comicviewer.feature.settings.viewer.navigation

import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import com.sorrowblue.comicviewer.feature.settings.viewer.ViewerSettingsScreenContext
import com.sorrowblue.comicviewer.feature.settings.viewer.ViewerSettingsScreenRoot
import com.sorrowblue.comicviewer.framework.ui.animation.transitionMaterialSharedAxisX
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import com.sorrowblue.comicviewer.framework.ui.navigation.ScreenKey
import io.github.takahirom.rin.rememberRetained
import kotlinx.serialization.Serializable

@Serializable
data object ViewerSettingsNavKey : ScreenKey

context(factory: ViewerSettingsScreenContext.Factory)
internal fun EntryProviderScope<NavKey>.viewerSettingsNavEntry(navigator: Navigator) {
    entry<ViewerSettingsNavKey>(
        metadata = ListDetailSceneStrategy.detailPane("Settings") +
            NavDisplay.transitionMaterialSharedAxisX(),
    ) {
        with(rememberRetained { factory.createViewerSettingsScreenContext() }) {
            ViewerSettingsScreenRoot(onBackClick = navigator::goBack)
        }
    }
}
