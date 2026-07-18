/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.settings.viewer.navigation

import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.metadata
import com.sorrowblue.comicviewer.feature.settings.viewer.ViewerSettingsScreenRoot
import com.sorrowblue.comicviewer.feature.settings.viewer.subscreen.readingdirection.BindingDirectionScreenResultKey
import com.sorrowblue.comicviewer.framework.ui.animation.transitionMaterialSharedAxisX
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import com.sorrowblue.comicviewer.framework.ui.navigation3.NavigationEntry
import io.github.irgaly.navigation3.resultstate.NavigationResultMetadata
import io.github.irgaly.navigation3.resultstate.resultConsumer
import kotlinx.serialization.Serializable

@Serializable
data object ViewerSettingsNavKey : NavKey

@NavigationEntry
context(scope: EntryProviderScope<NavKey>)
internal fun viewerSettingsNavEntry(navigator: Navigator) {
    scope.entry<ViewerSettingsNavKey>(
        metadata = metadata {
            put(
                NavigationResultMetadata.ResultConsumerKey,
                NavigationResultMetadata.resultConsumer(BindingDirectionScreenResultKey),
            )
            transitionMaterialSharedAxisX()
        } + ListDetailSceneStrategy.detailPane("Settings"),
    ) {
        ViewerSettingsScreenRoot(
            onBackClick = dropUnlessResumed(block = navigator::goBack),
            onBindingDirectionClick = dropUnlessResumed { direction ->
                navigator.navigate(BindingDirectionNavKey(direction))
            },
        )
    }
}
