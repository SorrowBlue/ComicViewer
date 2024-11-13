package com.sorrowblue.comicviewer.feature.library.onedrive.navigation

import androidx.compose.runtime.Composable
import com.ramcosta.composedestinations.navigation.DependenciesContainerBuilder
import com.ramcosta.composedestinations.navigation.dependency
import com.ramcosta.composedestinations.navigation.navGraph
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.feature.library.onedrive.NavGraphs
import com.sorrowblue.comicviewer.feature.library.onedrive.OneDriveScreenNavigator
import com.sorrowblue.comicviewer.feature.library.onedrive.destinations.OneDriveAccountDialogDestination
import com.sorrowblue.comicviewer.feature.library.onedrive.destinations.OneDriveScreenDestination

@Composable
internal fun DependenciesContainerBuilder<*>.OneDriveGraphDependencies() {
    navGraph(NavGraphs.oneDrive) {
        dependency(object : OneDriveScreenNavigator {

            override fun navigateUp() {
                destinationsNavigator.navigateUp()
            }

            override fun onFileClick(file: File) {
                destinationsNavigator.navigate(OneDriveScreenDestination(file.name, file.path))
            }

            override fun onProfileImageClick() {
                destinationsNavigator.navigate(OneDriveAccountDialogDestination)
            }
        })
    }
}
