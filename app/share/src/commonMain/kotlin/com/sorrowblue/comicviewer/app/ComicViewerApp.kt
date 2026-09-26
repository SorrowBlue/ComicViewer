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
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.scene.DialogSceneStrategy
import androidx.navigation3.ui.NavDisplay
import com.sorrowblue.comicviewer.feature.folder.nav.FolderNavKey
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import com.sorrowblue.comicviewer.framework.common.appGraph
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.navigation.Navigator
import com.sorrowblue.comicviewer.framework.navigation.NestedAppContents
import com.sorrowblue.comicviewer.framework.ui.EventEffect
import com.sorrowblue.comicviewer.framework.ui.LocalAppState
import com.sorrowblue.comicviewer.framework.ui.LocalFinishApp
import com.sorrowblue.comicviewer.framework.ui.animation.LocalSharedTransitionScope
import com.sorrowblue.comicviewer.framework.ui.animation.Transitions
import com.sorrowblue.comicviewer.framework.ui.locale.ProvideLocalAppLocaleIso
import com.sorrowblue.comicviewer.framework.ui.navigation3.LocalNavigator
import dev.zacsweers.metrox.viewmodel.metroViewModel
import logcat.logcat

@Composable
context(context: PlatformContext)
internal fun ComicViewerApp(
    finishApp: () -> Unit,
    navigator: Navigator = rememberAppNavigator(),
    allowNavigationRestored: Boolean = true,
    viewModel: ComicViewerAppViewModel = metroViewModel<ComicViewerAppViewModel>(),
) {
    val entryProvider = entryProvider {
        context.appGraph<NavigationGraph>().navigationEntryProviders.forEach { provider ->
            provider(navigator = navigator)
        }
    }
    val navEntryDecorators: @Composable () -> List<NavEntryDecorator<NavKey>> = {
        context.appGraph<NavigationGraph>().navEntryDecoratorProviders.map {
            it.rememberNavEntryDecorator()
        }
    }
    val appContentDecorators = context.appGraph<NavigationGraph>().appContentDecoratorProviders
    CompositionLocalProvider(
        LocalNavigator provides navigator,
        LocalFinishApp provides finishApp,
        ProvidesAppState,
        ProvideLocalAppLocaleIso,
    ) {
        ComicTheme {
            val isInitialized by viewModel.isInitializedNavigation.collectAsStateWithLifecycle()
            val sortedAppContents =
                remember(appContentDecorators) { appContentDecorators.sortedBy { it.order } }
            sortedAppContents.NestedAppContents(
                isInitialized = isInitialized,
                onInitialize = viewModel::completeScreenInitialize,
            ) {
                ComicViewerApp(
                    navigator = navigator,
                    entryProvider = entryProvider,
                    navEntryDecorators = navEntryDecorators,
                )
                SideEffect(Unit) {
                    viewModel.restore(allowNavigationRestored)
                }
                EventEffect(viewModel.restoreNavigation) {
                    navigator.navigate(
                        FolderNavKey(
                            bookshelfId = it.bookshelfId,
                            path = it.path,
                            restorePath = it.restorePath,
                            showSearch = true,
                            onRestoreComplete = it.onRestoreComplete,
                        ),
                    )
                }
            }
        }
    }
}

@Composable
private fun ComicViewerApp(
    navigator: Navigator,
    entryProvider: (NavKey) -> NavEntry<NavKey>,
    navEntryDecorators: @Composable () -> List<NavEntryDecorator<NavKey>>,
) {
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
                val sceneStrategies = remember(
                    supportingPaneSceneStrategy,
                    listDetailSceneStrategy,
                    dialogSceneStrategy,
                ) {
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
                        entryDecorators = navEntryDecorators(),
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
