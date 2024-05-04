package com.sorrowblue.comicviewer.feature.settings.folder.navigation

import androidx.compose.runtime.Composable
import com.ramcosta.composedestinations.navigation.DependenciesContainerBuilder
import com.ramcosta.composedestinations.navigation.dependency
import com.ramcosta.composedestinations.navigation.navGraph
import com.sorrowblue.comicviewer.feature.settings.folder.FolderSettingsScreenNavigator
import com.sorrowblue.comicviewer.feature.settings.folder.NavGraphs
import com.sorrowblue.comicviewer.feature.settings.folder.destinations.SupportExtensionScreenDestination

@Composable
fun DependenciesContainerBuilder<*>.FolderSettingsGraphDependencies(
    navigateBack: () -> Unit,
) {
    navGraph(NavGraphs.folderSettings) {
        dependency(object : FolderSettingsScreenNavigator {
            override fun navigateToExtension() {
                destinationsNavigator.navigate(SupportExtensionScreenDestination)
            }

            override fun navigateBack() = navigateBack()
        })
    }
}
