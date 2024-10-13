package com.sorrowblue.comicviewer.feature.settings.display.navigation

import androidx.compose.runtime.Composable
import com.ramcosta.composedestinations.navigation.DependenciesContainerBuilder
import com.ramcosta.composedestinations.navigation.dependency
import com.ramcosta.composedestinations.navigation.navGraph
import com.sorrowblue.comicviewer.feature.settings.display.DisplaySettingsScreenNavigator
import com.sorrowblue.comicviewer.feature.settings.display.NavGraphs
import com.sorrowblue.comicviewer.feature.settings.display.destinations.AppearanceDialogDestination

@Composable
fun DependenciesContainerBuilder<*>.DisplaySettingsGraphDependencies(
    navigateBack: () -> Unit,
) {
    navGraph(NavGraphs.displaySettings) {
        dependency(object : DisplaySettingsScreenNavigator {
            override fun navigateToDarkMode() =
                destinationsNavigator.navigate(AppearanceDialogDestination())

            override fun navigateBack() = navigateBack()
        })
    }
}
