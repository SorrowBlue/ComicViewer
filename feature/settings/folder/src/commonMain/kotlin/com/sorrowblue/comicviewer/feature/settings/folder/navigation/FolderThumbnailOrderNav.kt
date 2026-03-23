package com.sorrowblue.comicviewer.feature.settings.folder.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.DialogSceneStrategy
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderThumbnailOrder
import com.sorrowblue.comicviewer.feature.settings.folder.FolderThumbnailOrderScreenRoot
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import kotlinx.serialization.Serializable

@Serializable
internal data class FolderThumbnailOrderNavKey(val folderThumbnailOrder: FolderThumbnailOrder) :
    NavKey

internal fun EntryProviderScope<NavKey>.folderThumbnailOrderNavEntry(navigator: Navigator) {
    entry<FolderThumbnailOrderNavKey>(
        metadata = DialogSceneStrategy.dialog(),
    ) {
        FolderThumbnailOrderScreenRoot(
            folderThumbnailOrder = it.folderThumbnailOrder,
            onDismissRequest = navigator::goBack,
        )
    }
}
