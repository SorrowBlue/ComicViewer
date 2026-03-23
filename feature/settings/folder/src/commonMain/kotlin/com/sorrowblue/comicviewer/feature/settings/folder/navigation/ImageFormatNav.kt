package com.sorrowblue.comicviewer.feature.settings.folder.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.DialogSceneStrategy
import com.sorrowblue.comicviewer.domain.model.settings.folder.ImageFormat
import com.sorrowblue.comicviewer.feature.settings.folder.ImageFormatScreenRoot
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import kotlinx.serialization.Serializable

@Serializable
internal data class ImageFormatNavKey(val imageFormat: ImageFormat) : NavKey

internal fun EntryProviderScope<NavKey>.imageFormatNavEntry(navigator: Navigator) {
    entry<ImageFormatNavKey>(metadata = DialogSceneStrategy.dialog()) {
        ImageFormatScreenRoot(
            imageFormat = it.imageFormat,
            onDismissRequest = navigator::goBack,
        )
    }
}
