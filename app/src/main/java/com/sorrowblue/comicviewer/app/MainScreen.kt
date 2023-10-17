package com.sorrowblue.comicviewer.app

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.sorrowblue.comicviewer.app.component.AppFab
import com.sorrowblue.comicviewer.app.component.AppNavigationBar
import com.sorrowblue.comicviewer.app.component.AppNavigationRail
import com.sorrowblue.comicviewer.app.component.NavHostWithSharedAxisX
import com.sorrowblue.comicviewer.framework.ui.CommonViewModel
import com.sorrowblue.comicviewer.framework.ui.responsive.ResponsiveLayout
import com.sorrowblue.comicviewer.framework.ui.responsive.rememberResponsiveLayoutState
import kotlinx.collections.immutable.toPersistentList

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
    val mainScreenTabs = remember { MainScreenTab.entries.toPersistentList() }
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentTab by remember(backStackEntry) {
        mutableStateOf(backStackEntry?.destination?.hierarchy?.firstOrNull()?.route?.routeToTab())
    }
    val currentFab by remember(backStackEntry) {
        mutableStateOf(backStackEntry?.destination?.hierarchy?.firstOrNull()?.route?.routeToFab())
    }
    val responsiveLayoutState = rememberResponsiveLayoutState()
    ResponsiveLayout(
        state = responsiveLayoutState,
        navigationRail = {
            AppNavigationRail(
                mainScreenTabs = mainScreenTabs,
                currentTab = currentTab,
                onTabSelected = { tab -> onTabSelected(navController, tab) },
                currentFab = currentFab,
                onFabClick = { fab -> onFabClick(navController, fab) }
            )
        },
        navigationBar = {
            AppNavigationBar(
                mainScreenTabs = mainScreenTabs,
                currentTab = currentTab,
                onTabSelected = { tab -> onTabSelected(navController, tab) },
            )
        },
        floatingActionButton = {
            AppFab(currentFab, viewModel.canScroll) {
                onFabClick(navController, currentFab!!)
            }
        }
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
    LaunchedEffect(currentTab) {
        if (currentTab != null) {
            responsiveLayoutState.navigationState.show()
        } else {
            responsiveLayoutState.navigationState.hide()
        }
    }
    LaunchedEffect(currentFab) {
        if (currentFab != null) {
            responsiveLayoutState.fabState.show()
        } else {
            responsiveLayoutState.fabState.hide()
        }
    }
}
