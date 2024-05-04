package com.sorrowblue.comicviewer.feature.library.googledrive.navigation

import androidx.compose.runtime.Composable
import com.ramcosta.composedestinations.navigation.DependenciesContainerBuilder
import com.ramcosta.composedestinations.navigation.dependency
import com.ramcosta.composedestinations.navigation.navGraph
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.feature.library.googledrive.GoogleDriveLoginScreenNavigator
import com.sorrowblue.comicviewer.feature.library.googledrive.GoogleDriveScreenNavigator
import com.sorrowblue.comicviewer.feature.library.googledrive.NavGraphs
import com.sorrowblue.comicviewer.feature.library.googledrive.destinations.GoogleDriveLoginScreenDestination
import com.sorrowblue.comicviewer.feature.library.googledrive.destinations.GoogleDriveScreenDestination

@Composable
internal fun DependenciesContainerBuilder<*>.GoogleDriveGraphDependencies() {
    navGraph(NavGraphs.googleDrive) {
        dependency(object : GoogleDriveScreenNavigator, GoogleDriveLoginScreenNavigator {

            override fun onComplete() {
                destinationsNavigator.navigate(GoogleDriveScreenDestination()) {
                    popUpTo(NavGraphs.googleDrive) {
                        inclusive = true
                    }
                }
            }

            override fun navigateUp() {
                destinationsNavigator.navigateUp()
            }

            override fun onFolderClick(folder: Folder) {
                destinationsNavigator.navigate(GoogleDriveScreenDestination(folder.path))
            }

            override fun requireAuthentication() {
                destinationsNavigator.navigate(GoogleDriveLoginScreenDestination) {
                    popUpTo(NavGraphs.googleDrive) {
                        inclusive = true
                    }
                }
            }
        })
    }
}
