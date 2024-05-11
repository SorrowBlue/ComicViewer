package com.sorrowblue.comicviewer.feature.library.dropbox.navigation

import androidx.compose.runtime.Composable
import com.ramcosta.composedestinations.navigation.DependenciesContainerBuilder
import com.ramcosta.composedestinations.navigation.dependency
import com.ramcosta.composedestinations.navigation.navGraph
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.feature.library.dropbox.DropBoxLoginScreenNavigator
import com.sorrowblue.comicviewer.feature.library.dropbox.DropBoxScreenNavigator
import com.sorrowblue.comicviewer.feature.library.dropbox.NavGraphs
import com.sorrowblue.comicviewer.feature.library.dropbox.destinations.DropBoxLoginScreenDestination
import com.sorrowblue.comicviewer.feature.library.dropbox.destinations.DropBoxScreenDestination
import com.sorrowblue.comicviewer.feature.library.dropbox.navgraphs.DropBoxNavGraph

@Composable
internal fun DependenciesContainerBuilder<*>.DropBoxGraphDependencies() {
    navGraph(NavGraphs.dropBox) {
        dependency(object : DropBoxScreenNavigator, DropBoxLoginScreenNavigator {

            override fun onLoginCompleted() {
                destinationsNavigator.navigate(DropBoxScreenDestination()) {
                    popUpTo(DropBoxNavGraph) {
                        inclusive = true
                    }
                }
            }

            override fun navigateUp() {
                destinationsNavigator.navigateUp()
            }

            override fun onFolderClick(folder: Folder) {
                destinationsNavigator.navigate(DropBoxScreenDestination(folder.path))
            }

            override fun requireAuthentication() {
                destinationsNavigator.navigate(DropBoxLoginScreenDestination) {
                    popUpTo(DropBoxNavGraph) {
                        inclusive = true
                    }
                }
            }
        })
    }
}
