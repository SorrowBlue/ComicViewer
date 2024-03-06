package com.sorrowblue.comicviewer.app

import android.os.Parcelable
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.navigation.popUpTo
import com.sorrowblue.comicviewer.app.navigation.RootNavGraph
import com.sorrowblue.comicviewer.bookshelf.destinations.BookshelfFolderScreenDestination
import com.sorrowblue.comicviewer.bookshelf.navigation.BookshelfNavGraph
import com.sorrowblue.comicviewer.domain.model.AddOn
import com.sorrowblue.comicviewer.favorite.navigation.FavoriteNavGraph
import com.sorrowblue.comicviewer.feature.library.navigation.LibraryNavGraph
import com.sorrowblue.comicviewer.feature.readlater.navigation.ReadLaterNavGraph
import com.sorrowblue.comicviewer.feature.tutorial.destinations.TutorialScreenDestination
import com.sorrowblue.comicviewer.framework.ui.AnimatedNavGraphSpec
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
import kotlinx.parcelize.Parcelize
import logcat.LogPriority
import logcat.logcat

internal interface ComicViewerAppState : SaveableScreenState {

    val appEvent: ComicViewerAppEvent
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

    fun onAuthCompleted()
}

@Parcelize
internal data class ComicViewerAppEvent(
    val navigateToTutorial: Boolean = false,
    val navigateToAuth: Boolean? = null,
) : Parcelable

@Composable
internal fun rememberComicViewerAppState(
    viewModel: ComicViewerAppViewModel = hiltViewModel(LocalContext.current as ComponentActivity),
    navTabHandler: NavTabHandler = hiltViewModel(LocalContext.current as ComponentActivity),
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

    override var appEvent by savedStateHandle.saveable { mutableStateOf(ComicViewerAppEvent()) }
        private set

    override val addOnList = mutableStateListOf<AddOn>()

    init {
        val backStackEntryFlow = navController.currentBackStackEntryFlow
        backStackEntryFlow
            .filter { it.destination is ComposeNavigator.Destination }
            .onEach { backStackEntry ->
                val currentTab = MainScreenTab.entries.find { tab ->
                    tab.navGraph is AnimatedNavGraphSpec && backStackEntry.destination.hierarchy.any {
                        tab.navGraph.showNavigation.contains(it.route)
                    }
                }
                uiState = uiState.copy(currentTab = currentTab)
                logcat {
                    "destination.hierarchy=${
                        backStackEntry.destination.hierarchy.joinToString(",") {
                            it.route.orEmpty().ifEmpty { "null" }
                        }
                    }"
                }
            }.flowWithLifecycle(lifecycle)
            .launchIn(scope)
    }

    override fun onCreate() {
        if (isInitialized) return
        scope.launch {
            if (viewModel.isTutorial()) {
                appEvent = appEvent.copy(navigateToTutorial = true)
                closeSplashScreen()
                isInitialized = true
            } else if (viewModel.isAuth()) {
                uiState = uiState.copy(isAuthenticating = true)
                closeSplashScreen()
            } else if (viewModel.isNeedToRestore()) {
                cancelJob(
                    scope = scope,
                    waitTimeMillis = 3000,
                    onCancel = ::completeRestoreHistory,
                    action = ::restoreNavigation
                )
            } else {
                closeSplashScreen()
                isInitialized = true
            }
        }
    }

    override fun onStart() {
        scope.launch {
            val installedModules = viewModel.installedModules.first()
            addOnList.removeAll { !installedModules.contains(it.moduleName) }
            addOnList.addAll(
                installedModules
                    .mapNotNull { module -> AddOn.entries.find { it.moduleName == module } }
                    .filter { module -> !addOnList.any { it == module } }
            )
        }

        logcat { "addOnList=${addOnList.joinToString(",") { it.moduleName }}" }

        if (isInitialized) {
            scope.launch {
                if (viewModel.lockOnBackground()) {
                    appEvent = appEvent.copy(navigateToAuth = true)
                }
                closeSplashScreen()
            }
        }
    }

    override fun onAuthCompleted() {
        if (isInitialized) {
            // 初期化済み = 履歴復元する必要がない ので何もしない
        } else {
            scope.launch {
                if (viewModel.isNeedToRestore()) {
                    cancelJob(
                        scope = scope,
                        waitTimeMillis = 3000,
                        onCancel = ::completeRestoreHistory,
                        action = ::restoreNavigation
                    )
                } else {
                    uiState = uiState.copy(isAuthenticating = false)
                }
            }
        }
    }

    fun closeSplashScreen() {
        viewModel.shouldKeepSplash = false
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
        uiState = uiState.copy(isAuthenticating = false)
        closeSplashScreen()
        isInitialized = true
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
            navController.navigate(BookshelfNavGraph) {
                popUpTo(RootNavGraph) {
                    inclusive = true
                }
            }
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
