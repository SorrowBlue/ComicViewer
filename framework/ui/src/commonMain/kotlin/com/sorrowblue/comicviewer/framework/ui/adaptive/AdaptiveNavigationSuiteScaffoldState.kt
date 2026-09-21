/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.framework.ui.adaptive

import androidx.compose.material3.WideNavigationRailState
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldState
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.material3.adaptive.navigationsuite.rememberNavigationSuiteScaffoldState
import androidx.compose.material3.rememberWideNavigationRailState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState

interface AdaptiveNavigationSuiteScaffoldState : NavigationSuiteScaffoldState {
    fun onNavigationReSelect()

    val navigationSuiteType: NavigationSuiteType
    val wideNavigationRailState: WideNavigationRailState
    val floatingActionButtonState: FloatingActionButtonState
}

@Composable
fun rememberAdaptiveNavigationSuiteScaffoldState(
    onNavigationReSelect: () -> Unit = {},
): AdaptiveNavigationSuiteScaffoldState {
    val navigationSuiteScaffoldState = rememberNavigationSuiteScaffoldState()
    val wideNavigationRailState = rememberWideNavigationRailState()
    val floatingActionButtonState = rememberFloatingActionButtonState()
    val windowAdaptiveInfo = currentWindowAdaptiveInfoV2()
    val navigationSuiteType = remember(windowAdaptiveInfo) {
        NavigationSuiteScaffoldDefaults.navigationSuiteType(windowAdaptiveInfo)
    }
    val currentOnNavigationReSelect by rememberUpdatedState(onNavigationReSelect)
    return remember(
        navigationSuiteScaffoldState,
        wideNavigationRailState,
        floatingActionButtonState,
        navigationSuiteType,
    ) {
        AdaptiveNavigationSuiteScaffoldStateImpl(
            navigationSuiteScaffoldState = navigationSuiteScaffoldState,
            wideNavigationRailState = wideNavigationRailState,
            floatingActionButtonState = floatingActionButtonState,
            navigationSuiteType = navigationSuiteType,
            onNavigationReSelect = { currentOnNavigationReSelect() },
        )
    }
}

private class AdaptiveNavigationSuiteScaffoldStateImpl(
    navigationSuiteScaffoldState: NavigationSuiteScaffoldState,
    override val wideNavigationRailState: WideNavigationRailState,
    override val floatingActionButtonState: FloatingActionButtonState,
    override val navigationSuiteType: NavigationSuiteType,
    private val onNavigationReSelect: () -> Unit,
) : AdaptiveNavigationSuiteScaffoldState,
    NavigationSuiteScaffoldState by navigationSuiteScaffoldState {

    override fun onNavigationReSelect() {
        onNavigationReSelect.invoke()
    }
}
