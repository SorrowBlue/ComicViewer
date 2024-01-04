package com.sorrowblue.comicviewer.app

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.google.accompanist.navigation.material.BottomSheetNavigator
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import com.ramcosta.composedestinations.navigation.navigate
import com.sorrowblue.comicviewer.bookshelf.destinations.BookshelfFolderScreenDestination
import com.sorrowblue.comicviewer.domain.model.AddOn
import com.sorrowblue.comicviewer.feature.authentication.destinations.AuthenticationScreenDestination
import com.sorrowblue.comicviewer.feature.authentication.navigation.Mode
import com.sorrowblue.comicviewer.feature.tutorial.destinations.TutorialScreenDestination
import com.sorrowblue.comicviewer.feature.tutorial.navigation.TutorialNavGraph
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import logcat.LogPriority
import logcat.logcat

@OptIn(ExperimentalMaterialNavigationApi::class)
internal interface ComicViewerAppState {
    var uiState: MainScreenUiState

    val bottomSheetNavigator: BottomSheetNavigator
    val navController: NavHostController
    val graphStateHolder: GraphStateHolder
    val addOnList: SnapshotStateList<AddOn>
    fun onCreate()
    fun onStart()
    fun onCompleteTutorial()
    fun completeRestoreHistory()
}

@OptIn(ExperimentalMaterialNavigationApi::class)
@Composable
internal fun rememberComicViewerAppState(
    savedStateHandle: SavedStateHandle,
    viewModel: ComicViewerAppViewModel = viewModel(LocalContext.current as ComponentActivity),
    bottomSheetNavigator: BottomSheetNavigator = rememberBottomSheetNavigator(),
    navController: NavHostController = rememberNavController(bottomSheetNavigator),
    graphStateHolder: GraphStateHolder = rememberGraphStateHolder(),
    scope: CoroutineScope = rememberCoroutineScope(),
): ComicViewerAppState = remember {
    ComicViewerAppStateImpl(
        savedStateHandle,
        bottomSheetNavigator,
        navController,
        graphStateHolder,
        viewModel,
        scope
    )
}

@OptIn(ExperimentalMaterialNavigationApi::class, SavedStateHandleSaveableApi::class)
@Stable
private class ComicViewerAppStateImpl(
    savedStateHandle: SavedStateHandle,
    override val bottomSheetNavigator: BottomSheetNavigator,
    override val navController: NavHostController,
    override val graphStateHolder: GraphStateHolder,
    private val viewModel: ComicViewerAppViewModel,
    private val scope: CoroutineScope,
) : ComicViewerAppState {

    var isInitialized: Boolean by savedStateHandle.saveable { mutableStateOf(false) }
    var isRestoredNavHistory = false

    override var uiState by savedStateHandle.saveable(
        stateSaver = mapSaver(
            save = {
                mapOf("currentTab" to it.currentTab?.name, "showNavigation" to it.showNavigation)
            },
            restore = {
                MainScreenUiState(
                    currentTab = (it["currentTab"] as? String)?.let(MainScreenTab::valueOf),
                    showNavigation = it["showNavigation"] as Boolean
                )
            }
        )
    ) {
        mutableStateOf(MainScreenUiState())
    }

    override val addOnList = mutableStateListOf<AddOn>().apply {
        addAll(viewModel.installedModules.mapNotNull { module -> AddOn.entries.find { it.moduleName == module } })
    }

    init {
        scope.launch {
            navController.currentBackStackEntryFlow.collectLatest {
                if (it.destination is ComposeNavigator.Destination) {
                    logcat { "destination.hierarchy=${it.destination.hierarchy.joinToString(",") { it.route.orEmpty() }}" }
                    val currentTab =
                        it.destination.hierarchy.firstOrNull()?.route?.let(graphStateHolder::routeToTab)
                    uiState = uiState.copy(
                        currentTab = currentTab,
                        showNavigation = currentTab != null
                    )
                }
            }
        }
    }

    override fun onCreate() {
        if (this.isInitialized) return
        scope.launch {
            if (viewModel.isTutorial()) {
                navController.navigate(TutorialNavGraph) {
                    popUpTo(MainGraphRoute) {
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
                    graphStateHolder.startDestination,
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
                        popUpTo(MainGraphRoute) {
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
}
