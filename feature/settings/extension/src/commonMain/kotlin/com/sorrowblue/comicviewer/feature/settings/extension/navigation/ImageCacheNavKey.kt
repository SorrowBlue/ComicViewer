/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.settings.extension.navigation

import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.metadata
import com.sorrowblue.comicviewer.feature.settings.extension.subscreen.imagecache.ImageCacheScreenRoot
import com.sorrowblue.comicviewer.framework.ui.animation.transitionMaterialSharedAxisX
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import com.sorrowblue.comicviewer.framework.ui.navigation3.NavigationEntry
import kotlinx.serialization.Serializable

@Serializable
internal data object ImageCacheNavKey : NavKey

@NavigationEntry
context(scope: EntryProviderScope<NavKey>)
internal fun imageCacheNavEntry(navigator: Navigator) {
    scope.entry<ImageCacheNavKey>(
        metadata = metadata {
            transitionMaterialSharedAxisX()
        } + ListDetailSceneStrategy.detailPane("Settings"),
    ) {
        ImageCacheScreenRoot(onBackClick = navigator::goBack)
    }
}
