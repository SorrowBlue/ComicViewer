package com.sorrowblue.comicviewer.feature.settings.folder.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.DialogSceneStrategy
import com.sorrowblue.comicviewer.domain.model.settings.folder.ImageScale
import com.sorrowblue.comicviewer.feature.settings.folder.ImageScaleScreenRoot
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import kotlinx.serialization.Serializable

@Serializable
internal data class ImageScaleNavKey(val imageScale: ImageScale) : NavKey

internal fun EntryProviderScope<NavKey>.imageScaleNavEntry(navigator: Navigator) {
    entry<ImageScaleNavKey>(metadata = DialogSceneStrategy.dialog()) {
        ImageScaleScreenRoot(
            imageScale = it.imageScale,
            onDismissRequest = navigator::goBack,
        )
    }
}
