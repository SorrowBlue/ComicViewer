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
import com.sorrowblue.comicviewer.app.AppNavItem.Bookshelf
import com.sorrowblue.comicviewer.app.AppNavItem.History
import com.sorrowblue.comicviewer.app.AppNavItem.Readlater
import com.sorrowblue.comicviewer.app.navigation.ComicViewerAppNavGraph
import com.sorrowblue.comicviewer.domain.EmptyRequest
import com.sorrowblue.comicviewer.domain.model.fold
import com.sorrowblue.comicviewer.domain.usecase.GetNavigationHistoryUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageDisplaySettingsUseCase
import com.sorrowblue.comicviewer.feature.bookshelf.BookshelfFolder
import com.sorrowblue.comicviewer.feature.bookshelf.navgraph.BookshelfNavGraph
import com.sorrowblue.comicviewer.framework.ui.AppState
import com.sorrowblue.comicviewer.framework.ui.navigation.NavItem
import com.sorrowblue.comicviewer.framework.ui.navigation.TabDisplayRoute
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
import org.koin.core.component.KoinComponent

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
internal fun rememberComicViewerAppState(
    sharedTransitionScope: SharedTransitionScope,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    lifecycle: Lifecycle = LocalLifecycleOwner.current.lifecycle,
    scope: CoroutineScope = rememberCoroutineScope(),
    manageDisplaySettingsUseCase: ManageDisplaySettingsUseCase = koinInject(),
    getNavigationHistoryUseCase: GetNavigationHistoryUseCase = koinInject(),
    mainViewModel: MainViewModel = koinViewModel(),
    navController: NavHostController = rememberNavController(),
): ComicViewerAppState {
    val navigationSuiteType = NavigationSuiteScaffoldDefaults.navigationSuiteType(
        currentWindowAdaptiveInfo()
    )
    val comicViewerAppState = rememberListSaveable(
        save = { listOf(it.isNavigationRestored) },
        restore = { isNavigationRestored = it[0] as Boolean }
    ) {
        ComicViewerAppStateImpl(
            lifecycle = lifecycle,
            navigationSuiteType = navigationSuiteType,
            sharedTransitionScope = sharedTransitionScope,
            snackbarHostState = snackbarHostState,
            scope = scope,
            manageDisplaySettingsUseCase = manageDisplaySettingsUseCase,
            getNavigationHistoryUseCase = getNavigationHistoryUseCase,
            completeInit = {
                mainViewModel.shouldKeepSplash.value = false
                mainViewModel.isInitialized.value = true
            },
            navController = navController,
        )
    }
    comicViewerAppState.navigationSuiteType = navigationSuiteType
    return comicViewerAppState
}

internal interface ComicViewerAppState : AppState {

    val navController: NavHostController
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
    override val navController: NavHostController,
) : ComicViewerAppState, KoinComponent, SharedTransitionScope by sharedTransitionScope {

    override val navItems = mutableStateListOf(Bookshelf, AppNavItem.Collection, Readlater, History)

    override var navigationSuiteType by mutableStateOf(navigationSuiteType)

    override var currentNavItem by mutableStateOf<AppNavItem?>(null)

    override fun onNavItemClick(navItem: NavItem) {
        require(navItem is AppNavItem)
        val navGraph = navItem.navGraph
        logcat { "Clicked navItem.navGraph = $navGraph" }
        if (navController.currentBackStackEntry?.destination?.hierarchy?.any { it.hasRoute(navGraph::class) } != true) {
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

    var isNavigationRestored by mutableStateOf(false)

    init {
        val tabDisplayRoutes = getKoin().getAll<TabDisplayRoute>().flatMap(TabDisplayRoute::routes)
        logcat { "init" }
        navController.currentBackStackEntryFlow
            .filter { it.destination is ComposeNavigator.Destination }
            .onEach { backStackEntry ->
                val hierarchy = backStackEntry.destination.hierarchy
                val currentTab = navItems.find { tab ->
                    hierarchy.any { destination ->
                        tabDisplayRoutes.any { destination.hasRoute(it) } &&
                            destination.parent?.hasRoute(tab.navGraph::class) == true
                    }
                }
                if (currentNavItem == null && currentTab != null) {
                    // 画面が更新されてからNavigationを表示します。
                    delay(250)
                }
                currentNavItem = currentTab
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
