package com.sorrowblue.comicviewer

import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.background
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation.BackNavigationBehavior
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberSupportingPaneSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.scene.DialogSceneStrategy
import androidx.navigation3.ui.NavDisplay
import com.sorrowblue.comicviewer.app.AppGraph
import com.sorrowblue.comicviewer.app.PreAppScreen
import com.sorrowblue.comicviewer.app.ProvidesAppState
import com.sorrowblue.comicviewer.framework.common.providePlatformContext
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.LocalAppState
import com.sorrowblue.comicviewer.framework.ui.LocalSharedTransitionScope
import com.sorrowblue.comicviewer.framework.ui.animation.Transitions
import com.sorrowblue.comicviewer.framework.ui.navigation.LocalNavigator
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import com.sorrowblue.comicviewer.framework.ui.navigation.toEntries
import com.sorrowblue.comicviewer.framework.ui.navigation3.rememberSupportingPaneWindowInsetsDecorator
import io.github.irgaly.navigation3.resultstate.rememberNavigationResultNavEntryDecorator
import io.github.takahirom.rin.rememberRetained

@Composable
context(appGraph: AppGraph)
fun ComicViewerUI(state: ComicViewerUIState, finishApp: () -> Unit) {
    CompositionLocalProvider(providePlatformContext(appGraph.context)) {
        ComicTheme {
            with(rememberRetained { appGraph.createPreAppScreenContext() }) {
                PreAppScreen(finishApp = finishApp) {
                    ComicViewerUI(
                        navigator = state.navigator,
                        entryProvider = entryProvider {
                            appGraph.entries.forEach { it(state.navigator) }
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun ComicViewerUI(navigator: Navigator, entryProvider: (NavKey) -> NavEntry<NavKey>) {
    SharedTransitionLayout(modifier = Modifier.background(ComicTheme.colorScheme.background)) {
        CompositionLocalProvider(
            ProvidesAppState,
            LocalSharedTransitionScope provides this,
            LocalNavigator provides navigator,
        ) {
            Scaffold(
                snackbarHost = {
                    SnackbarHost(LocalAppState.current.snackbarHostState)
                },
            ) {
                val directive = calculatePaneScaffoldDirective(currentWindowAdaptiveInfo())
                val supportingPaneSceneStrategy =
                    rememberSupportingPaneSceneStrategy<NavKey>(
                        backNavigationBehavior = BackNavigationBehavior.PopUntilContentChange,
                        directive = directive,
                    )
                val listDetailSceneStrategy =
                    rememberListDetailSceneStrategy<NavKey>(directive = directive)
                val dialogSceneStrategy = remember { DialogSceneStrategy<NavKey>() }
                val customNavEntryDecorator =
                    rememberSupportingPaneWindowInsetsDecorator<NavKey>(directive = directive)
                Transitions.InitSlideDistance()
                Transitions.motionScheme = ComicTheme.motionScheme
                NavDisplay(
                    entries = navigator.state.toEntries(
                        entryDecorators = listOf(
                            rememberSaveableStateHolderNavEntryDecorator(),
                            rememberNavigationResultNavEntryDecorator(
                                backStack = navigator.backStack,
                                entryProvider = entryProvider,
                            ),
                            rememberViewModelStoreNavEntryDecorator(),
                            customNavEntryDecorator,
                        ),
                        entryProvider = entryProvider,
                    ),
                    sharedTransitionScope = this,
                    onBack = { navigator.goBack() },
                    sceneStrategy = supportingPaneSceneStrategy
                        .then(listDetailSceneStrategy)
                        .then(dialogSceneStrategy),
                )
            }
        }
    }
}
