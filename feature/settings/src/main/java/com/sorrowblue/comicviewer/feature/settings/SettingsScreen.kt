package com.sorrowblue.comicviewer.feature.settings

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.parameters.CodeGenVisibility
import com.ramcosta.composedestinations.spec.Direction
import com.sorrowblue.comicviewer.feature.settings.destinations.ImageCacheScreenDestination
import com.sorrowblue.comicviewer.feature.settings.destinations.InAppLanguagePickerScreenDestination
import com.sorrowblue.comicviewer.feature.settings.display.destinations.DisplaySettingsScreenDestination
import com.sorrowblue.comicviewer.feature.settings.folder.navgraphs.FolderSettingsNavGraph
import com.sorrowblue.comicviewer.feature.settings.info.navgraphs.AppInfoSettingsNavGraph
import com.sorrowblue.comicviewer.feature.settings.navgraphs.SettingsDetailNavGraph
import com.sorrowblue.comicviewer.feature.settings.navigation.SettingsDetailGraphDependencies
import com.sorrowblue.comicviewer.feature.settings.navigation.SettingsGraph
import com.sorrowblue.comicviewer.feature.settings.navigation.SettingsGraphTransitions
import com.sorrowblue.comicviewer.feature.settings.section.SettingsListPane
import com.sorrowblue.comicviewer.feature.settings.security.destinations.SecuritySettingsScreenDestination
import com.sorrowblue.comicviewer.feature.settings.viewer.destinations.ViewerSettingsScreenDestination
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons

internal interface SettingsScreenNavigator {
    fun navigateUp()
    fun onStartTutorialClick()
    fun navigateToChangeAuth(enabled: Boolean)
    fun onPasswordChange()
}

@Destination<SettingsGraph>(
    start = true,
    style = SettingsGraphTransitions::class,
    visibility = CodeGenVisibility.INTERNAL
)
@Composable
internal fun SettingsScreen(
    screenNavigator: SettingsScreenNavigator,
    state: SettingsScreenState = rememberSettingsScreenState(),
) {
    val navigator = state.navigator
    SettingsScreen(
        navigator = navigator,
        onBackClick = screenNavigator::navigateUp,
        onSettingsClick = { state.onSettingsClick(it, screenNavigator::onStartTutorialClick) },
    ) {
        DestinationsNavHost(
            navGraph = NavGraphs.settingsDetail,
            start = navigator.currentDestination?.contentKey?.direction
                ?: SettingsDetailNavGraph.defaultStartDirection,
            dependenciesContainerBuilder = {
                SettingsDetailGraphDependencies(navigator, screenNavigator)
            }
        )
    }
}

@Composable
private fun SettingsScreen(
    navigator: ThreePaneScaffoldNavigator<Settings2>,
    onBackClick: () -> Unit,
    onSettingsClick: (Settings2) -> Unit,
    content: @Composable () -> Unit,
) {
    @Suppress("UNCHECKED_CAST")
    NavigableListDetailPaneScaffold(
        navigator = navigator as ThreePaneScaffoldNavigator<Any>,
        detailPane = {
            val modifier = if (navigator.scaffoldDirective.maxHorizontalPartitions == 1) {
                Modifier
            } else {
                Modifier.consumeWindowInsets(WindowInsets.safeDrawing.only(WindowInsetsSides.Start))
            }
            AnimatedPane(modifier = modifier) {
                content()
            }
        },
        listPane = {
            val startPadding = WindowInsets.safeDrawing.asPaddingValues()
                .calculateStartPadding(LocalLayoutDirection.current)
            AnimatedPane(Modifier.preferredWidth(360.dp + startPadding)) {
                SettingsListPane(
                    navigator = navigator,
                    onBackClick = onBackClick,
                    onSettingsClick = onSettingsClick,
                )
            }
        },
    )
}

enum class Settings2(
    val title: Int,
    val icon: ImageVector,
    val direction: Direction? = null,
) {
    DISPLAY(
        R.string.settings_label_display,
        ComicIcons.DisplaySettings,
        DisplaySettingsScreenDestination
    ),
    FOLDER(
        R.string.settings_label_folder,
        ComicIcons.FolderOpen,
        FolderSettingsNavGraph
    ),
    VIEWER(R.string.settings_label_viewer, ComicIcons.Image, ViewerSettingsScreenDestination),
    SECURITY(
        R.string.settings_label_security,
        ComicIcons.Lock,
        SecuritySettingsScreenDestination
    ),
    APP(
        R.string.settings_label_app,
        ComicIcons.Info,
        AppInfoSettingsNavGraph
    ),
    TUTORIAL(R.string.settings_label_tutorial, ComicIcons.Start),

    //    Donation(R.string.settings_label_donation, ComicIcons.Money, DonationScreenDestination),
    Thumbnail(R.string.settings_label_image_cache, ComicIcons.Storage, ImageCacheScreenDestination),
    LANGUAGE(
        R.string.settings_label_language,
        ComicIcons.Language,
        InAppLanguagePickerScreenDestination
    ),
}
