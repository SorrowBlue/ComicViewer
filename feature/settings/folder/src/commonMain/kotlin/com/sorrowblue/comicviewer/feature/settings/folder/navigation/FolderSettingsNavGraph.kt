package com.sorrowblue.comicviewer.feature.settings.folder.navigation

import androidx.navigation.NavController
import com.sorrowblue.cmpdestinations.annotation.NavGraph
import com.sorrowblue.comicviewer.domain.model.settings.folder.ImageFormat
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
import com.sorrowblue.comicviewer.feature.settings.common.SettingsDetailNavigator
import com.sorrowblue.comicviewer.feature.settings.common.SettingsScope
import com.sorrowblue.comicviewer.feature.settings.folder.FolderSettings
import com.sorrowblue.comicviewer.feature.settings.folder.FolderSettingsScreenNavigator
import com.sorrowblue.comicviewer.feature.settings.folder.FolderThumbnailOrderSettings
import com.sorrowblue.comicviewer.feature.settings.folder.ImageFilterQualitySettings
import com.sorrowblue.comicviewer.feature.settings.folder.ImageScaleSettings
import com.sorrowblue.comicviewer.feature.settings.folder.SortTypeSettings
import kotlinx.serialization.Serializable
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped
import com.sorrowblue.comicviewer.feature.settings.folder.ImageFormatSettings as ImageFormatRoute

@Serializable
@NavGraph(
    startDestination = FolderSettings::class,
    destinations = [
        FolderSettings::class,
        SortTypeSettings::class,
        FolderThumbnailOrderSettings::class,
        ImageFilterQualitySettings::class,
        ImageFormatRoute::class,
        ImageScaleSettings::class,
    ]
)
data object FolderSettingsNavGraph

@Scope(SettingsScope::class)
@Scoped
internal class FolderSettingsNavGraphNavigator(
    private val navController: NavController,
    private val navigator: SettingsDetailNavigator,
) : FolderSettingsScreenNavigator {

    override fun navigateToImageFormat(imageFormat: ImageFormat) {
        navController.navigate(ImageFormatRoute(imageFormat))
    }

    override fun navigateToFileSort(sortType: SortType) {
        navController.navigate(SortTypeSettings(sortType))
    }

    override fun navigateToImageScale(imageScale: com.sorrowblue.comicviewer.domain.model.settings.folder.ImageScale) {
        navController.navigate(ImageScaleSettings(imageScale))
    }

    override fun navigateToImageFilterQuality(imageFilterQuality: com.sorrowblue.comicviewer.domain.model.settings.folder.ImageFilterQuality) {
        navController.navigate(ImageFilterQualitySettings(imageFilterQuality))
    }

    override fun navigateToFolderThumbnailOrder(folderThumbnailOrder: com.sorrowblue.comicviewer.domain.model.settings.folder.FolderThumbnailOrder) {
        navController.navigate(FolderThumbnailOrderSettings(folderThumbnailOrder))
    }

    override fun navigateBack() = navigator.navigateBack()
}
