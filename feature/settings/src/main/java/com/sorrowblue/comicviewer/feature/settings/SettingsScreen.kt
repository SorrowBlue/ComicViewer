package com.sorrowblue.comicviewer.feature.settings

import android.os.Parcelable
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.PaneAdaptedValue
import androidx.compose.material3.adaptive.ThreePaneScaffoldNavigator
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.sorrowblue.comicviewer.feature.settings.display.navigation.SettingsDisplayRoute
import com.sorrowblue.comicviewer.feature.settings.folder.navigation.SettingsFolderRoute
import com.sorrowblue.comicviewer.feature.settings.info.navigation.SettingsAppInfoRoute
import com.sorrowblue.comicviewer.feature.settings.navigation.InAppLanguagePickerRoute
import com.sorrowblue.comicviewer.feature.settings.navigation.settingsGraph
import com.sorrowblue.comicviewer.feature.settings.section.SettingsListPane
import com.sorrowblue.comicviewer.feature.settings.section.SettingsListPaneUiState
import com.sorrowblue.comicviewer.feature.settings.security.navigation.SettingsSecurityRoute
import com.sorrowblue.comicviewer.feature.settings.viewer.navigation.SettingsViewerRoute
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.copy
import kotlinx.parcelize.Parcelize

context(NavBackStackEntry)
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
internal fun SettingsRoute(
    onBackClick: () -> Unit,
    onChangeAuthEnabled: (Boolean) -> Unit,
    onPasswordChangeClick: () -> Unit,
    onStartTutorialClick: () -> Unit,
    contentPadding: PaddingValues,
    state: SettingsScreenState = rememberSettingsScreenState(),
) {
    val navigator = state.navigator
    SettingsScreen(
        uiState = state.uiState,
        navigator = navigator,
        navController = state.navController,
        windowAdaptiveInfo = state.windowAdaptiveInfo,
        contentPadding = contentPadding,
        onBackClick = onBackClick,
        onSettingsClick = { state.onSettingsClick(it, onStartTutorialClick) },
    ) { navController, innerPadding ->
        settingsGraph(
            navController = navController,
            onBackClick = state::onDetailBackClick,
            contentPadding = innerPadding,
            onChangeAuthEnabled = onChangeAuthEnabled,
            onPasswordChangeClick = onPasswordChangeClick
        )
    }
    BackHandler(navigator.canNavigateBack()) {
        navigator.navigateBack()
    }
}

@Parcelize
internal data class SettingsScreenUiState(
    val listPaneUiState: SettingsListPaneUiState = SettingsListPaneUiState(),
) : Parcelable

@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun SettingsScreen(
    uiState: SettingsScreenUiState,
    navigator: ThreePaneScaffoldNavigator,
    navController: NavHostController,
    windowAdaptiveInfo: WindowAdaptiveInfo,
    contentPadding: PaddingValues,
    onBackClick: () -> Unit,
    onSettingsClick: (Settings2) -> Unit,
    navGraph: NavGraphBuilder.(NavHostController, PaddingValues) -> Unit,
) {
    ListDetailPaneScaffold(
        scaffoldState = navigator.scaffoldState,
        listPane = {
            SettingsListPane(
                uiState = uiState.listPaneUiState,
                onBackClick = onBackClick,
                onSettingsClick = onSettingsClick,
                windowAdaptiveInfo = windowAdaptiveInfo,
                contentPadding = contentPadding
            )
        }
    ) {
        val innerPadding =
            if (navigator.scaffoldState.scaffoldValue.secondary == PaneAdaptedValue.Expanded) {
                contentPadding.copy(start = 0.dp)
            } else {
                contentPadding
            }

        NavHost(
            navController = navController,
            startDestination = uiState.listPaneUiState.currentSettings2.route
        ) {
            navGraph(navController, innerPadding)
        }
    }
}

enum class Settings2(val title: Int, val icon: ImageVector, val route: String = "") {
    DISPLAY(R.string.settings_label_display, ComicIcons.DisplaySettings, SettingsDisplayRoute),
    FOLDER(R.string.settings_label_folder, ComicIcons.FolderOpen, SettingsFolderRoute),
    VIEWER(R.string.settings_label_viewer, ComicIcons.Image, SettingsViewerRoute),
    SECURITY(R.string.settings_label_security, ComicIcons.Lock, SettingsSecurityRoute),
    APP(R.string.settings_label_app, ComicIcons.Info, SettingsAppInfoRoute),
    TUTORIAL(R.string.settings_label_tutorial, ComicIcons.Start),
    LANGUAGE(R.string.settings_label_language, ComicIcons.Language, InAppLanguagePickerRoute),
}
