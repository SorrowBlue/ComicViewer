package com.sorrowblue.comicviewer.framework.ui.layout

import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldState
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.material3.adaptive.navigationsuite.rememberNavigationSuiteScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf

interface ScaffoldState {
    val navigationSuiteType: NavigationSuiteType
    val navigationSuiteScaffoldState: NavigationSuiteScaffoldState
}

val LocalScaffoldState = staticCompositionLocalOf<ScaffoldState> {
    error("No ScaffoldState provided")
}

@Composable
fun rememberAdaptiveNavigationSuiteScaffoldState(
    navigationSuiteType: NavigationSuiteType = NavigationSuiteScaffoldDefaults.navigationSuiteType(
        currentWindowAdaptiveInfo()
    ),
    navigationSuiteScaffoldState: NavigationSuiteScaffoldState = rememberNavigationSuiteScaffoldState(),
    floatingActionButtonState: FloatingActionButtonState = rememberFloatingActionButtonState(),
): AdaptiveNavigationSuiteScaffoldState {
    return remember {
        AdaptiveNavigationSuiteScaffoldStateImpl(
            navigationSuiteType = navigationSuiteType,
            navigationSuiteScaffoldState = navigationSuiteScaffoldState,
            floatingActionButtonState = floatingActionButtonState,
        )
    }
}

interface AdaptiveNavigationSuiteScaffoldState : ScaffoldState {

    val floatingActionButtonState: FloatingActionButtonState

    fun toggleNavigationRail()
    fun updateNavigationSuiteType(suiteType: NavigationSuiteType)
}

private class AdaptiveNavigationSuiteScaffoldStateImpl(
    navigationSuiteType: NavigationSuiteType,
    override val navigationSuiteScaffoldState: NavigationSuiteScaffoldState,
    override val floatingActionButtonState: FloatingActionButtonState,
) : AdaptiveNavigationSuiteScaffoldState {

    override var navigationSuiteType: NavigationSuiteType by mutableStateOf(navigationSuiteType)

    override fun toggleNavigationRail() {
        if (navigationSuiteType == NavigationSuiteType.WideNavigationRailCollapsed) {
            navigationSuiteType = NavigationSuiteType.WideNavigationRailExpanded
        } else if (navigationSuiteType == NavigationSuiteType.WideNavigationRailExpanded) {
            navigationSuiteType = NavigationSuiteType.WideNavigationRailCollapsed
        }
    }

    override fun updateNavigationSuiteType(suiteType: NavigationSuiteType) {
        navigationSuiteType = suiteType
    }
}
