package com.sorrowblue.comicviewer.app

import androidx.activity.ComponentActivity
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.navigation.material.BottomSheetNavigator
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.sorrowblue.comicviewer.app.component.ComicViewerFab
import com.sorrowblue.comicviewer.app.component.ComicViewerNavigationBar
import com.sorrowblue.comicviewer.app.component.ComicViewerNavigationRail
import com.sorrowblue.comicviewer.app.component.NavHostWithSharedAxisX
import com.sorrowblue.comicviewer.framework.designsystem.animation.navigationRailAnimation
import com.sorrowblue.comicviewer.framework.designsystem.theme.LocalWindowSize
import com.sorrowblue.comicviewer.framework.ui.CommonViewModel
import kotlinx.collections.immutable.toPersistentList

private enum class NavigationType {
    BottomNavigation,
    NavigationRail
}

internal const val mainGraphRoute = "main"

@OptIn(ExperimentalMaterialNavigationApi::class)
@Composable
internal fun MainScreen(
    bottomSheetNavigator: BottomSheetNavigator,
    navController: NavHostController,
    startDestination: String,
    routeToTab: String.() -> MainScreenTab?,
    routeToFab: String.() -> MainScreenFab?,
    onTabSelected: (NavController, MainScreenTab) -> Unit,
    onFabClick: (NavController, MainScreenFab) -> Unit,
    viewModel: CommonViewModel = hiltViewModel(LocalContext.current as ComponentActivity),
    navGraph: NavGraphBuilder.(NavHostController, PaddingValues) -> Unit,
) {
    Row(modifier = Modifier.fillMaxSize()) {
        val backStackEntry by navController.currentBackStackEntryAsState()
        val currentTab by remember(backStackEntry) {
            mutableStateOf(backStackEntry?.destination?.hierarchy?.firstOrNull()?.route?.routeToTab())
        }
        val currentFab by remember(backStackEntry) {
            mutableStateOf(backStackEntry?.destination?.hierarchy?.firstOrNull()?.route?.routeToFab())
        }
        val navigationType: NavigationType = when (LocalWindowSize.current.widthSizeClass) {
            WindowWidthSizeClass.Compact -> NavigationType.BottomNavigation
            WindowWidthSizeClass.Medium -> NavigationType.NavigationRail
            WindowWidthSizeClass.Expanded -> NavigationType.NavigationRail
            else -> NavigationType.BottomNavigation
        }
        AnimatedContent(
            targetState = navigationType == NavigationType.NavigationRail && currentTab != null,
            contentAlignment = Alignment.CenterStart,
            transitionSpec = { navigationRailAnimation() },
            label = "ComicViewerNavigationRail"
        ) {
            if (it) {
                ComicViewerNavigationRail(
                    mainScreenTabs = MainScreenTab.entries.toPersistentList(),
                    onTabSelected = { tab ->
                        onTabSelected(navController, tab)
                    },
                    currentTab = currentTab,
                    currentFab = currentFab,
                    onFabClick = { fab ->
                        onFabClick(navController, fab)
                    }
                )
            } else {
                Spacer(modifier = Modifier.fillMaxHeight())
            }
        }

        Scaffold(
            bottomBar = {
                if (navigationType == NavigationType.BottomNavigation) {
                    ComicViewerNavigationBar(
                        mainScreenTabs = remember { MainScreenTab.entries.toPersistentList() },
                        onTabSelected = { tab -> onTabSelected(navController, tab) },
                        currentTab = currentTab,
                    )
                }
            },
            floatingActionButton = {
                if (navigationType == NavigationType.BottomNavigation) {
                    ComicViewerFab(currentFab, viewModel.canScroll) {
                        onFabClick(
                            navController,
                            currentFab!!
                        )
                    }
                }
            },
        ) { contentPadding ->
            ModalBottomSheetLayout(bottomSheetNavigator) {
                NavHostWithSharedAxisX(
                    navController = navController,
                    route = mainGraphRoute,
                    startDestination = startDestination,
                    modifier = Modifier
                ) {
                    navGraph(navController, contentPadding)
                }
            }
        }
    }
}
