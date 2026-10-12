/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.app

import androidx.compose.material3.adaptive.navigationsuite.rememberNavigationSuiteScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sorrowblue.comicviewer.feature.folder.nav.FolderNavKey
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import com.sorrowblue.comicviewer.framework.common.appGraph
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.navigation.AppContentDecoratorProvider
import com.sorrowblue.comicviewer.framework.navigation.Navigator
import com.sorrowblue.comicviewer.framework.navigation.NestedAppContents
import com.sorrowblue.comicviewer.framework.ui.EventEffect
import com.sorrowblue.comicviewer.framework.ui.LocalFinishApp
import com.sorrowblue.comicviewer.framework.ui.locale.ProvideLocalAppLocaleIso
import com.sorrowblue.comicviewer.framework.ui.navigation3.LocalNavigator
import dev.zacsweers.metrox.viewmodel.metroViewModel
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList

@Composable
context(context: PlatformContext)
internal fun ComicViewerApp(
    finishApp: () -> Unit,
    navigator: Navigator = rememberAppNavigator(),
    allowNavigationRestored: Boolean = true,
    viewModel: ComicViewerAppViewModel = metroViewModel<ComicViewerAppViewModel>(),
) {
    CompositionLocalProvider(
        LocalNavigator provides navigator,
        LocalFinishApp provides finishApp,
        ProvidesAppState,
        ProvideLocalAppLocaleIso,
    ) {
        ComicTheme {
            val isInitialized by viewModel.isInitializedNavigation.collectAsStateWithLifecycle()
            val appContentDecorators = rememberAppContentDecoratorProviders()
            NestedAppContents(
                list = appContentDecorators,
                isInitialized = isInitialized,
                onInitialize = viewModel::completeScreenInitialize,
            ) {
                ComicViewerApp(navigator = navigator)
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
context(context: PlatformContext)
private fun ComicViewerApp(navigator: Navigator) {
    val entryProvider = rememberEntryProvider(navigator)
    val state = rememberNavigationSuiteScaffoldState()
    val fabState = rememberAppFloatingActionBarState(
        entryProvider = entryProvider,
        navigator = navigator,
    )
    CompositionLocalProvider(fabState.provides) {
        AppNavigationSuiteScaffold(
            state = state,
            railActionContent = {
                FloatingActionContainer(
                    state = fabState,
                    isRail = true,
                )
            },
            bottomActionContent = {
                FloatingActionContainer(
                    state = fabState,
                    isRail = false,
                )
            },
        ) {
            ComicViewerNavDisplay(
                navigator = navigator,
                entryProvider = entryProvider,
            )
        }
    }
}

@Composable
context(context: PlatformContext)
private fun rememberAppContentDecoratorProviders(): PersistentList<AppContentDecoratorProvider> =
    remember(context) {
        context.appGraph<NavigationGraph>().appContentDecoratorProviders
            .sortedBy(AppContentDecoratorProvider::order)
            .toPersistentList()
    }

@Composable
@NonRestartableComposable
private fun NestedAppContents(
    list: PersistentList<AppContentDecoratorProvider>,
    isInitialized: Boolean,
    onInitialize: () -> Unit,
    content: @Composable () -> Unit,
) {
    list.NestedAppContents(
        isInitialized = isInitialized,
        onInitialize = onInitialize,
        content = content,
    )
}
