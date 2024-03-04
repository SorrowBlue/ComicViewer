package com.sorrowblue.comicviewer.feature.settings

import android.os.Parcelable
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.PaneAdaptedValue
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.dependency
import com.ramcosta.composedestinations.spec.Route
import com.sorrowblue.comicviewer.feature.settings.destinations.InAppLanguagePickerScreenDestination
import com.sorrowblue.comicviewer.feature.settings.display.destinations.DisplaySettingsScreenDestination
import com.sorrowblue.comicviewer.feature.settings.folder.destinations.FolderSettingsScreenDestination
import com.sorrowblue.comicviewer.feature.settings.info.destinations.AppInfoSettingsScreenDestination
import com.sorrowblue.comicviewer.feature.settings.section.SettingsListPane
import com.sorrowblue.comicviewer.feature.settings.section.SettingsListPaneUiState
import com.sorrowblue.comicviewer.feature.settings.security.destinations.SecuritySettingsScreenDestination
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.copy
import kotlinx.parcelize.Parcelize

internal interface SettingsScreenNavigator {
    fun navigateUp()
    fun onStartTutorialClick()
    fun navigateToChangeAuth(enabled: Boolean)
    fun onPasswordChange()
}

@Destination
@Composable
internal fun SettingsScreen(navigator: SettingsScreenNavigator) {
    SettingsScreen(settingsScreenNavigator = navigator)
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
private fun SettingsScreen(
    settingsScreenNavigator: SettingsScreenNavigator,
    state: SettingsScreenState = rememberSettingsScreenState(),
) {
    val uiState = state.uiState
    val navigator = state.navigator
    val windowAdaptiveInfo = state.windowAdaptiveInfo
    SettingsScreen(
        uiState = uiState,
        navigator = navigator,
        windowAdaptiveInfo = windowAdaptiveInfo,
        onBackClick = settingsScreenNavigator::navigateUp,
        onSettingsClick = {
            state.onSettingsClick(it, settingsScreenNavigator::onStartTutorialClick)
        },
    ) { contentPadding ->
        DestinationsNavHost(
            navGraph = SettingsDetailNavGraph,
            startRoute = state.uiState.listPaneUiState.currentSettings2.route
                ?: SettingsDetailNavGraph.startRoute,
            dependenciesContainerBuilder = {
                dependency(contentPadding)
                dependency(innerSettingsNavigator(navigator, settingsScreenNavigator))
            }
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
    navigator: ThreePaneScaffoldNavigator<Unit>,
    windowAdaptiveInfo: WindowAdaptiveInfo,
    onBackClick: () -> Unit,
    onSettingsClick: (Settings2) -> Unit,
    content: @Composable (PaddingValues) -> Unit,
) {
    ListDetailPaneScaffold(
        scaffoldState = navigator.scaffoldState,
        listPane = {
            AnimatedPane(modifier = Modifier) {
                SettingsListPane(
                    uiState = uiState.listPaneUiState,
                    onBackClick = onBackClick,
                    onSettingsClick = onSettingsClick,
                    windowAdaptiveInfo = windowAdaptiveInfo
                )
            }
        },
        windowInsets = WindowInsets(0)
    ) {
        val contentPadding =
            if (navigator.scaffoldState.scaffoldValue.secondary == PaneAdaptedValue.Expanded) {
                WindowInsets.safeDrawing.asPaddingValues().copy(start = 0.dp)
            } else {
                WindowInsets.safeDrawing.asPaddingValues()
            }
        AnimatedPane(modifier = Modifier) {
            content(contentPadding)
        }
    }
}

enum class Settings2(
    val title: Int,
    val icon: ImageVector,
    val route: Route? = null,
) {
    DISPLAY(
        R.string.settings_label_display,
        ComicIcons.DisplaySettings,
        DisplaySettingsScreenDestination
    ),
    FOLDER(
        R.string.settings_label_folder,
        ComicIcons.FolderOpen,
        FolderSettingsScreenDestination
    ),
    VIEWER(R.string.settings_label_viewer, ComicIcons.Image),
    SECURITY(
        R.string.settings_label_security,
        ComicIcons.Lock,
        SecuritySettingsScreenDestination
    ),
    APP(
        R.string.settings_label_app,
        ComicIcons.Info,
        AppInfoSettingsScreenDestination
    ),
    TUTORIAL(R.string.settings_label_tutorial, ComicIcons.Start),
    LANGUAGE(
        R.string.settings_label_language,
        ComicIcons.Language,
        InAppLanguagePickerScreenDestination
    ),
}
