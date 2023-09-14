package com.sorrowblue.comicviewer.feature.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sorrowblue.comicviewer.feature.main.component.ComicViewerNavigationBar
import com.sorrowblue.comicviewer.feature.main.component.ComicViewerNavigationRail
import kotlinx.collections.immutable.toPersistentList

const val mainRoute = "main"

fun NavGraphBuilder.mainScreen(
    windowSize: WindowSizeClass,
    mainNestedGraphStateHolder: MainNestedGraphStateHolder,
    mainNestedGraph: NavGraphBuilder.(mainNestedNavController: NavController, PaddingValues) -> Unit,
) {
    composable(mainRoute) {
        MainScreen(
            windowSize = windowSize,
            mainNestedGraphStateHolder = mainNestedGraphStateHolder,
            mainNestedNavGraph = mainNestedGraph,
        )
    }
}

private enum class NavigationType {
    BottomNavigation,
    NavigationRail
}

@Composable
private fun MainScreen(
    windowSize: WindowSizeClass,
    mainNestedGraphStateHolder: MainNestedGraphStateHolder,
    mainNestedNavGraph: NavGraphBuilder.(NavController, PaddingValues) -> Unit,
) {
    // 485
    val navigationType: NavigationType = when (windowSize.widthSizeClass) {
        WindowWidthSizeClass.Compact -> NavigationType.BottomNavigation
        WindowWidthSizeClass.Medium -> NavigationType.NavigationRail
        WindowWidthSizeClass.Expanded -> NavigationType.NavigationRail
        else -> NavigationType.BottomNavigation
    }

    MainScreen(
        navigationType = navigationType,
        startDestination = mainNestedGraphStateHolder.startDestination,
        routeToTab = mainNestedGraphStateHolder::routeToTab,
        onTabSelected = mainNestedGraphStateHolder::onTabSelected,
        mainNestedNavGraph = mainNestedNavGraph,
    )
}

@Composable
private fun MainScreen(
    navigationType: NavigationType,
    startDestination: String,
    routeToTab: String.() -> MainScreenTab?,
    onTabSelected: (NavController, MainScreenTab) -> Unit,
    mainNestedNavGraph: NavGraphBuilder.(NavController, PaddingValues) -> Unit,
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentTab = backStackEntry?.destination?.hierarchy?.firstOrNull()?.route?.routeToTab()
    Row(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(visible = navigationType == NavigationType.NavigationRail) {
            ComicViewerNavigationRail(
                mainScreenTabs = MainScreenTab.entries.toPersistentList(),
                onTabSelected = { tab ->
                    onTabSelected(navController, tab)
                },
                currentTab = currentTab ?: MainScreenTab.Bookshelf,
            )
        }
        Scaffold(
            bottomBar = {
                AnimatedVisibility(visible = navigationType == NavigationType.BottomNavigation) {
                    ComicViewerNavigationBar(
                        mainScreenTabs = MainScreenTab.entries.toPersistentList(),
                        onTabSelected = { tab ->
                            onTabSelected(navController, tab)
                        },
                        currentTab = currentTab ?: MainScreenTab.Bookshelf,
                    )
                }
            }
        ) {
            NavHost(
                navController = navController,
                startDestination = startDestination,
                modifier = Modifier,
                enterTransition = { materialFadeThroughIn() },
                exitTransition = { materialFadeThroughOut() },
            ) {
                mainNestedNavGraph(navController, it)
            }
        }
    }
}

private fun materialFadeThroughIn(): EnterTransition = fadeIn(
    animationSpec = tween(
        durationMillis = 195,
        delayMillis = 105,
        easing = LinearOutSlowInEasing,
    ),
) + scaleIn(
    animationSpec = tween(
        durationMillis = 195,
        delayMillis = 105,
        easing = LinearOutSlowInEasing,
    ),
    initialScale = 0.92f,
)

private fun materialFadeThroughOut(): ExitTransition = fadeOut(
    animationSpec = tween(
        durationMillis = 105,
        delayMillis = 0,
        easing = FastOutLinearInEasing,
    ),
)
