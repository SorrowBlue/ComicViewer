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
    val navigator: ThreePaneScaffoldNavigator<SettingsItem>
    val navController: NavHostController
    fun onSettingsClick(item: SettingsItem, onStartTutorialClick: () -> Unit)
    fun onSettingsLongClick(item: SettingsItem, onStartTutorialClick: () -> Unit)
    fun onDetailBackClick()
}

@Composable
internal fun rememberSettingsScreenState(
    scaffoldDirective: PaneScaffoldDirective =
        calculateLowerInfoPaneScaffoldDirective(currentWindowAdaptiveInfo()),
    navigator: ThreePaneScaffoldNavigator<SettingsItem> = rememberFixListDetailPaneScaffoldNavigator(
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
    override val navigator: ThreePaneScaffoldNavigator<SettingsItem>,
    override val navController: NavHostController,
    private val appLocaleSettingsLauncher: AppLocaleSettingsLauncher,
    private val scope: CoroutineScope,
) : SettingsScreenState {

    override fun onSettingsClick(item: SettingsItem, onStartTutorialClick: () -> Unit) {
        when (item) {
            SettingsItem.LANGUAGE -> appLocaleSettingsLauncher.launch {
                onSettingsClick2(item)
            }

            SettingsItem.TUTORIAL -> onStartTutorialClick()

            else -> onSettingsClick2(item)
        }
    }

    override fun onSettingsLongClick(item: SettingsItem, onStartTutorialClick: () -> Unit) {
        when (item) {
            SettingsItem.LANGUAGE -> onSettingsClick2(item)
            else -> Unit
        }
    }

    private fun onSettingsClick2(item: SettingsItem) {
        scope.launch {
            navigator.navigateTo(ListDetailPaneScaffoldRole.Detail, item)
        }
    }

    override fun onDetailBackClick() {
        scope.launch {
            navigator.navigateBack()
        }
    }
}
