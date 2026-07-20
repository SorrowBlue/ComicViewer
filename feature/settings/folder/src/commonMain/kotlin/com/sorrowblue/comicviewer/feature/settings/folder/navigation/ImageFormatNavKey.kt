/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.settings.folder.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.DialogSceneStrategy
import com.sorrowblue.comicviewer.domain.model.settings.folder.ImageFormat
import com.sorrowblue.comicviewer.feature.settings.folder.subscreen.thumbnailformat.ThumbnailFormatScreenRoot
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import com.sorrowblue.comicviewer.framework.ui.navigation3.NavigationEntry
import kotlinx.serialization.Serializable

@Serializable
internal data class ImageFormatNavKey(val imageFormat: ImageFormat) : NavKey

@NavigationEntry
context(scope: EntryProviderScope<NavKey>)
internal fun imageFormatNavEntry(navigator: Navigator) {
    scope.entry<ImageFormatNavKey>(metadata = DialogSceneStrategy.dialog()) {
        ThumbnailFormatScreenRoot(
            imageFormat = it.imageFormat,
            onDismissRequest = navigator::goBack,
        )
    }
}
