package com.sorrowblue.comicviewer.feature.settings.navigation

/*

@Composable
internal fun DependenciesContainerBuilder<*>.SettingsDetailGraphDependencies(
    scaffoldNavigator: ThreePaneScaffoldNavigator<Settings2>,
    settingsScreenNavigator: SettingsScreenNavigator,
) {
    val scope = rememberCoroutineScope()
    navGraph(NavGraphs.settingsDetail) {
        dependency(object :
            SecuritySettingsScreenNavigator,
            SettingsDetailNavigator,
            SettingsExtraNavigator {

            override fun navigateToChangeAuth(enabled: Boolean) {
                settingsScreenNavigator.navigateToChangeAuth(enabled)
            }

            override fun navigateToPasswordChange() {
                settingsScreenNavigator.onPasswordChange()
            }

            override fun navigateBack() {
                scope.launch {
                    scaffoldNavigator.navigateBack()
                }
            }

            override fun navigateUp() {
                destinationsNavigator.navigateUp()
            }
        })
    }

    FolderSettingsGraphDependencies(
        navigateBack = {
            scope.launch { scaffoldNavigator.navigateBack() }
        }
    )
    DisplaySettingsGraphDependencies(
        navigateBack = {
            scope.launch { scaffoldNavigator.navigateBack() }
        }
    )
    AppInfoSettingsGraphDependencies(
        navigateBack = {
            scope.launch { scaffoldNavigator.navigateBack() }
        }
    )
}
*/
