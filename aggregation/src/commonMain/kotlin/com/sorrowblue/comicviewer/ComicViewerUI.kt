package com.sorrowblue.comicviewer

import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.background
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.adaptive.navigation.BackNavigationBehavior
import androidx.compose.material3.adaptive.navigation3.kmp.rememberListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.kmp.rememberSupportingPaneSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
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
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigation3State
import io.github.irgaly.navigation3.resultstate.rememberNavigationResultNavEntryDecorator

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
                        navigation3State = state.navigation3State,
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
    navigation3State: Navigation3State,
    onBookshelfFolderRestored: () -> Unit,
) {
    SharedTransitionLayout(modifier = Modifier.background(ComicTheme.colorScheme.background)) {
        CompositionLocalProvider(
            ProvidesAppState,
            LocalAdaptiveNavigationSuiteState provides adaptiveNavigationSuiteState,
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
                        with(navigation3State) {
                            appNavigation(
                                onBookshelfFolderRestored = onBookshelfFolderRestored,
                            )
                        }
                    }
                }
                NavDisplay(
                    entryDecorators = listOf(
                        rememberNavigationResultNavEntryDecorator(
                            backStack = navigation3State.currentBackStack,
                            entryProvider = entryProvider,
                        ),
                        rememberSaveableStateHolderNavEntryDecorator(),
                        rememberViewModelStoreNavEntryDecorator(),
                    ),
                    sceneStrategy = supportingPaneSceneStrategy
                        .then(listDetailSceneStrategy)
                        .then(dialogSceneStrategy),
                    backStack = navigation3State.currentBackStack,
                    entryProvider = entryProvider,
                )
            }
        }
    }
}
