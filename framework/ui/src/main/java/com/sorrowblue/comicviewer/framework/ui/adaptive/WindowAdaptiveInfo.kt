package com.sorrowblue.comicviewer.framework.ui.adaptive

import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.PaneScaffoldDirective
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldDestinationItem
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.window.core.layout.WindowHeightSizeClass
import androidx.window.core.layout.WindowWidthSizeClass

@Composable
fun isCompactWindowClass(): Boolean {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    return windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT || windowSizeClass.windowHeightSizeClass == WindowHeightSizeClass.COMPACT
}

@Composable
fun <T> rememberFixListDetailPaneScaffoldNavigator(
    scaffoldDirective: PaneScaffoldDirective = calculatePaneScaffoldDirective(
        currentWindowAdaptiveInfo()
    ),
    initialDestinationHistory: List<ThreePaneScaffoldDestinationItem<T>> = listOf(
        ThreePaneScaffoldDestinationItem(ListDetailPaneScaffoldRole.List)
    ),
) = rememberListDetailPaneScaffoldNavigator(
    scaffoldDirective = scaffoldDirective,
    initialDestinationHistory = initialDestinationHistory
)
