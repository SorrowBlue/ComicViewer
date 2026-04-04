package com.sorrowblue.comicviewer.feature.settings.folder.navigation

import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import com.sorrowblue.comicviewer.feature.settings.folder.FolderSettingsScreenContext
import com.sorrowblue.comicviewer.feature.settings.folder.FolderSettingsScreenRoot
import com.sorrowblue.comicviewer.feature.settings.folder.FolderThumbnailOrderScreenResultKey
import com.sorrowblue.comicviewer.feature.settings.folder.ImageFilterQualityResultKey
import com.sorrowblue.comicviewer.feature.settings.folder.ImageFormatScreenResultKey
import com.sorrowblue.comicviewer.feature.settings.folder.ImageScaleScreenResultKey
import com.sorrowblue.comicviewer.feature.settings.folder.SortTypeScreenResultKey
import com.sorrowblue.comicviewer.framework.ui.animation.transitionMaterialSharedAxisZ
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import io.github.irgaly.navigation3.resultstate.NavigationResultMetadata
import io.github.irgaly.navigation3.resultstate.resultConsumer
import io.github.takahirom.rin.rememberRetained
import kotlinx.serialization.Serializable

@Serializable
data object FolderSettingsNavKey : NavKey

context(factory: FolderSettingsScreenContext.Factory)
internal fun EntryProviderScope<NavKey>.folderSettingsNavEntry(navigator: Navigator) {
    entry<FolderSettingsNavKey>(
        metadata = ListDetailSceneStrategy.detailPane("Settings") +
            NavigationResultMetadata.resultConsumer(
                SortTypeScreenResultKey,
                ImageScaleScreenResultKey,
                ImageFilterQualityResultKey,
                ImageFormatScreenResultKey,
                FolderThumbnailOrderScreenResultKey,
            ) + NavDisplay.transitionMaterialSharedAxisZ(),
    ) {
        with(rememberRetained { factory.createFolderSettingsScreenContext() }) {
            FolderSettingsScreenRoot(
                onBackClick = navigator::goBack,
                onSortTypeClick = { navigator.navigate(SortTypeNavKey(it)) },
                onFolderThumbnailOrderClick = {
                    navigator.navigate(
                        FolderThumbnailOrderNavKey(it),
                    )
                },
                onImageFormatClick = { navigator.navigate(ImageFormatNavKey(it)) },
                onImageScaleClick = { navigator.navigate(ImageScaleNavKey(it)) },
                onImageFilterQualityClick = {
                    navigator.navigate(
                        ImageFilterQualityNavKey(it),
                    )
                },
            )
        }
    }
}
