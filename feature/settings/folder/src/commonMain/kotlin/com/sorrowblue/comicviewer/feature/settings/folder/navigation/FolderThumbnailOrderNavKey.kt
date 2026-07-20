/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.settings.folder.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.DialogSceneStrategy
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderThumbnailOrder
import com.sorrowblue.comicviewer.feature.settings.folder.subscreen.thumbnailorder.FolderThumbnailOrderScreenRoot
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import com.sorrowblue.comicviewer.framework.ui.navigation3.NavigationEntry
import kotlinx.serialization.Serializable

@Serializable
internal data class FolderThumbnailOrderNavKey(val folderThumbnailOrder: FolderThumbnailOrder) :
    NavKey

@NavigationEntry
context(scope: EntryProviderScope<NavKey>)
internal fun folderThumbnailOrderNavEntry(navigator: Navigator) {
    scope.entry<FolderThumbnailOrderNavKey>(
        metadata = DialogSceneStrategy.dialog(),
    ) {
        FolderThumbnailOrderScreenRoot(
            folderThumbnailOrder = it.folderThumbnailOrder,
            onDismissRequest = navigator::goBack,
        )
    }
}
