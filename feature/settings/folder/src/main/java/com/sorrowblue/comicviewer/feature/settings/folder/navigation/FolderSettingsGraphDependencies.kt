package com.sorrowblue.comicviewer.feature.settings.folder.navigation

import androidx.compose.runtime.Composable
import com.ramcosta.composedestinations.navigation.DependenciesContainerBuilder
import com.ramcosta.composedestinations.navigation.dependency
import com.ramcosta.composedestinations.navigation.navGraph
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderThumbnailOrder
import com.sorrowblue.comicviewer.domain.model.settings.folder.ImageFilterQuality
import com.sorrowblue.comicviewer.domain.model.settings.folder.ImageFormat
import com.sorrowblue.comicviewer.domain.model.settings.folder.ImageScale
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
import com.sorrowblue.comicviewer.feature.settings.folder.FolderSettingsScreenNavigator
import com.sorrowblue.comicviewer.feature.settings.folder.NavGraphs
import com.sorrowblue.comicviewer.feature.settings.folder.destinations.FileSortDialogDestination
import com.sorrowblue.comicviewer.feature.settings.folder.destinations.FolderThumbnailOrderDialogDestination
import com.sorrowblue.comicviewer.feature.settings.folder.destinations.ImageFilterQualityDialogDestination
import com.sorrowblue.comicviewer.feature.settings.folder.destinations.ImageFormatDialogDestination
import com.sorrowblue.comicviewer.feature.settings.folder.destinations.ImageScaleDialogDestination

@Composable
fun DependenciesContainerBuilder<*>.FolderSettingsGraphDependencies(
    navigateBack: () -> Unit,
) {
    navGraph(NavGraphs.folderSettings) {
        dependency(object : FolderSettingsScreenNavigator {

            override fun navigateToImageFormat(imageFormat: ImageFormat) {
                destinationsNavigator.navigate(ImageFormatDialogDestination(imageFormat))
            }

            override fun navigateToFileSort(sortType: SortType) {
                destinationsNavigator.navigate(FileSortDialogDestination(sortType))
            }

            override fun navigateToImageScale(imageScale: ImageScale) {
                destinationsNavigator.navigate(ImageScaleDialogDestination(imageScale))
            }

            override fun navigateToImageFilterQuality(imageFilterQuality: ImageFilterQuality) {
                destinationsNavigator.navigate(
                    ImageFilterQualityDialogDestination(
                        imageFilterQuality
                    )
                )
            }

            override fun navigateToFolderThumbnailOrder(folderThumbnailOrder: FolderThumbnailOrder) {
                destinationsNavigator.navigate(
                    FolderThumbnailOrderDialogDestination(
                        folderThumbnailOrder
                    )
                )
            }

            override fun navigateBack() = navigateBack()
        })
    }
}
