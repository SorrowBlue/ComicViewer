package com.sorrowblue.comicviewer.framework.ui.adaptive.navigation

import androidx.compose.material3.adaptive.layout.PaneExpansionState
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldPaneScope
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldScope
import androidx.compose.material3.adaptive.navigation.BackNavigationBehavior
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
actual fun <T> NavigableListDetailPaneScaffold(
    navigator: ThreePaneScaffoldNavigator<T>,
    listPane: @Composable ThreePaneScaffoldPaneScope.() -> Unit,
    detailPane: @Composable ThreePaneScaffoldPaneScope.() -> Unit,
    modifier: Modifier,
    extraPane: @Composable (ThreePaneScaffoldPaneScope.() -> Unit)?,
    defaultBackBehavior: BackNavigationBehavior,
    paneExpansionDragHandle: @Composable (ThreePaneScaffoldScope.(PaneExpansionState) -> Unit)?,
    paneExpansionState: PaneExpansionState,
) {
    androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold(
        navigator = navigator,
        listPane = listPane,
        detailPane = detailPane,
        extraPane = extraPane,
        defaultBackBehavior = defaultBackBehavior,
        paneExpansionDragHandle = paneExpansionDragHandle,
        paneExpansionState = paneExpansionState,
        modifier = modifier
    )
}
