package com.sorrowblue.comicviewer.app

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
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import com.sorrowblue.comicviewer.bookshelf.navigation.bookshelfGraphRoute
import com.sorrowblue.comicviewer.feature.tutorial.navigation.TutorialRoute
import com.sorrowblue.comicviewer.feature.tutorial.navigation.navigateToTutorial
import com.sorrowblue.comicviewer.framework.compose.AppMaterialTheme
import com.sorrowblue.comicviewer.framework.compose.CollectAsEffect
import com.sorrowblue.comicviewer.framework.compose.LifecycleEffect
import com.sorrowblue.comicviewer.framework.compose.LocalWindowSize

internal sealed interface ComicViewerAppUiEvent {
    data object StartTutorial : ComicViewerAppUiEvent
    data class CompleteTutorial(val isInitial: Boolean) : ComicViewerAppUiEvent
}

@OptIn(ExperimentalMaterialNavigationApi::class)
@Composable
internal fun ComicViewerApp(
    windowsSize: WindowSizeClass,
    viewModel: ComicViewerAppViewModel,
    modifier: Modifier = Modifier
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
                MainScreen(
                    bottomSheetNavigator = bottomSheetNavigator,
                    navController = navController,
                    startDestination = bookshelfGraphRoute,
                    routeToTab = graphStateHolder::routeToTab,
                    onTabSelected = graphStateHolder::onTabSelected
                ) { navHostController, contentPadding ->
                    mainGraph(
                        context = context,
                        navController = navHostController,
                        extraNavController = extraNavController,
                        contentPadding = contentPadding,
                        restoreComplete = {},
                        onTutorialExit = viewModel::completeTutorial,
                        addOnList = addOnList
                    )
                }
            }
        }
    }
    viewModel.uiEvents.CollectAsEffect {
        when (it) {
            ComicViewerAppUiEvent.StartTutorial -> navController.navigateToTutorial(
                navOptions {
                    popUpTo(graphStateHolder.startDestination) {
                        inclusive = true
                    }
                }
            )

            is ComicViewerAppUiEvent.CompleteTutorial ->
                if (it.isInitial) {
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
    LifecycleEffect(lifecycleObserver = viewModel)
}
