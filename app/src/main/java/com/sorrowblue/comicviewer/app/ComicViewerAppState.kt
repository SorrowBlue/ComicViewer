package com.sorrowblue.comicviewer.app

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.navigation.popUpTo
import com.sorrowblue.comicviewer.bookshelf.destinations.BookshelfFolderScreenDestination
import com.sorrowblue.comicviewer.bookshelf.navigation.BookshelfNavGraph
import com.sorrowblue.comicviewer.domain.model.AddOn
import com.sorrowblue.comicviewer.favorite.navigation.FavoriteNavGraph
import com.sorrowblue.comicviewer.feature.authentication.destinations.AuthenticationScreenDestination
import com.sorrowblue.comicviewer.feature.authentication.navigation.Mode
import com.sorrowblue.comicviewer.feature.library.navigation.LibraryNavGraph
import com.sorrowblue.comicviewer.feature.readlater.navigation.ReadLaterNavGraph
import com.sorrowblue.comicviewer.feature.tutorial.destinations.TutorialScreenDestination
import com.sorrowblue.comicviewer.feature.tutorial.navigation.TutorialNavGraph
import com.sorrowblue.comicviewer.framework.ui.NavTabHandler
import com.sorrowblue.comicviewer.framework.ui.SaveableScreenState
import com.sorrowblue.comicviewer.framework.ui.rememberSaveableScreenState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import logcat.LogPriority
import logcat.logcat

internal interface ComicViewerAppState : SaveableScreenState {

    val uiState: MainScreenUiState
    val navController: NavHostController
    val addOnList: SnapshotStateList<AddOn>

    fun onCreate()
    fun onStart()
    fun onCompleteTutorial()
    fun completeRestoreHistory()
    fun onTabSelected(
        navController: NavController,
        tab: MainScreenTab,
    )
}

@Composable
internal fun rememberComicViewerAppState(
    viewModel: ComicViewerAppViewModel = viewModel(LocalContext.current as ComponentActivity),
    navTabHandler: NavTabHandler = viewModel(LocalContext.current as ComponentActivity),
    navController: NavHostController = rememberNavController(),
    scope: CoroutineScope = rememberCoroutineScope(),
    lifecycle: LifecycleOwner = LocalLifecycleOwner.current,
): ComicViewerAppState = rememberSaveableScreenState {
    ComicViewerAppStateImpl(
        savedStateHandle = it,
        lifecycle = lifecycle.lifecycle,
        navController = navController,
        viewModel = viewModel,
        navTabHandler = navTabHandler,
        scope = scope
    )
}

@OptIn(SavedStateHandleSaveableApi::class)
@Stable
private class ComicViewerAppStateImpl(
    lifecycle: Lifecycle,
    override val savedStateHandle: SavedStateHandle,
    override val navController: NavHostController,
    private val viewModel: ComicViewerAppViewModel,
    private val navTabHandler: NavTabHandler,
    private val scope: CoroutineScope,
) : ComicViewerAppState {

    private var isInitialized by savedStateHandle.saveable { mutableStateOf(false) }

    private var isRestoredNavHistory = false

    override var uiState by savedStateHandle.saveable { mutableStateOf(MainScreenUiState()) }
        private set

    override val addOnList = mutableStateListOf<AddOn>().apply {
        addAll(viewModel.installedModules.mapNotNull { module -> AddOn.entries.find { it.moduleName == module } })
    }

    init {
        val backStackEntryFlow = navController.currentBackStackEntryFlow
        backStackEntryFlow
            .filter { it.destination is ComposeNavigator.Destination }
            .onEach {
                logcat { "destination.hierarchy=${it.destination.hierarchy.joinToString(",") { it.route.orEmpty() }}" }
                val currentTab = MainScreenTab.entries.find { tab ->
                    it.destination.hierarchy.any { it.route == tab.navGraph.route }
                }
                uiState = uiState.copy(
                    currentTab = currentTab,
                    showNavigation = currentTab != null
                )
            }.flowWithLifecycle(lifecycle)
            .launchIn(scope)
    }

    override fun onCreate() {
        if (this.isInitialized) return
        scope.launch {
            if (viewModel.isTutorial()) {
                navController.navigate(TutorialNavGraph) {
                    popUpTo(RootNavGraph) {
                        inclusive = true
                    }
                }
                viewModel.shouldKeepSplash = false
                this@ComicViewerAppStateImpl.isInitialized = true
            } else if (viewModel.isRestore()) {
                val job = restoreNavigation()
                scope.launch {
                    delay(3000)
                    completeRestoreHistory()
                    job.cancel()
                }
            } else {
                completeRestoreHistory()
            }
        }
    }

    override fun onStart() {
        val installedModules = viewModel.installedModules

        addOnList.removeAll { !installedModules.contains(it.moduleName) }

        addOnList.addAll(
            installedModules
                .mapNotNull { module -> AddOn.entries.find { it.moduleName == module } }
                .filter { module -> !addOnList.any { it == module } }
        )

        logcat { "addOnList=${addOnList.joinToString(",") { it.moduleName }}" }

        if (this.isInitialized) {
            scope.launch {
                if (viewModel.lockOnBackground()) {
                    navController.navigate(
                        AuthenticationScreenDestination(Mode.Authentication, true)
                    ) {
                        launchSingleTop = true
                    }
                    viewModel.shouldKeepSplash = false
                } else {
                    viewModel.shouldKeepSplash = false
                }
            }
        }
    }

    override fun onCompleteTutorial() {
        scope.launch {
            val isTutorial = viewModel.isTutorial()
            if (isTutorial) {
                viewModel.onTutorialComplete()
            }
            if (isTutorial) {
                navController.navigate(
                    BookshelfNavGraph.route,
                    navOptions {
                        popUpTo(TutorialScreenDestination.route) {
                            inclusive = true
                        }
                    }
                )
            } else {
                navController.popBackStack()
            }
        }
    }

    override fun completeRestoreHistory() {
        scope.launch {
            if (viewModel.isAuth()) {
                logcat { "認証 復元完了後" }
                if (isRestoredNavHistory) {
                    navController.navigate(
                        AuthenticationScreenDestination(Mode.Authentication, true)
                    ) {
                        launchSingleTop = true
                    }
                } else {
                    navController.navigate(
                        AuthenticationScreenDestination(Mode.Authentication, false)
                    ) {
                        launchSingleTop = true
                        popUpTo(RootNavGraph) {
                            inclusive = true
                        }
                    }
                }
                viewModel.shouldKeepSplash = false
                isInitialized = true
            } else {
                viewModel.shouldKeepSplash = false
                isInitialized = true
            }
        }
    }

    override fun onTabSelected(
        navController: NavController,
        tab: MainScreenTab,
    ) {
        when (tab) {
            MainScreenTab.Bookshelf -> BookshelfNavGraph.route
            MainScreenTab.Favorite -> FavoriteNavGraph.route
            MainScreenTab.Readlater -> ReadLaterNavGraph.route
            MainScreenTab.Library -> LibraryNavGraph.route
        }.let { route ->
            if (navController.currentBackStackEntry?.destination?.hierarchy?.any { it.route == route } == true) {
                navTabHandler.click.tryEmit(Unit)
            } else {
                navController.navigate(
                    route,
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
    }

    private fun restoreNavigation(): Job {
        return scope.launch {
            val history = viewModel.history()
            if (history?.folderList.isNullOrEmpty()) {
                completeRestoreHistory()
            } else {
                isRestoredNavHistory = true
                val (folderList, book) = history!!.value
                val bookshelfId = folderList.first().bookshelfId
                if (folderList.size == 1) {
                    navController.navigate(
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
                    navController.navigate(
                        BookshelfFolderScreenDestination(bookshelfId, folderList.first().path, null)
                    )
                    logcat("RESTORE_NAVIGATION", LogPriority.INFO) {
                        "bookshelf(${bookshelfId.value}) -> folder(${folderList.first().path})"
                    }
                    folderList.drop(1).dropLast(1).forEach { folder ->
                        navController.navigate(
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
                    navController.navigate(
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
}
