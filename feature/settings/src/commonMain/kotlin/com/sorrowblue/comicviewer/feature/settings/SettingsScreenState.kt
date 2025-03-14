package com.sorrowblue.comicviewer.feature.settings

import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.PaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.sorrowblue.comicviewer.feature.settings.utils.AppLocaleSettingsLauncher
import com.sorrowblue.comicviewer.feature.settings.utils.rememberAppLocaleSettingsLauncher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal interface SettingsScreenState {
    val navigator: ThreePaneScaffoldNavigator<Settings2>
    val navController: NavHostController
    fun onSettingsClick(settings2: Settings2, onStartTutorialClick: () -> Unit)
    fun onSettingsLongClick(settings2: Settings2, onStartTutorialClick: () -> Unit)
    fun onDetailBackClick()
}

@Composable
internal fun rememberSettingsScreenState(
    scaffoldDirective: PaneScaffoldDirective =
        calculateLowerInfoPaneScaffoldDirective(currentWindowAdaptiveInfo()),
    navigator: ThreePaneScaffoldNavigator<Settings2> = rememberFixListDetailPaneScaffoldNavigator(
        scaffoldDirective = scaffoldDirective,
    ),
    appLocaleSettingsLauncher: AppLocaleSettingsLauncher = rememberAppLocaleSettingsLauncher(),
    navController: NavHostController = rememberNavController(),
    scope: CoroutineScope = rememberCoroutineScope(),
): SettingsScreenState = remember {
    SettingsScreenStateImpl(
        navigator = navigator,
        navController = navController,
        appLocaleSettingsLauncher = appLocaleSettingsLauncher,
        scope = scope,
    )
}

private class SettingsScreenStateImpl(
    override val navigator: ThreePaneScaffoldNavigator<Settings2>,
    override val navController: NavHostController,
    private val appLocaleSettingsLauncher: AppLocaleSettingsLauncher,
    private val scope: CoroutineScope,
) : SettingsScreenState {

    override fun onSettingsClick(settings2: Settings2, onStartTutorialClick: () -> Unit) {
        when (settings2) {
            Settings2.LANGUAGE -> appLocaleSettingsLauncher.launch {
                onSettingsClick2(settings2)
            }


            Settings2.TUTORIAL -> onStartTutorialClick()

            else -> onSettingsClick2(settings2)
        }
    }

    override fun onSettingsLongClick(settings2: Settings2, onStartTutorialClick: () -> Unit) {
        when (settings2) {
            Settings2.LANGUAGE -> onSettingsClick2(settings2)
            else -> Unit
        }
    }

    private fun onSettingsClick2(settings2: Settings2) {
        scope.launch {
            navigator.navigateTo(ListDetailPaneScaffoldRole.Detail, settings2)
        }
    }

    override fun onDetailBackClick() {
        scope.launch {
            navigator.navigateBack()
        }
    }
}
