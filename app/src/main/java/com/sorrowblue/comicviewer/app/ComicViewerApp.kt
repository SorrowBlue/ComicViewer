package com.sorrowblue.comicviewer.app

import android.app.Activity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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
import com.sorrowblue.comicviewer.framework.compose.AppMaterialTheme
import com.sorrowblue.comicviewer.framework.compose.LaunchedEffectUiEvent
import com.sorrowblue.comicviewer.framework.compose.LifecycleEffect
import com.sorrowblue.comicviewer.framework.compose.LocalWindowSize
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
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val bottomSheetNavigator = rememberBottomSheetNavigator()
    val navController = rememberNavController(bottomSheetNavigator)
    val extraNavController = rememberExtraNavController()
    val graphStateHolder = rememberGraphStateHolder()
    AppMaterialTheme {
        CompositionLocalProvider(LocalWindowSize provides windowsSize) {
            Surface(
                modifier = modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background,
            ) {
                val addOnList by viewModel.addOnList.collectAsState()
                val activity = LocalContext.current as Activity
                MainScreen(
                    bottomSheetNavigator = bottomSheetNavigator,
                    navController = navController,
                    startDestination = graphStateHolder.startDestination,
                    routeToTab = graphStateHolder::routeToTab,
                    onTabSelected = graphStateHolder::onTabSelected
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
