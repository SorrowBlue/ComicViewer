package com.sorrowblue.comicviewer.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.google.accompanist.navigation.material.BottomSheetNavigator
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import com.sorrowblue.comicviewer.bookshelf.navigation.navigateToBookshelfFolder
import com.sorrowblue.comicviewer.domain.model.AddOn
import com.sorrowblue.comicviewer.feature.authentication.navigation.Mode
import com.sorrowblue.comicviewer.feature.authentication.navigation.navigateToAuthentication
import com.sorrowblue.comicviewer.feature.tutorial.navigation.TutorialRoute
import com.sorrowblue.comicviewer.feature.tutorial.navigation.navigateToTutorial
import com.sorrowblue.comicviewer.framework.ui.SavableState
import com.sorrowblue.comicviewer.framework.ui.rememberSavableState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import logcat.LogPriority
import logcat.logcat

@OptIn(ExperimentalMaterialNavigationApi::class)
@Composable
internal fun rememberComicViewerAppState(
    bottomSheetNavigator: BottomSheetNavigator = rememberBottomSheetNavigator(),
    navController: NavHostController = rememberNavController(bottomSheetNavigator),
    graphStateHolder: GraphStateHolder = rememberGraphStateHolder(),
    viewModel: ComicViewerAppViewModel = hiltViewModel(),
    scope: CoroutineScope = rememberCoroutineScope(),
): ComicViewerAppState = rememberSavableState(
    restore = {
        ComicViewerAppStateImpl(
            bottomSheetNavigator,
            navController,
            graphStateHolder,
            viewModel,
            scope,
            it
        )
    }
) {
    ComicViewerAppStateImpl(
        bottomSheetNavigator,
        navController,
        graphStateHolder,
        viewModel,
        scope,
        SavedStateHandle()
    )
}

internal interface ComicViewerAppState : SavableState {
    var uiState: MainScreenUiState

    @OptIn(ExperimentalMaterialNavigationApi::class)
    val bottomSheetNavigator: BottomSheetNavigator
    val navController: NavHostController
    val graphStateHolder: GraphStateHolder
    val addOnList: SnapshotStateList<AddOn>
    fun onCreate()
    fun onStart()
    fun onCompleteTutorial()
    fun completeRestoreHistory()
}

@OptIn(ExperimentalMaterialNavigationApi::class, SavedStateHandleSaveableApi::class)
@Stable
internal class ComicViewerAppStateImpl(
    override val bottomSheetNavigator: BottomSheetNavigator,
    override val navController: NavHostController,
    override val graphStateHolder: GraphStateHolder,
    private val viewModel: ComicViewerAppViewModel,
    private val scope: CoroutineScope,
    override val savedStateHandle: SavedStateHandle,
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
                    logcat { "it.destination=${it.destination}" }
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
                navController.navigateToTutorial(
                    navOptions {
                        popUpTo(MainGraphRoute) {
                            inclusive = true
                        }
                    }
                )
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
                    navController.navigateToBookshelfFolder(
                        bookshelfId,
                        folderList.first().path,
                        book.path
                    )
                    logcat("RESTORE_NAVIGATION", LogPriority.INFO) {
                        "bookshelf(${bookshelfId.value}) -> folder(${folderList.first().path})"
                    }
                } else {
                    navController.navigateToBookshelfFolder(
                        bookshelfId,
                        folderList.first().path
                    )
                    logcat("RESTORE_NAVIGATION", LogPriority.INFO) {
                        "bookshelf(${bookshelfId.value}) -> folder(${folderList.first().path})"
                    }
                    folderList.drop(1).dropLast(1).forEach { folder ->
                        navController.navigateToBookshelfFolder(bookshelfId, folder.path)
                        logcat("RESTORE_NAVIGATION", LogPriority.INFO) {
                            "-> folder(${folder.path})"
                        }
                    }
                    navController.navigateToBookshelfFolder(
                        bookshelfId,
                        folderList.last().path,
                        book.path
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

        if (this.isInitialized) {
            scope.launch {
                if (viewModel.lockOnBackground()) {
                    navController.navigateToAuthentication(
                        Mode.Authentication,
                        true,
                        navOptions { launchSingleTop = true }
                    )
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
                        popUpTo(TutorialRoute) {
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
                    navController.navigateToAuthentication(
                        Mode.Authentication,
                        true,
                        navOptions { launchSingleTop = true }
                    )
                } else {
                    navController.navigateToAuthentication(
                        Mode.Authentication,
                        false,
                        navOptions {
                            launchSingleTop = true
                            popUpTo(MainGraphRoute) {
                                inclusive = true
                            }
                        }
                    )
                }
                viewModel.shouldKeepSplash = false
                this@ComicViewerAppStateImpl.isInitialized = true
            } else {
                viewModel.shouldKeepSplash = false
                this@ComicViewerAppStateImpl.isInitialized = true
            }
        }
    }
}
