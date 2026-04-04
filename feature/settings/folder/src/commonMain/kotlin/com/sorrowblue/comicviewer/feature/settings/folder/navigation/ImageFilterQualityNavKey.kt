package com.sorrowblue.comicviewer.feature.settings.folder.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.DialogSceneStrategy
import com.sorrowblue.comicviewer.domain.model.settings.folder.ImageFilterQuality
import com.sorrowblue.comicviewer.feature.settings.folder.ImageFilterQualityScreenRoot
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import kotlinx.serialization.Serializable

@Serializable
internal data class ImageFilterQualityNavKey(val imageFilterQuality: ImageFilterQuality) : NavKey

internal fun EntryProviderScope<NavKey>.imageFilterQualityNavEntry(navigator: Navigator) {
    entry<ImageFilterQualityNavKey>(metadata = DialogSceneStrategy.dialog()) {
        ImageFilterQualityScreenRoot(
            imageFilterQuality = it.imageFilterQuality,
            onDismissRequest = navigator::goBack,
        )
    }
}
