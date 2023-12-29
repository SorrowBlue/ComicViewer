package com.sorrowblue.comicviewer.app

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation.suite.ExperimentalMaterial3AdaptiveNavigationSuiteApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.google.accompanist.navigation.material.BottomSheetNavigator
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.sorrowblue.comicviewer.framework.ui.NavigationSuiteScaffold
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
    ExperimentalMaterial3AdaptiveNavigationSuiteApi::class, ExperimentalMaterial3AdaptiveApi::class
)
@Composable
internal fun MainScreen(
    uiState: MainScreenUiState,
    bottomSheetNavigator: BottomSheetNavigator,
    navController: NavHostController,
    startDestination: String,
    onTabSelected: (NavController, MainScreenTab) -> Unit,
    navGraph: NavGraphBuilder.(PaddingValues) -> Unit,
) {
    NavigationSuiteScaffold(
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
                        Text(text = stringResource(id = it.label))
                    }
                )
            }
        }
    ) {
        ModalBottomSheetLayout(bottomSheetNavigator) {
            NavHost(
                navController = navController,
                route = MainGraphRoute,
                startDestination = startDestination,
                modifier = Modifier
            ) {
                navGraph(it)
            }
        }
    }
}
