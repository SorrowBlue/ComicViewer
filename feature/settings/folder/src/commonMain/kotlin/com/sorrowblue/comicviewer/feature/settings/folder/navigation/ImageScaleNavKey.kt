/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.settings.folder.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.DialogSceneStrategy
import com.sorrowblue.comicviewer.domain.model.settings.folder.ImageScale
import com.sorrowblue.comicviewer.feature.settings.folder.subscreen.thumbnailscale.ThumbnailScaleScreenRoot
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import com.sorrowblue.comicviewer.framework.ui.navigation3.NavigationEntry
import kotlinx.serialization.Serializable

@Serializable
internal data class ImageScaleNavKey(val imageScale: ImageScale) : NavKey

@NavigationEntry
context(scope: EntryProviderScope<NavKey>)
internal fun imageScaleNavEntry(navigator: Navigator) {
    scope.entry<ImageScaleNavKey>(metadata = DialogSceneStrategy.dialog()) {
        ThumbnailScaleScreenRoot(
            imageScale = it.imageScale,
            onDismissRequest = navigator::goBack,
        )
    }
}
