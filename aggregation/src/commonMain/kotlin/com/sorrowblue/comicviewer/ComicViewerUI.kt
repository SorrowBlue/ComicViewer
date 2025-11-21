package com.sorrowblue.comicviewer

import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.background
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.adaptive.navigation.BackNavigationBehavior
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberSupportingPaneSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.scene.DialogSceneStrategy
import androidx.navigation3.ui.NavDisplay
import com.sorrowblue.comicviewer.app.PreAppScreen
import com.sorrowblue.comicviewer.app.ProvidesAppState
import com.sorrowblue.comicviewer.app.navigation.appNavigation
import com.sorrowblue.comicviewer.app.rememberPreAppScreenContext
import com.sorrowblue.comicviewer.framework.common.LocalPlatformContext
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import com.sorrowblue.comicviewer.framework.common.platformGraph
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.AdaptiveNavigationSuiteState
import com.sorrowblue.comicviewer.framework.ui.LocalAdaptiveNavigationSuiteState
import com.sorrowblue.comicviewer.framework.ui.LocalAppState
import com.sorrowblue.comicviewer.framework.ui.LocalNavigationState
import com.sorrowblue.comicviewer.framework.ui.LocalSharedTransitionScope
import com.sorrowblue.comicviewer.framework.ui.navigation.NavigationState
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import com.sorrowblue.comicviewer.framework.ui.navigation.toEntries

@Composable
context(context: PlatformContext)
fun ComicViewerUI(state: ComicViewerUIState, finishApp: () -> Unit) {
    CompositionLocalProvider(LocalPlatformContext provides context) {
        ComicTheme {
            with(rememberPreAppScreenContext()) {
                PreAppScreen(
                    finishApp = finishApp,
                ) {
                    ComicViewerUI(
                        adaptiveNavigationSuiteState = state.adaptiveNavigationSuiteState,
                        navigationState = state.navigationState,
                        navigator = state.navigator,
                        onBookshelfFolderRestored = state::onNavigationHistoryRestore,
                    )
                }
            }
        }
    }
}

@Composable
private fun ComicViewerUI(
    adaptiveNavigationSuiteState: AdaptiveNavigationSuiteState,
    navigationState: NavigationState,
    navigator: Navigator,
    onBookshelfFolderRestored: () -> Unit,
) {
    SharedTransitionLayout(modifier = Modifier.background(ComicTheme.colorScheme.background)) {
        CompositionLocalProvider(
            ProvidesAppState,
            LocalSharedTransitionScope provides this,
            LocalAdaptiveNavigationSuiteState provides adaptiveNavigationSuiteState,
            LocalNavigationState provides navigationState,
        ) {
            Scaffold(
                snackbarHost = {
                    SnackbarHost(LocalAppState.current.snackbarHostState)
                },
            ) {
                val platformGraph = LocalPlatformContext.current.platformGraph
                val supportingPaneSceneStrategy =
                    rememberSupportingPaneSceneStrategy<NavKey>(
                        backNavigationBehavior = BackNavigationBehavior.PopUntilContentChange,
                    )

                val listDetailSceneStrategy = rememberListDetailSceneStrategy<NavKey>()
                val dialogSceneStrategy = remember { DialogSceneStrategy<NavKey>() }
                val entryProvider = entryProvider {
                    with(platformGraph) {
                        appNavigation(
                            navigator = navigator,
                            onBookshelfFolderRestored = onBookshelfFolderRestored,
                        )
                    }
                }
                NavDisplay(
                    entries = navigationState.toEntries(entryProvider),
                    onBack = { navigator.goBack() },
                    sceneStrategy = supportingPaneSceneStrategy
                        .then(listDetailSceneStrategy)
                        .then(dialogSceneStrategy),
                )
            }
        }
    }
}
