package com.sorrowblue.comicviewer.app

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.adaptive.navigation.suite.ExperimentalMaterial3AdaptiveNavigationSuiteApi
import androidx.compose.material3.adaptive.navigation.suite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.navigation.material.BottomSheetNavigator
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.sorrowblue.comicviewer.app.component.AppFab
import com.sorrowblue.comicviewer.app.component.NavHostWithSharedAxisX
import com.sorrowblue.comicviewer.framework.ui.CommonViewModel
import com.sorrowblue.comicviewer.framework.ui.material3.Text
import kotlinx.collections.immutable.toPersistentList
import logcat.logcat

internal const val MainGraphRoute = "main"

@OptIn(
    ExperimentalMaterialNavigationApi::class,
    ExperimentalMaterial3AdaptiveNavigationSuiteApi::class
)
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
    var currentTab by remember { mutableStateOf<MainScreenTab?>(null) }
    var currentFab by remember { mutableStateOf<MainScreenFab?>(null) }
    LaunchedEffect(key1 = backStackEntry?.destination) {
        if (backStackEntry?.destination is ComposeNavigator.Destination) {
            currentTab = backStackEntry?.destination?.hierarchy?.firstOrNull()?.route?.routeToTab()
            currentFab = backStackEntry?.destination?.hierarchy?.firstOrNull()?.route?.routeToFab()
        }
    }
    if (currentTab != null) {
        NavigationSuiteScaffold(
            navigationSuiteItems = {
                mainScreenTabs.forEach {
                    item(
                        selected = it == currentTab,
                        onClick = {
                            onTabSelected(navController, it)
                        },
                        icon = {
                            Icon(
                                imageVector = it.icon,
                                contentDescription = stringResource(id = it.label)
                            )
                        },
                        label = {
                            Text(id = it.label)
                        }
                    )
                }
            },
            modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Start))
        ) {
            com.sorrowblue.comicviewer.framework.ui.material3.Scaffold(
                floatingActionButton = {
                    if (currentFab != null) {
                        AppFab(currentFab, viewModel.canScroll) {
                            onFabClick(navController, currentFab!!)
                        }
                    }
                }
            ) {
                LaunchedEffect(it) {
                    logcat { "padding = $it" }
                }
                ModalBottomSheetLayout(bottomSheetNavigator) {
                    NavHostWithSharedAxisX(
                        navController = navController,
                        route = MainGraphRoute,
                        startDestination = startDestination,
                        modifier = Modifier
                    ) {
                        navGraph(navController, it)
                    }
                }
            }
        }
    } else {
        Scaffold(
            floatingActionButton = {
                if (currentFab != null) {
                    AppFab(currentFab, viewModel.canScroll) {
                        onFabClick(navController, currentFab!!)
                    }
                }
            }
        ) {
            ModalBottomSheetLayout(bottomSheetNavigator) {
                NavHostWithSharedAxisX(
                    navController = navController,
                    route = MainGraphRoute,
                    startDestination = startDestination,
                    modifier = Modifier
                ) {
                    navGraph(navController, it)
                }
            }
        }
    }
}
