package com.sorrowblue.comicviewer.feature.settings.info.navigation

import androidx.compose.runtime.Composable
import com.ramcosta.composedestinations.navigation.DependenciesContainerBuilder
import com.ramcosta.composedestinations.navigation.dependency
import com.ramcosta.composedestinations.navigation.navGraph
import com.sorrowblue.comicviewer.feature.settings.info.AppInfoSettingsScreenNavigator
import com.sorrowblue.comicviewer.feature.settings.info.NavGraphs
import com.sorrowblue.comicviewer.feature.settings.info.destinations.LicenseScreenDestination

@Composable
fun DependenciesContainerBuilder<*>.AppInfoSettingsGraphDependencies(
    navigateBack: () -> Unit,
) {
    navGraph(NavGraphs.appInfoSettings) {
        dependency(object : AppInfoSettingsScreenNavigator {
            override fun navigateToLicense() {
                destinationsNavigator.navigate(LicenseScreenDestination)
            }

            override fun navigateBack() = navigateBack()
        })
    }
}
