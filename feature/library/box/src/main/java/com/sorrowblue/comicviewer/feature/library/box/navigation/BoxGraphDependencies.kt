package com.sorrowblue.comicviewer.feature.library.box.navigation

import androidx.compose.runtime.Composable
import com.ramcosta.composedestinations.navigation.DependenciesContainerBuilder
import com.ramcosta.composedestinations.navigation.dependency
import com.ramcosta.composedestinations.navigation.navGraph
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.feature.library.box.BoxOauth2RouteNavigator
import com.sorrowblue.comicviewer.feature.library.box.BoxScreenNavigator
import com.sorrowblue.comicviewer.feature.library.box.NavGraphs
import com.sorrowblue.comicviewer.feature.library.box.data.boxModule
import com.sorrowblue.comicviewer.feature.library.box.destinations.BoxLoginScreenDestination
import com.sorrowblue.comicviewer.feature.library.box.destinations.BoxScreenDestination
import org.koin.core.context.loadKoinModules

@Composable
internal fun DependenciesContainerBuilder<*>.BoxGraphDependencies() {
    navGraph(NavGraphs.box) {
        loadKoinModules(boxModule)
        dependency(object : BoxScreenNavigator, BoxOauth2RouteNavigator {
            override fun requireLogin() {
                destinationsNavigator.navigate(BoxLoginScreenDestination) {
                    popUpTo(NavGraphs.box) {
                        inclusive = true
                    }
                }
            }

            override fun onFolderClick(folder: Folder) {
                destinationsNavigator.navigate(BoxScreenDestination(folder.path))
            }

            override fun navigateUp() {
                destinationsNavigator.popBackStack()
            }

            override fun onComplete() {
                destinationsNavigator.navigate(BoxScreenDestination()) {
                    popUpTo(NavGraphs.box) {
                        inclusive = true
                    }
                }
            }

            override fun onFail() {
                destinationsNavigator.navigate(BoxLoginScreenDestination()) {
                    popUpTo(NavGraphs.box) {
                        inclusive = true
                    }
                }
            }
        })
    }
}
