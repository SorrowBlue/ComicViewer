package com.sorrowblue.comicviewer.app

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sorrowblue.comicviewer.domain.EmptyRequest
import com.sorrowblue.comicviewer.domain.model.fold
import com.sorrowblue.comicviewer.domain.usecase.GetNavigationHistoryUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageDisplaySettingsUseCase
import com.sorrowblue.comicviewer.framework.ui.AppState
import com.sorrowblue.comicviewer.framework.ui.navigation.NavItem
import com.sorrowblue.comicviewer.framework.ui.saveable.rememberListSaveable
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Scope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import logcat.LogPriority
import logcat.logcat

@Scope
annotation class ComicViewerAppScope

@GraphExtension(ComicViewerAppScope::class)
interface ComicViewerAppContext {
    val manageDisplaySettingsUseCase: ManageDisplaySettingsUseCase
    val getNavigationHistoryUseCase: GetNavigationHistoryUseCase

    @ContributesTo(AppScope::class)
    @GraphExtension.Factory
    fun interface Factory {
        fun createComicViewerAppContext(): ComicViewerAppContext
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
context(context: ComicViewerAppContext)
internal fun rememberComicViewerAppState(
    sharedTransitionScope: SharedTransitionScope,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    lifecycle: Lifecycle = LocalLifecycleOwner.current.lifecycle,
    scope: CoroutineScope = rememberCoroutineScope(),
    mainViewModel: MainViewModel = viewModel(),
): ComicViewerAppState {
    val navigationSuiteType = NavigationSuiteScaffoldDefaults.navigationSuiteType(
        currentWindowAdaptiveInfo(),
    )
    val comicViewerAppState = rememberListSaveable(
        save = { listOf(it.isNavigationRestored) },
        restore = { isNavigationRestored = it[0] as Boolean },
    ) {
        ComicViewerAppStateImpl(
            lifecycle = lifecycle,
            navigationSuiteType = navigationSuiteType,
            sharedTransitionScope = sharedTransitionScope,
            snackbarHostState = snackbarHostState,
            scope = scope,
            manageDisplaySettingsUseCase = context.manageDisplaySettingsUseCase,
            getNavigationHistoryUseCase = context.getNavigationHistoryUseCase,
            completeInit = {
                mainViewModel.shouldKeepSplash.value = false
                mainViewModel.isInitialized.value = true
            },
        )
    }
    comicViewerAppState.navigationSuiteType = navigationSuiteType
    return comicViewerAppState
}

internal interface ComicViewerAppState : AppState {

    fun onNavigationHistoryRestore()
}

@OptIn(ExperimentalSharedTransitionApi::class)
private class ComicViewerAppStateImpl(
    lifecycle: Lifecycle,
    navigationSuiteType: NavigationSuiteType,
    sharedTransitionScope: SharedTransitionScope,
    override var snackbarHostState: SnackbarHostState,
    private val scope: CoroutineScope,
    private val manageDisplaySettingsUseCase: ManageDisplaySettingsUseCase,
    private val getNavigationHistoryUseCase: GetNavigationHistoryUseCase,
    private val completeInit: () -> Unit,
) : ComicViewerAppState,
    SharedTransitionScope by sharedTransitionScope {
    override val navItems: SnapshotStateList<AppNavItem> = mutableStateListOf()

    override var navigationSuiteType by mutableStateOf(navigationSuiteType)

    override var currentNavItem by mutableStateOf<AppNavItem?>(null)

    override fun onNavItemClick(navItem: NavItem) {
        require(navItem is AppNavItem)
        val navGraph = navItem.navGraph
        logcat { "Clicked navItem.navGraph = $navGraph" }
//        if (navController.currentBackStackEntry?.destination?.hierarchy?.any {
//                it.hasRoute(
//                    navGraph::class,
//                )
//            } != true
//        ) {
//            navController.navigate(
//                navGraph,
//                navOptions {
//                    popUpTo(navController.graph.findStartDestination().route!!) {
//                        saveState = true
//                    }
//                    launchSingleTop = true
//                    restoreState = true
//                },
//            )
//        }
    }

    var isNavigationRestored by mutableStateOf(false)

    init {
        if (!isNavigationRestored) {
            scope.launch {
                if (manageDisplaySettingsUseCase.settings.first().restoreOnLaunch) {
                    cancelJob(scope, 3000, ::completeRestoreHistory, ::restoreNavigation)
                } else {
                    completeRestoreHistory()
                }
            }
        } else {
            completeRestoreHistory()
        }
    }

    private fun restoreNavigation(): Job = scope.launch {
        val history = getNavigationHistoryUseCase(EmptyRequest).first().fold({ it }, { null })
//            navController.navigate(
//                BookshelfNavGraph,
//                navOptions {
//                    popUpTo(ComicViewerAppNavGraph) {
//                        inclusive = true
//                    }
//                }
//            )
        if (history?.folderList.isNullOrEmpty()) {
            completeRestoreHistory()
        } else {
            val (folderList, book) = history.value
            val bookshelfId = folderList.first().bookshelfId
            if (folderList.size == 1) {
//                    navController.navigate(
//                        BookshelfFolder(
//                            bookshelfId = bookshelfId,
//                            path = folderList.first().path,
//                            restorePath = book.path
//                        )
//                    )
                logcat("RESTORE_NAVIGATION", LogPriority.INFO) {
                    "bookshelf(${bookshelfId.value}) -> folder(${folderList.first().path})"
                }
            } else {
//                    navController.navigate(
//                        BookshelfFolder(
//                            bookshelfId = bookshelfId,
//                            path = folderList.first().path,
//                            restorePath = null
//                        )
//                    )
                logcat("RESTORE_NAVIGATION", LogPriority.INFO) {
                    "bookshelf(${bookshelfId.value}) -> folder(${folderList.first().path})"
                }
                folderList.drop(1).dropLast(1).forEach { folder ->
//                        navController.navigate(
//                            BookshelfFolder(
//                                bookshelfId = bookshelfId,
//                                path = folder.path,
//                                restorePath = null
//                            )
//                        )
                    logcat("RESTORE_NAVIGATION", LogPriority.INFO) {
                        "-> folder(${folder.path})"
                    }
                }
//                    navController.navigate(
//                        BookshelfFolder(
//                            bookshelfId = bookshelfId,
//                            path = folderList.last().path,
//                            restorePath = book.path
//                        )
//                    )
                logcat("RESTORE_NAVIGATION", LogPriority.INFO) {
                    "-> folder${folderList.last().path}, ${book.path}"
                }
            }
        }
    }

    override fun onNavigationHistoryRestore() {
        logcat { "onNavigationHistoryRestore" }
        completeRestoreHistory()
    }

    private fun completeRestoreHistory() {
        completeInit()
        isNavigationRestored = true
    }

    /**
     * Cancel job
     *
     * @param scope
     * @param waitTimeMillis
     * @param onCancel
     * @param action
     * @receiver
     * @receiver
     */
    private fun cancelJob(
        scope: CoroutineScope,
        waitTimeMillis: Long,
        onCancel: () -> Unit,
        action: () -> Unit,
    ) {
        val job = scope.launch {
            action()
        }
        scope.launch {
            delay(waitTimeMillis)
            onCancel()
            job.cancel()
        }
    }
}
