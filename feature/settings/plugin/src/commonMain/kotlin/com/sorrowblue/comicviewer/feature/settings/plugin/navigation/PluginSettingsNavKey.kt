package com.sorrowblue.comicviewer.feature.settings.plugin.navigation

import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import com.sorrowblue.comicviewer.feature.settings.plugin.PluginScreenContext
import com.sorrowblue.comicviewer.feature.settings.plugin.PluginScreenRoot
import com.sorrowblue.comicviewer.framework.ui.animation.transitionMaterialSharedAxisX
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import io.github.takahirom.rin.rememberRetained
import kotlinx.serialization.Serializable

@Serializable
data object PluginSettingsNavKey : NavKey

context(factory: PluginScreenContext.Factory)
internal fun EntryProviderScope<NavKey>.pdfPluginNavEntry(navigator: Navigator) {
    entry<PluginSettingsNavKey>(
        metadata = ListDetailSceneStrategy.detailPane("Settings") +
            NavDisplay.transitionMaterialSharedAxisX(),
    ) {
        with(rememberRetained { factory.createPluginScreenContext() }) {
            PluginScreenRoot(
                onBackClick = navigator::goBack,
                onPdfPluginClick = { navigator.navigate(PdfPluginNavKey) },
            )
        }
    }
}
