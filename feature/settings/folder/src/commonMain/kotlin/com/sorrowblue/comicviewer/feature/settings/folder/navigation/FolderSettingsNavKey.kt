package com.sorrowblue.comicviewer.feature.settings.folder.navigation

import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.metadata
import com.sorrowblue.comicviewer.feature.settings.folder.FolderSettingsScreenContext
import com.sorrowblue.comicviewer.feature.settings.folder.FolderSettingsScreenRoot
import com.sorrowblue.comicviewer.feature.settings.folder.subscreen.thumbnailorder.FolderThumbnailOrderScreenResultKey
import com.sorrowblue.comicviewer.feature.settings.folder.subscreen.filterquality.FilterQualityResultKey
import com.sorrowblue.comicviewer.feature.settings.folder.subscreen.thumbnailformat.ThumbnailFormatScreenResultKey
import com.sorrowblue.comicviewer.feature.settings.folder.subscreen.thumbnailscale.ThumbnailScaleScreenResultKey
import com.sorrowblue.comicviewer.feature.settings.folder.subscreen.sortorder.SortOrderScreenResultKey
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
        metadata = metadata {
            put(
                NavigationResultMetadata.ResultConsumerKey,
                NavigationResultMetadata.resultConsumer(
                    SortOrderScreenResultKey,
                    ThumbnailScaleScreenResultKey,
                    FilterQualityResultKey,
                    ThumbnailFormatScreenResultKey,
                    FolderThumbnailOrderScreenResultKey,
                ),
            )

            transitionMaterialSharedAxisZ()
        } + ListDetailSceneStrategy.detailPane("Settings"),
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
