package com.sorrowblue.comicviewer.app

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Scaffold
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.navigation.material.BottomSheetNavigator
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.sorrowblue.comicviewer.app.component.ComicViewerNavigationBar
import com.sorrowblue.comicviewer.app.component.ComicViewerNavigationRail
import com.sorrowblue.comicviewer.app.component.NavHostWithSharedAxisX
import com.sorrowblue.comicviewer.bookshelf.navigation.routeInBookshelfGraph
import com.sorrowblue.comicviewer.favorite.navigation.routeInFavoriteGraph
import com.sorrowblue.comicviewer.feature.library.navigation.routeInLibraryGraph
import com.sorrowblue.comicviewer.feature.readlater.navigation.routeInReadlaterGraph
import com.sorrowblue.comicviewer.framework.compose.LocalWindowSize
import kotlinx.collections.immutable.toPersistentList

private enum class NavigationType {
    BottomNavigation,
    NavigationRail
}

private val routeInNavigationBar =
    routeInBookshelfGraph + routeInFavoriteGraph + routeInReadlaterGraph + routeInLibraryGraph

@OptIn(ExperimentalMaterialNavigationApi::class)
@Composable
internal fun MainScreen(
    bottomSheetNavigator: BottomSheetNavigator,
    navController: NavHostController,
    startDestination: String,
    routeToTab: String.() -> MainScreenTab?,
    onTabSelected: (NavController, MainScreenTab) -> Unit,
    navGraph: NavGraphBuilder.(NavHostController, PaddingValues) -> Unit,
) {
    Row(modifier = Modifier.fillMaxSize()) {
        val backStackEntry by navController.currentBackStackEntryAsState()
        var currentTab by remember {
            mutableStateOf(backStackEntry?.destination?.hierarchy?.firstOrNull()?.route?.routeToTab())
        }
        val navigationType: NavigationType = when (LocalWindowSize.current.widthSizeClass) {
            WindowWidthSizeClass.Compact -> NavigationType.BottomNavigation
            WindowWidthSizeClass.Medium -> NavigationType.NavigationRail
            WindowWidthSizeClass.Expanded -> NavigationType.NavigationRail
            else -> NavigationType.BottomNavigation
        }

        var visible by remember { mutableStateOf(false) }
        LaunchedEffect(backStackEntry) {
            if (backStackEntry?.destination?.navigatorName == "dialog") {
                return@LaunchedEffect
            }
            visible = backStackEntry?.destination?.route in routeInNavigationBar
            currentTab = backStackEntry?.destination?.hierarchy?.firstOrNull()?.route?.routeToTab()
                ?: return@LaunchedEffect
        }
        AnimatedVisibility(visible = navigationType == NavigationType.NavigationRail) {
            ComicViewerNavigationRail(
                mainScreenTabs = MainScreenTab.entries.toPersistentList(),
                onTabSelected = { tab ->
                    onTabSelected(navController, tab)
                },
                currentTab = currentTab,
            )
        }
        Scaffold(
            bottomBar = {
                AnimatedContent(
                    targetState = navigationType == NavigationType.BottomNavigation && visible,
                    transitionSpec = { slideInVertically { height -> height } togetherWith slideOutVertically { height -> height } },
                    label = "test"
                ) { isVisible ->
                    if (isVisible) {
                        ComicViewerNavigationBar(
                            mainScreenTabs = MainScreenTab.entries.toPersistentList(),
                            onTabSelected = { tab ->
                                onTabSelected(navController, tab)
                            },
                            currentTab = currentTab ?: MainScreenTab.Bookshelf,
                        )
                    } else {
                        Box(modifier = Modifier.fillMaxWidth())
                    }
                }
            }
        ) { contentPadding ->
            ModalBottomSheetLayout(bottomSheetNavigator) {
                NavHostWithSharedAxisX(
                    navController = navController,
                    startDestination = startDestination,
                    modifier = Modifier
                ) {
                    navGraph(navController, contentPadding)
                }
            }
        }
    }
}
