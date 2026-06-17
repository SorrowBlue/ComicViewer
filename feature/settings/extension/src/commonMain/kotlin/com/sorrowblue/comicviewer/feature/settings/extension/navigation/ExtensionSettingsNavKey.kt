package com.sorrowblue.comicviewer.feature.settings.extension.navigation

import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.metadata
import com.sorrowblue.comicviewer.feature.settings.extension.ExtensionSettingsScreenRoot
import com.sorrowblue.comicviewer.framework.ui.animation.transitionMaterialSharedAxisX
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import kotlinx.serialization.Serializable

@Serializable
data object ExtensionSettingsNavKey : NavKey

internal fun EntryProviderScope<NavKey>.extensionSettingsNavEntry(navigator: Navigator) {
    entry<ExtensionSettingsNavKey>(
        metadata = metadata {
            transitionMaterialSharedAxisX()
        } + ListDetailSceneStrategy.detailPane("Settings"),
    ) {
        ExtensionSettingsScreenRoot(
            onBackClick = dropUnlessResumed { navigator.goBack() },
            onImageCacheClick = dropUnlessResumed { navigator.navigate(ImageCacheNavKey) },
        )
    }
}
