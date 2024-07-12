package com.sorrowblue.comicviewer.app

import android.annotation.SuppressLint
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.ramcosta.composedestinations.spec.Direction
import com.ramcosta.composedestinations.utils.findDestination
import com.ramcosta.composedestinations.utils.toDestinationsNavigator
import com.sorrowblue.comicviewer.app.component.ComicViewerScaffoldUiState
import com.sorrowblue.comicviewer.app.component.MainScreenTab
import com.sorrowblue.comicviewer.app.navgraphs.MainNavGraph
import com.sorrowblue.comicviewer.domain.EmptyRequest
import com.sorrowblue.comicviewer.domain.model.AddOn
import com.sorrowblue.comicviewer.domain.model.fold
import com.sorrowblue.comicviewer.domain.usecase.GetInstalledModulesUseCase
import com.sorrowblue.comicviewer.domain.usecase.GetNavigationHistoryUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageDisplaySettingsUseCase
import com.sorrowblue.comicviewer.feature.bookshelf.destinations.BookshelfFolderScreenDestination
import com.sorrowblue.comicviewer.feature.bookshelf.navgraphs.BookshelfNavGraph
import com.sorrowblue.comicviewer.framework.ui.DestinationTransitions
import com.sorrowblue.comicviewer.framework.ui.NavTabHandler
import com.sorrowblue.comicviewer.framework.ui.SaveableScreenState
import com.sorrowblue.comicviewer.framework.ui.rememberSaveableScreenState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import logcat.LogPriority
import logcat.logcat

internal interface ComicViewerAppState : SaveableScreenState {

    val navController: NavHostController
    val uiState: ComicViewerScaffoldUiState
    val addOnList: SnapshotStateList<AddOn>

    fun onTabSelect(tab: MainScreenTab)
    fun onNavigationHistoryRestore()
    fun refreshAddOnList()
}

@Composable
internal fun rememberComicViewerAppState(
    scope: CoroutineScope = rememberCoroutineScope(),
    lifecycle: Lifecycle = LocalLifecycleOwner.current.lifecycle,
    navController: NavHostController = rememberNavController(),
    mainViewModel: MainViewModel = viewModel(LocalContext.current as ComponentActivity),
    navTabHandler: NavTabHandler = viewModel(LocalContext.current as ComponentActivity),
    viewModel: ComicViewerAppViewModel = hiltViewModel(),
): ComicViewerAppState = rememberSaveableScreenState {
    ComicViewerAppStateImpl(
        lifecycle = lifecycle,
        savedStateHandle = it,
        navController = navController,
        scope = scope,
        mainViewModel = mainViewModel,
        navTabHandler = navTabHandler,
        getNavigationHistoryUseCase = viewModel.getNavigationHistoryUseCase,
        manageDisplaySettingsUseCase = viewModel.manageDisplaySettingsUseCase,
        getInstalledModulesUseCase = viewModel.getInstalledModulesUseCase,
    )
}

@SuppressLint("RestrictedApi")
@OptIn(SavedStateHandleSaveableApi::class)
private class ComicViewerAppStateImpl(
    lifecycle: Lifecycle,
    override val savedStateHandle: SavedStateHandle,
    override val navController: NavHostController,
    private val scope: CoroutineScope,
    private val mainViewModel: MainViewModel,
    private val navTabHandler: NavTabHandler,
    private val getNavigationHistoryUseCase: GetNavigationHistoryUseCase,
    private val manageDisplaySettingsUseCase: ManageDisplaySettingsUseCase,
    private val getInstalledModulesUseCase: GetInstalledModulesUseCase,
) : ComicViewerAppState {

    override var uiState by savedStateHandle.saveable { mutableStateOf(ComicViewerScaffoldUiState()) }
        private set
    override val addOnList = mutableStateListOf<AddOn>()
    private var isInitialized by savedStateHandle.saveable { mutableStateOf(false) }

    init {
        val backStackEntryFlow = navController.currentBackStackEntryFlow
        backStackEntryFlow
            .filter { it.destination is ComposeNavigator.Destination }
            .onEach { backStackEntry ->
                val currentTab = MainScreenTab.entries.find { tab ->
                    backStackEntry.destination.hierarchy.any { destination ->
                        val findDestination =
                            tab.navGraph.findDestination(destination.route.orEmpty())
                        (findDestination?.style as? DestinationTransitions)?.directionToDisplayNavigation?.any { destination.route == it.route }
                            ?: false
                    }
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
        logcat { "init. isInitialized=$isInitialized" }
        if (!isInitialized) {
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
        if (navController.currentBackStackEntry?.destination?.hierarchy?.any { it.route == navGraph.route } == true) {
            navTabHandler.click.tryEmit(Unit)
        } else if (navGraph is Direction) {
            navController.toDestinationsNavigator().navigate(
                navGraph,
                navOptions {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            )
        }
    }

    override fun onNavigationHistoryRestore() {
        logcat { "onNavigationHistoryRestore" }
        completeRestoreHistory()
    }

    private fun completeRestoreHistory() {
        mainViewModel.shouldKeepSplash.value = false
        mainViewModel.isInitialized.value = true
        isInitialized = true
    }

    private fun restoreNavigation(): Job {
        val destinationTransitions = navController.toDestinationsNavigator()
        return scope.launch {
            val history =
                getNavigationHistoryUseCase.execute(EmptyRequest).first().fold({ it }, { null })
            destinationTransitions.navigate(BookshelfNavGraph) {
                popUpTo(MainNavGraph) {
                    inclusive = true
                }
            }
            if (history?.folderList.isNullOrEmpty()) {
                completeRestoreHistory()
            } else {
                val (folderList, book) = history!!.value
                val bookshelfId = folderList.first().bookshelfId
                if (folderList.size == 1) {
                    destinationTransitions.navigate(
                        BookshelfFolderScreenDestination(
                            bookshelfId,
                            folderList.first().path,
                            book.path
                        )
                    )
                    logcat("RESTORE_NAVIGATION", LogPriority.INFO) {
                        "bookshelf(${bookshelfId.value}) -> folder(${folderList.first().path})"
                    }
                } else {
                    destinationTransitions.navigate(
                        BookshelfFolderScreenDestination(bookshelfId, folderList.first().path, null)
                    )
                    logcat("RESTORE_NAVIGATION", LogPriority.INFO) {
                        "bookshelf(${bookshelfId.value}) -> folder(${folderList.first().path})"
                    }
                    folderList.drop(1).dropLast(1).forEach { folder ->
                        destinationTransitions.navigate(
                            BookshelfFolderScreenDestination(
                                bookshelfId,
                                folder.path,
                                null
                            )
                        )
                        logcat("RESTORE_NAVIGATION", LogPriority.INFO) {
                            "-> folder(${folder.path})"
                        }
                    }
                    destinationTransitions.navigate(
                        BookshelfFolderScreenDestination(
                            bookshelfId,
                            folderList.last().path,
                            book.path
                        )
                    )
                    logcat("RESTORE_NAVIGATION", LogPriority.INFO) {
                        "-> folder${folderList.last().path}, ${book.path}"
                    }
                }
            }
        }
    }

    override fun refreshAddOnList() {
        runBlocking {
            val installedModules =
                getInstalledModulesUseCase.execute(GetInstalledModulesUseCase.Request)
                    .first().dataOrNull!!
            addOnList.removeAll { !installedModules.contains(it.moduleName) }
            addOnList.addAll(
                installedModules
                    .mapNotNull { module -> AddOn.entries.find { it.moduleName == module } }
                    .filter { module -> !addOnList.any { it == module } }
            )
        }
        logcat { "addOnList=${addOnList.joinToString(",") { it.moduleName }}" }
    }
}

fun cancelJob(
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
