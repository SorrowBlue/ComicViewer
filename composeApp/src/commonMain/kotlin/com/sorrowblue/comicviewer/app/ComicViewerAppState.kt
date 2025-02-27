package com.sorrowblue.comicviewer.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.sorrowblue.comicviewer.app.component.ComicViewerScaffoldUiState
import com.sorrowblue.comicviewer.app.component.MainScreenTab
import com.sorrowblue.comicviewer.app.navigation.ComicViewerAppNavGraph
import com.sorrowblue.comicviewer.domain.EmptyRequest
import com.sorrowblue.comicviewer.domain.model.fold
import com.sorrowblue.comicviewer.domain.usecase.GetNavigationHistoryUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageDisplaySettingsUseCase
import com.sorrowblue.comicviewer.feature.bookshelf.BookshelfFolder
import com.sorrowblue.comicviewer.feature.bookshelf.navgraph.BookshelfNavGraph
import com.sorrowblue.comicviewer.framework.ui.navigation.NavTabHandler
import com.sorrowblue.comicviewer.framework.ui.saveable.rememberListSaveable
import com.sorrowblue.comicviewer.framework.ui.sharedKoinViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import logcat.LogPriority
import logcat.logcat
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun rememberComicViewerAppState(
    lifecycle: Lifecycle = LocalLifecycleOwner.current.lifecycle,
    scope: CoroutineScope = rememberCoroutineScope(),
    navTabHandler: NavTabHandler = sharedKoinViewModel(),
    manageDisplaySettingsUseCase: ManageDisplaySettingsUseCase = koinInject(),
    getNavigationHistoryUseCase: GetNavigationHistoryUseCase = koinInject(),
    mainViewModel: MainViewModel = koinViewModel(),
    navController: NavHostController = rememberNavController(),
): ComicViewerAppState {
    return rememberListSaveable(
        save = { listOf(it.isNavigationRestored) },
        restore = { isNavigationRestored = it[0] as Boolean }
    ) {
        ComicViewerAppStateImpl(
            lifecycle = lifecycle,
            scope = scope,
            navTabHandler = navTabHandler,
            manageDisplaySettingsUseCase = manageDisplaySettingsUseCase,
            getNavigationHistoryUseCase = getNavigationHistoryUseCase,
            completeInit = {
                mainViewModel.shouldKeepSplash.value = false
                mainViewModel.isInitialized.value = true
            },
            navController = navController,
        )
    }
}

internal interface ComicViewerAppState {

    val navController: NavHostController
    val uiState: ComicViewerScaffoldUiState
    fun onTabSelect(tab: MainScreenTab)
    fun onNavigationHistoryRestore()
}

private class ComicViewerAppStateImpl(
    lifecycle: Lifecycle,
    private val scope: CoroutineScope,
    private val navTabHandler: NavTabHandler,
    private val manageDisplaySettingsUseCase: ManageDisplaySettingsUseCase,
    private val getNavigationHistoryUseCase: GetNavigationHistoryUseCase,
    private val completeInit: () -> Unit,
    override val navController: NavHostController,
) : ComicViewerAppState {

    override var uiState: ComicViewerScaffoldUiState by mutableStateOf(ComicViewerScaffoldUiState())
        private set

    var isNavigationRestored by mutableStateOf(false)

    init {
        logcat { "init" }
        navController.currentBackStackEntryFlow
            .filter { it.destination is ComposeNavigator.Destination }
            .onEach { backStackEntry ->
                val hierarchy = backStackEntry.destination.hierarchy
                val currentTab = MainScreenTab.entries.find { tab ->
                    hierarchy.any { it.hasRoute(tab.navGraph::class) }
                }
                if (uiState.currentTab == null && currentTab != null) {
                    // 画面が更新されてからNavigationを表示します。
                    // TODO delay(250)
                }
                uiState = uiState.copy(currentTab = currentTab)
                logcat {
                    "destination.hierarchy=${
                        backStackEntry.destination.hierarchy.joinToString(",") {
                            it.route?.split('/')?.firstOrNull().orEmpty().ifEmpty { "null" }
                        }
                    }"
                }
            }.flowWithLifecycle(lifecycle)
            .launchIn(scope)
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

    override fun onTabSelect(tab: MainScreenTab) {
        val navGraph = tab.navGraph
        logcat { "Selected navGraph = $navGraph" }
        if (navController.currentBackStackEntry?.destination?.hierarchy?.any { it.hasRoute(navGraph::class) } == true) {
            navTabHandler.click.tryEmit(Unit)
        } else {
            navController.navigate(
                navGraph,
                navOptions {
                    popUpTo(navController.graph.findStartDestination().route!!) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            )
        }
    }

    private fun restoreNavigation(): Job {
        return scope.launch {
            val history = getNavigationHistoryUseCase(EmptyRequest).first().fold({ it }, { null })
            navController.navigate(
                BookshelfNavGraph,
                navOptions {
                    popUpTo(ComicViewerAppNavGraph) {
                        inclusive = true
                    }
                }
            )
            if (history?.folderList.isNullOrEmpty()) {
                completeRestoreHistory()
            } else {
                val (folderList, book) = history.value
                val bookshelfId = folderList.first().bookshelfId
                if (folderList.size == 1) {
                    navController.navigate(
                        BookshelfFolder(
                            bookshelfId = bookshelfId,
                            path = folderList.first().path,
                            restorePath = book.path
                        )
                    )
                    logcat("RESTORE_NAVIGATION", LogPriority.INFO) {
                        "bookshelf(${bookshelfId.value}) -> folder(${folderList.first().path})"
                    }
                } else {
                    navController.navigate(
                        BookshelfFolder(
                            bookshelfId = bookshelfId,
                            path = folderList.first().path,
                            restorePath = null
                        )
                    )
                    logcat("RESTORE_NAVIGATION", LogPriority.INFO) {
                        "bookshelf(${bookshelfId.value}) -> folder(${folderList.first().path})"
                    }
                    folderList.drop(1).dropLast(1).forEach { folder ->
                        navController.navigate(
                            BookshelfFolder(
                                bookshelfId = bookshelfId,
                                path = folder.path,
                                restorePath = null
                            )
                        )
                        logcat("RESTORE_NAVIGATION", LogPriority.INFO) {
                            "-> folder(${folder.path})"
                        }
                    }
                    navController.navigate(
                        BookshelfFolder(
                            bookshelfId = bookshelfId,
                            path = folderList.last().path,
                            restorePath = book.path
                        )
                    )
                    logcat("RESTORE_NAVIGATION", LogPriority.INFO) {
                        "-> folder${folderList.last().path}, ${book.path}"
                    }
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
