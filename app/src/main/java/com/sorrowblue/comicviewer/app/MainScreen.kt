package com.sorrowblue.comicviewer.app

import androidx.activity.ComponentActivity
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.adaptive.navigation.suite.ExperimentalMaterial3AdaptiveNavigationSuiteApi
import androidx.compose.material3.adaptive.navigation.suite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigation.suite.NavigationSuiteScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.material.BottomSheetNavigator
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.sorrowblue.comicviewer.app.component.AppFab
import com.sorrowblue.comicviewer.app.component.AppFabState
import com.sorrowblue.comicviewer.app.component.NavHostWithSharedAxisX
import com.sorrowblue.comicviewer.framework.designsystem.animation.fabAnimation
import com.sorrowblue.comicviewer.framework.ui.CommonViewModel
import com.sorrowblue.comicviewer.framework.ui.material3.Scaffold
import com.sorrowblue.comicviewer.framework.ui.material3.Text
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList

internal const val MainGraphRoute = "main"

internal data class MainScreenUiState(
    val currentTab: MainScreenTab? = null,
    val tabs: PersistentList<MainScreenTab> = MainScreenTab.entries.toPersistentList(),
    val showNavigation: Boolean = currentTab != null,
)

@OptIn(
    ExperimentalMaterialNavigationApi::class,
    ExperimentalMaterial3AdaptiveNavigationSuiteApi::class
)
@Composable
internal fun MainScreen(
    uiState: MainScreenUiState,
    appFabState: AppFabState,
    bottomSheetNavigator: BottomSheetNavigator,
    navController: NavHostController,
    startDestination: String,
    onTabSelected: (NavController, MainScreenTab) -> Unit,
    onFabClick: (NavController, MainScreenFab) -> Unit,
    viewModel: CommonViewModel = hiltViewModel(LocalContext.current as ComponentActivity),
    navGraph: NavGraphBuilder.(NavHostController, PaddingValues) -> Unit,
) {
    NavigationSuiteScaffold2(
        showNavigation = uiState.showNavigation,
        navigationSuiteItems = {
            uiState.tabs.forEach {
                item(
                    selected = it == uiState.currentTab,
                    onClick = { onTabSelected(navController, it) },
                    icon = {
                        Icon(
                            imageVector = it.icon,
                            contentDescription = stringResource(id = it.label)
                        )
                    },
                    label = {
                        Text(text = it.label)
                    }
                )
            }
        },
        floatingActionButton = {
            AnimatedContent(
                targetState = appFabState.isShown,
                transitionSpec = { fabAnimation() },
                contentAlignment = Alignment.BottomEnd,
                label = "fab",
            ) {
                if (it) {
                    AppFab(appFabState, viewModel.canScroll) {
                        onFabClick(navController, appFabState.mainScreenFab!!)
                    }
                }
            }
        },
        content = {
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
        },
    )
}

@OptIn(ExperimentalMaterial3AdaptiveNavigationSuiteApi::class)
@Composable
fun NavigationSuiteScaffold2(
    showNavigation: Boolean,
    navigationSuiteItems: NavigationSuiteScope.() -> Unit,
    floatingActionButton: @Composable () -> Unit,
    content: @Composable (PaddingValues) -> Unit,
) {
    if (showNavigation) {
        NavigationSuiteScaffold(
            navigationSuiteItems = navigationSuiteItems,
            modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Start))
        ) {
            Scaffold(floatingActionButton = floatingActionButton, content = content)
        }
    } else {
        Scaffold(floatingActionButton = floatingActionButton, content = content)
    }
}
