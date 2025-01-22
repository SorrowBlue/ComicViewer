package com.sorrowblue.comicviewer.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.viewmodel.compose.saveable
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.sorrowblue.comicviewer.MainViewModel
import com.sorrowblue.comicviewer.app.component.ComicViewerScaffoldUiState
import com.sorrowblue.comicviewer.app.component.MainScreenTab
import com.sorrowblue.comicviewer.app.navigation.ComicViewerAppNavGraph
import com.sorrowblue.comicviewer.domain.EmptyRequest
import com.sorrowblue.comicviewer.domain.model.fold
import com.sorrowblue.comicviewer.domain.usecase.GetNavigationHistoryUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageDisplaySettingsUseCase
import com.sorrowblue.comicviewer.feature.bookshelf.BookshelfFolder
import com.sorrowblue.comicviewer.feature.bookshelf.navgraph.BookshelfNavGraph
import com.sorrowblue.comicviewer.framework.ui.EventFlow
import com.sorrowblue.comicviewer.framework.ui.SaveableScreenState
import com.sorrowblue.comicviewer.framework.ui.navigation.NavTabHandler
import com.sorrowblue.comicviewer.framework.ui.rememberSaveableScreenState
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

internal sealed interface ComicViewerAppEvent {

    data class Navigate(
        val route: Any,
        val navOptions: NavOptions? = null,
    ) : ComicViewerAppEvent
}

@Composable
internal fun rememberComicViewerAppState(
    lifecycle: Lifecycle = LocalLifecycleOwner.current.lifecycle,
    scope: CoroutineScope = rememberCoroutineScope(),
    navTabHandler: NavTabHandler = koinInject(),
    manageDisplaySettingsUseCase: ManageDisplaySettingsUseCase = koinInject(),
    getNavigationHistoryUseCase: GetNavigationHistoryUseCase = koinInject(),
    mainViewModel: MainViewModel = koinViewModel(),
    navController: NavHostController = rememberNavController(),
): ComicViewerAppState {
    return rememberSaveableScreenState {
        ComicViewerAppStateImpl(
            lifecycle = lifecycle,
            mainViewModel = mainViewModel,
            scope = scope,
            navTabHandler = navTabHandler,
            manageDisplaySettingsUseCase = manageDisplaySettingsUseCase,
            getNavigationHistoryUseCase = getNavigationHistoryUseCase,
            savedStateHandle = it,
            navController = navController,
        )
    }
}

internal interface ComicViewerAppState : SaveableScreenState {

    val navController: NavHostController
    val uiState: ComicViewerScaffoldUiState
    val events: EventFlow<ComicViewerAppEvent>

    fun onTabSelect(tab: MainScreenTab)
    fun onNavigationHistoryRestore()
    fun refreshAddOnList()
}

private class ComicViewerAppStateImpl(
    lifecycle: Lifecycle,
    private val mainViewModel: MainViewModel,
    private val scope: CoroutineScope,
    private val navTabHandler: NavTabHandler,
    private val manageDisplaySettingsUseCase: ManageDisplaySettingsUseCase,
    private val getNavigationHistoryUseCase: GetNavigationHistoryUseCase,
    override val savedStateHandle: SavedStateHandle,
    override val navController: NavHostController,
) :
    ComicViewerAppState {

    override var uiState: ComicViewerScaffoldUiState by mutableStateOf(ComicViewerScaffoldUiState())
        private set

    override val events = EventFlow<ComicViewerAppEvent>()

    private var isNavigationRestored by savedStateHandle.saveable { mutableStateOf(false) }

    init {
        refreshAddOnList()
        navController.currentBackStackEntryFlow
            .filter { it.destination is ComposeNavigator.Destination }
            .onEach { backStackEntry ->
                val hierarchy = backStackEntry.destination.hierarchy
                val currentTab = MainScreenTab.entries.find { tab ->
                    hierarchy.any { it.hasRoute(tab.navGraph::class) }
                }
                if (currentTab == null) {
//                    delay(500)
                }
                if (uiState.currentTab == null && currentTab != null) {
                    delay(250)
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
            events.tryEmit(
                ComicViewerAppEvent.Navigate(
                    navGraph,
                    navOptions {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                )
            )
        }
    }

    private fun restoreNavigation(): Job {
        return scope.launch {
            val history = getNavigationHistoryUseCase(EmptyRequest).first().fold({ it }, { null })
            events.tryEmit(
                ComicViewerAppEvent.Navigate(
                    BookshelfNavGraph,
                    navOptions {
                        popUpTo(ComicViewerAppNavGraph) {
                            inclusive = true
                        }
                    }
                )
            )
            if (history?.folderList.isNullOrEmpty()) {
                completeRestoreHistory()
            } else {
                val (folderList, book) = history!!.value
                val bookshelfId = folderList.first().bookshelfId
                if (folderList.size == 1) {
                    events.tryEmit(
                        ComicViewerAppEvent.Navigate(
                            BookshelfFolder(
                                bookshelfId = bookshelfId,
                                path = folderList.first().path,
                                restorePath = book.path
                            )
                        )
                    )
                    logcat("RESTORE_NAVIGATION", LogPriority.INFO) {
                        "bookshelf(${bookshelfId.value}) -> folder(${folderList.first().path})"
                    }
                } else {
                    events.tryEmit(
                        ComicViewerAppEvent.Navigate(
                            BookshelfFolder(
                                bookshelfId = bookshelfId,
                                path = folderList.first().path,
                                restorePath = null
                            )
                        )
                    )
                    logcat("RESTORE_NAVIGATION", LogPriority.INFO) {
                        "bookshelf(${bookshelfId.value}) -> folder(${folderList.first().path})"
                    }
                    folderList.drop(1).dropLast(1).forEach { folder ->
                        events.tryEmit(
                            ComicViewerAppEvent.Navigate(
                                BookshelfFolder(
                                    bookshelfId = bookshelfId,
                                    path = folder.path,
                                    restorePath = null
                                )
                            )
                        )
                        logcat("RESTORE_NAVIGATION", LogPriority.INFO) {
                            "-> folder(${folder.path})"
                        }
                    }

                    events.tryEmit(
                        ComicViewerAppEvent.Navigate(
                            BookshelfFolder(
                                bookshelfId = bookshelfId,
                                path = folderList.last().path,
                                restorePath = book.path
                            )
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
        mainViewModel.shouldKeepSplash.value = false
        mainViewModel.isInitialized.value = true
        isNavigationRestored = true
    }

    override fun refreshAddOnList() {
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
