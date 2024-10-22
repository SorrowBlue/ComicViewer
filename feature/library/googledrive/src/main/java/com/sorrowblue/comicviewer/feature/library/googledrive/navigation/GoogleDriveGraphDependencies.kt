package com.sorrowblue.comicviewer.feature.library.googledrive.navigation

import androidx.compose.runtime.Composable
import com.ramcosta.composedestinations.navigation.DependenciesContainerBuilder
import com.ramcosta.composedestinations.navigation.dependency
import com.ramcosta.composedestinations.navigation.navGraph
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.feature.library.googledrive.GoogleDriveScreenNavigator
import com.sorrowblue.comicviewer.feature.library.googledrive.NavGraphs
import com.sorrowblue.comicviewer.feature.library.googledrive.destinations.GoogleDriveScreenDestination
import com.sorrowblue.comicviewer.feature.library.googledrive.destinations.GoogleSignOutDialogDestination

@Composable
internal fun DependenciesContainerBuilder<*>.GoogleDriveGraphDependencies() {
    navGraph(NavGraphs.googleDrive) {
        dependency(object : GoogleDriveScreenNavigator {
            override fun navigateUp() {
                destinationsNavigator.navigateUp()
            }

            override fun navigateToProfile() {
                destinationsNavigator.navigate(GoogleSignOutDialogDestination)
            }

            override fun onFolderClick(folder: Folder) {
                destinationsNavigator.navigate(GoogleDriveScreenDestination(folder.path))
            }
        })
    }
}
