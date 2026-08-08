/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.app

import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.background
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation.BackNavigationBehavior
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberSupportingPaneSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.scene.DialogSceneStrategy
import androidx.navigation3.ui.NavDisplay
import com.sorrowblue.comicviewer.app.wrapper.PreAppScreen
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.LocalAppState
import com.sorrowblue.comicviewer.framework.ui.animation.LocalSharedTransitionScope
import com.sorrowblue.comicviewer.framework.ui.animation.Transitions
import com.sorrowblue.comicviewer.framework.ui.locale.ProvideLocalAppLocaleIso
import com.sorrowblue.comicviewer.framework.ui.navigation.LocalNavigator
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import com.sorrowblue.comicviewer.framework.ui.navigation3.rememberSupportingPaneWindowInsetsDecorator
import io.github.irgaly.navigation3.resultstate.rememberNavigationResultNavEntryDecorator
import logcat.logcat

@Composable
context(appGraph: AppGraph)
internal fun ComicViewerApp(state: ComicViewerAppState, finishApp: () -> Unit) {
    CompositionLocalProvider(
        LocalNavigator provides state.navigator,
        ProvidesAppState,
        ProvideLocalAppLocaleIso,
    ) {
        ComicTheme {
            PreAppScreen(finishApp = finishApp) {
                ComicViewerApp(
                    navigator = state.navigator,
                    entryProvider = state.entryProvider,
                )
            }
        }
    }
}

@Composable
private fun ComicViewerApp(navigator: Navigator, entryProvider: (NavKey) -> NavEntry<NavKey>) {
    val appState = LocalAppState.current
    val lifecycle = LocalLifecycleOwner.current
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(appState) {
        appState.snackbarEvents.flowWithLifecycle(lifecycle.lifecycle).collect { event ->
            val result = snackbarHostState.showSnackbar(
                message = event.message,
                actionLabel = event.actionLabel,
                duration = event.duration,
                withDismissAction = event.withDismissAction,
            )
            if (result == SnackbarResult.ActionPerformed) {
                event.onActionPerformed?.invoke()
            }
        }
    }
    SharedTransitionLayout(modifier = Modifier.background(ComicTheme.colorScheme.background)) {
        CompositionLocalProvider(LocalSharedTransitionScope provides this) {
            Scaffold(
                snackbarHost = {
                    SnackbarHost(snackbarHostState)
                },
            ) {
                val directive = calculatePaneScaffoldDirective(currentWindowAdaptiveInfoV2())
                val supportingPaneSceneStrategy =
                    rememberSupportingPaneSceneStrategy<NavKey>(
                        backNavigationBehavior = BackNavigationBehavior.PopUntilContentChange,
                        directive = directive,
                    )
                val listDetailSceneStrategy =
                    rememberListDetailSceneStrategy<NavKey>(
                        backNavigationBehavior = BackNavigationBehavior.PopUntilContentChange,
                        directive = directive,
                    )
                val dialogSceneStrategy = remember { DialogSceneStrategy<NavKey>() }
                val windowInsetsDecorator =
                    rememberSupportingPaneWindowInsetsDecorator<NavKey>(directive = directive)
                val sceneStrategies = remember {
                    listOf(
                        supportingPaneSceneStrategy,
                        listDetailSceneStrategy,
                        dialogSceneStrategy,
                    )
                }
                Transitions.InitSlideDistance()
                Transitions.motionScheme = ComicTheme.motionScheme
                NavDisplay(
                    entries = navigator.state.toDecoratedEntries(
                        entryDecorators = listOf(
                            rememberSaveableStateHolderNavEntryDecorator(),
                            rememberNavigationResultNavEntryDecorator(
                                backStack = navigator.backStack,
                                entryProvider = entryProvider,
                            ),
                            rememberViewModelStoreNavEntryDecorator(),
                            windowInsetsDecorator,
                        ),
                        entryProvider = entryProvider,
                    ),
                    sceneStrategies = sceneStrategies,
                    onBack = {
                        logcat("Navigator") { "ComicViewerUI: #onBack" }
                        navigator.goBack()
                    },
                )
            }
        }
    }
}
