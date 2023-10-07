package com.sorrowblue.comicviewer.app

import android.app.Activity
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import com.sorrowblue.comicviewer.bookshelf.navigation.navigateToBookshelfFolder
import com.sorrowblue.comicviewer.domain.usecase.NavigationHistory
import com.sorrowblue.comicviewer.feature.authentication.navigation.Mode
import com.sorrowblue.comicviewer.feature.authentication.navigation.navigateToAuthentication
import com.sorrowblue.comicviewer.feature.tutorial.navigation.TutorialRoute
import com.sorrowblue.comicviewer.feature.tutorial.navigation.navigateToTutorial
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.designsystem.theme.LocalDimension
import com.sorrowblue.comicviewer.framework.designsystem.theme.LocalWindowSize
import com.sorrowblue.comicviewer.framework.designsystem.theme.compactDimension
import com.sorrowblue.comicviewer.framework.designsystem.theme.expandedDimension
import com.sorrowblue.comicviewer.framework.designsystem.theme.mediumDimension
import com.sorrowblue.comicviewer.framework.ui.LifecycleEffect
import com.sorrowblue.comicviewer.framework.ui.LocalNavController
import com.sorrowblue.comicviewer.framework.ui.lifecycle.LaunchedEffectUiEvent
import logcat.LogPriority
import logcat.logcat

internal sealed interface ComicViewerAppUiEvent {

    data class StartTutorial(val done: () -> Unit) : ComicViewerAppUiEvent

    data class CompleteTutorial(val isFirstTime: Boolean) : ComicViewerAppUiEvent

    class RestoreHistory(val history: NavigationHistory) : ComicViewerAppUiEvent

    data class RequireAuthentication(val isRestoredNavHistory: Boolean, val done: () -> Unit) :
        ComicViewerAppUiEvent
}

@OptIn(ExperimentalMaterialNavigationApi::class)
@Composable
internal fun ComicViewerApp(
    windowsSize: WindowSizeClass,
    viewModel: ComicViewerAppViewModel,
) {
    val context = LocalContext.current
    val bottomSheetNavigator = rememberBottomSheetNavigator()
    val navController = rememberNavController(bottomSheetNavigator)
    val extraNavController = rememberExtraNavController()
    val graphStateHolder = rememberGraphStateHolder()

    val dimension = when (windowsSize.widthSizeClass) {
        WindowWidthSizeClass.Compact -> compactDimension
        WindowWidthSizeClass.Medium -> mediumDimension
        WindowWidthSizeClass.Expanded -> expandedDimension
        else -> compactDimension
    }
    CompositionLocalProvider(
        LocalWindowSize provides windowsSize,
        LocalDimension provides dimension,
        LocalNavController provides navController
    ) {
        ComicTheme {
            val addOnList by viewModel.addOnList.collectAsState()
            val activity = LocalContext.current as Activity
            MainScreen(
                bottomSheetNavigator = bottomSheetNavigator,
                navController = navController,
                startDestination = graphStateHolder.startDestination,
                routeToTab = graphStateHolder::routeToTab,
                routeToFab = graphStateHolder::routeToFab,
                onTabSelected = graphStateHolder::onTabSelected,
                onFabClick = graphStateHolder::onTabClick,
            ) { navHostController, contentPadding ->
                mainGraph(
                    context = context,
                    navController = navHostController,
                    extraNavController = extraNavController,
                    contentPadding = contentPadding,
                    restoreComplete = viewModel::completeRestoreHistory,
                    onTutorialExit = viewModel::onCompleteTutorial,
                    onBackClick = {
                        ActivityCompat.finishAffinity(activity)
                    },
                    onAuthCompleted = { handleBack ->
                        if (handleBack) {
                            navController.popBackStack()
                        } else {
                            navController.navigate(
                                graphStateHolder.startDestination,
                                navOptions {
                                    popUpTo(com.sorrowblue.comicviewer.feature.authentication.navigation.authenticationRoute) {
                                        inclusive = true
                                    }
                                }
                            )
                        }
                    },
                    addOnList = addOnList,
                )
            }
        }
    }
    LaunchedEffectUiEvent(viewModel) { uiEvent ->
        when (uiEvent) {
            is ComicViewerAppUiEvent.StartTutorial -> {
                navController.navigateToTutorial(navOptions {
                    popUpTo(mainGraphRoute) {
                        inclusive = true
                    }
                })
                uiEvent.done()
            }

            is ComicViewerAppUiEvent.CompleteTutorial ->
                if (uiEvent.isFirstTime) {
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

            is ComicViewerAppUiEvent.RestoreHistory -> {
                val (bookshelf, folderList, position) = uiEvent.history.triple
                if (folderList.isEmpty()) {
                    viewModel.completeRestoreHistory()
                } else if (folderList.size == 1) {
                    navController.navigateToBookshelfFolder(
                        bookshelf.id,
                        folderList.first().path,
                        position
                    )
                    logcat("RESTORE_NAVIGATION", LogPriority.INFO) {
                        "bookshelf(${bookshelf.id}) -> folder(${folderList.first().path})"
                    }
                } else {
                    navController.navigateToBookshelfFolder(
                        bookshelf.id,
                        folderList.first().path
                    )
                    logcat("RESTORE_NAVIGATION", LogPriority.INFO) {
                        "bookshelf(${bookshelf.id}) -> folder(${folderList.first().path})"
                    }
                    folderList.drop(1).dropLast(1).forEach { folder ->
                        navController.navigateToBookshelfFolder(bookshelf.id, folder.path)
                        logcat("RESTORE_NAVIGATION", LogPriority.INFO) {
                            "-> folder(${folder.path})"
                        }
                    }
                    navController.navigateToBookshelfFolder(
                        bookshelf.id,
                        folderList.last().path,
                        position
                    )
                    logcat("RESTORE_NAVIGATION", LogPriority.INFO) {
                        "-> folder${folderList.last().path}, $position"
                    }
                }
            }

            is ComicViewerAppUiEvent.RequireAuthentication -> {
                if (uiEvent.isRestoredNavHistory) {
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
                            popUpTo(mainGraphRoute) {
                                inclusive = true
                            }
                        }
                    )
                }
                uiEvent.done()
            }

        }
    }
    LifecycleEffect(lifecycleObserver = viewModel)
}
