package com.sorrowblue.comicviewer.feature.settings

import android.os.Parcelable
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.PaneAdaptedValue
import androidx.compose.material3.adaptive.ThreePaneScaffoldNavigator
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.dependency
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.scope.DestinationScopeWithNoDependencies
import com.ramcosta.composedestinations.spec.Route
import com.sorrowblue.comicviewer.feature.settings.common.SettingsDetailNavigator
import com.sorrowblue.comicviewer.feature.settings.common.SettingsExtraNavigator
import com.sorrowblue.comicviewer.feature.settings.destinations.InAppLanguagePickerScreenDestination
import com.sorrowblue.comicviewer.feature.settings.display.destinations.DisplaySettingsScreenDestination
import com.sorrowblue.comicviewer.feature.settings.folder.FolderSettingsScreenNavigator
import com.sorrowblue.comicviewer.feature.settings.folder.destinations.FolderSettingsScreenDestination
import com.sorrowblue.comicviewer.feature.settings.folder.destinations.SupportExtensionScreenDestination
import com.sorrowblue.comicviewer.feature.settings.info.destinations.AppInfoSettingsScreenDestination
import com.sorrowblue.comicviewer.feature.settings.section.SettingsListPane
import com.sorrowblue.comicviewer.feature.settings.section.SettingsListPaneUiState
import com.sorrowblue.comicviewer.feature.settings.security.SecuritySettingsScreenNavigator
import com.sorrowblue.comicviewer.feature.settings.security.destinations.SecuritySettingsScreenDestination
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.CoreNavigator
import com.sorrowblue.comicviewer.framework.ui.copy
import kotlinx.parcelize.Parcelize

interface SettingsScreenNavigator : CoreNavigator {
    fun onStartTutorialClick()
    fun navigateToChangeAuth(enabled: Boolean)
    fun onPasswordChange()
}

@Destination
@Composable
internal fun SettingsScreen(
    navBackStackEntry: NavBackStackEntry,
    navigator: SettingsScreenNavigator,
) {
    SettingsScreen(
        savedStateHandle = navBackStackEntry.savedStateHandle,
        settingsScreenNavigator = navigator
    )
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
private fun SettingsScreen(
    savedStateHandle: SavedStateHandle,
    settingsScreenNavigator: SettingsScreenNavigator,
    state: SettingsScreenState = rememberSettingsScreenState(savedStateHandle = savedStateHandle),
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
                dependency(createCoreFeatureNavigator(navigator, settingsScreenNavigator))
            }
        )
    }
    BackHandler(navigator.canNavigateBack()) {
        navigator.navigateBack()
    }
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
fun DestinationScopeWithNoDependencies<*>.createCoreFeatureNavigator(
    scaffoldNavigator: ThreePaneScaffoldNavigator,
    settingsScreenNavigator: SettingsScreenNavigator,
) = SettingsDetailNavigatorImpl(
    scaffoldNavigator = scaffoldNavigator,
    navController = navController,
    settingsScreenNavigator = settingsScreenNavigator
)

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
class SettingsDetailNavigatorImpl(
    private val scaffoldNavigator: ThreePaneScaffoldNavigator,
    private val settingsScreenNavigator: SettingsScreenNavigator,
    private val navController: NavController,
) : SecuritySettingsScreenNavigator,
    FolderSettingsScreenNavigator,
    SettingsDetailNavigator,
    SettingsExtraNavigator {

    override fun navigateToChangeAuth(enabled: Boolean) {
        settingsScreenNavigator.navigateToChangeAuth(enabled)
    }

    override fun navigateToPasswordChange() {
        settingsScreenNavigator.onPasswordChange()
    }

    override fun navigateToExtension() {
        navController.navigate(SupportExtensionScreenDestination)
    }

    override fun navigateBack() {
        scaffoldNavigator.navigateBack()
    }

    override fun navigateUp() {
        navController.navigateUp()
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
    windowAdaptiveInfo: WindowAdaptiveInfo,
    onBackClick: () -> Unit,
    onSettingsClick: (Settings2) -> Unit,
    content: @Composable (PaddingValues) -> Unit,
) {
    ListDetailPaneScaffold(
        scaffoldState = navigator.scaffoldState,
        listPane = {
            SettingsListPane(
                uiState = uiState.listPaneUiState,
                onBackClick = onBackClick,
                onSettingsClick = onSettingsClick,
                windowAdaptiveInfo = windowAdaptiveInfo
            )
        }
    ) {
        val innerPadding =
            if (navigator.scaffoldState.scaffoldValue.secondary == PaneAdaptedValue.Expanded) {
                WindowInsets.safeDrawing.asPaddingValues().copy(start = 0.dp)
            } else {
                WindowInsets.safeDrawing.asPaddingValues()
            }
        content(innerPadding)
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
