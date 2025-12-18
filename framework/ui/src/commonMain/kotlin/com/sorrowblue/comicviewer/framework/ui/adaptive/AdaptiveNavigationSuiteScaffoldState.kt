package com.sorrowblue.comicviewer.framework.ui.adaptive

import androidx.compose.material3.WideNavigationRailState
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldState
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.material3.adaptive.navigationsuite.rememberNavigationSuiteScaffoldState
import androidx.compose.material3.rememberWideNavigationRailState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.sorrowblue.comicviewer.framework.ui.canonical.FloatingActionButtonState
import com.sorrowblue.comicviewer.framework.ui.canonical.rememberFloatingActionButtonState

interface AdaptiveNavigationSuiteScaffoldState : NavigationSuiteScaffoldState {
    val navigationSuiteType: NavigationSuiteType
    val wideNavigationRailState: WideNavigationRailState
    val floatingActionButtonState: FloatingActionButtonState
}

@Composable
fun rememberAdaptiveNavigationSuiteScaffoldState(): AdaptiveNavigationSuiteScaffoldState {
    val navigationSuiteScaffoldState = rememberNavigationSuiteScaffoldState()
    val navigationSuiteType =
        NavigationSuiteScaffoldDefaults.navigationSuiteType(currentWindowAdaptiveInfo())
    return remember {
        AdaptiveNavigationSuiteScaffoldStateImpl(
            navigationSuiteScaffoldState = navigationSuiteScaffoldState,
        )
    }.apply {
        this.navigationSuiteType = navigationSuiteType
        this.wideNavigationRailState = rememberWideNavigationRailState()
        this.floatingActionButtonState = rememberFloatingActionButtonState()
    }
}

private class AdaptiveNavigationSuiteScaffoldStateImpl(
    navigationSuiteScaffoldState: NavigationSuiteScaffoldState,
) : AdaptiveNavigationSuiteScaffoldState,
    NavigationSuiteScaffoldState by navigationSuiteScaffoldState {
    override var navigationSuiteType by mutableStateOf(NavigationSuiteType.None)
    override lateinit var wideNavigationRailState: WideNavigationRailState
    override lateinit var floatingActionButtonState: FloatingActionButtonState
}
