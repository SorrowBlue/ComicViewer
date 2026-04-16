package com.sorrowblue.comicviewer.feature.settings.viewer.navigation

import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.metadata
import com.sorrowblue.comicviewer.feature.settings.viewer.ViewerSettingsScreenContext
import com.sorrowblue.comicviewer.feature.settings.viewer.ViewerSettingsScreenRoot
import com.sorrowblue.comicviewer.feature.settings.viewer.subscreen.readingdirection.BindingDirectionScreenResultKey
import com.sorrowblue.comicviewer.framework.ui.animation.transitionMaterialSharedAxisX
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import io.github.irgaly.navigation3.resultstate.NavigationResultMetadata
import io.github.irgaly.navigation3.resultstate.resultConsumer
import io.github.takahirom.rin.rememberRetained
import kotlinx.serialization.Serializable
import logcat.logcat

@Serializable
data object ViewerSettingsNavKey : NavKey

context(factory: ViewerSettingsScreenContext.Factory)
internal fun EntryProviderScope<NavKey>.viewerSettingsNavEntry(navigator: Navigator) {
    entry<ViewerSettingsNavKey>(
        metadata = metadata {
            put(
                NavigationResultMetadata.ResultConsumerKey,
                NavigationResultMetadata.resultConsumer(BindingDirectionScreenResultKey),
            )
            transitionMaterialSharedAxisX()
        } + ListDetailSceneStrategy.detailPane("Settings"),
    ) {
        with(rememberRetained { factory.createViewerSettingsScreenContext() }) {
            ViewerSettingsScreenRoot(
                onBackClick = dropUnlessResumed(block = navigator::goBack),
                onBindingDirectionClick = dropUnlessResumed { direction ->
                    navigator.navigate(BindingDirectionNavKey(direction))
                    logcat { "#onBindingDirectionClick" }
                },
            )
        }
    }
}
