package com.sorrowblue.comicviewer.framework.ui.adaptive.navigation

import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
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
    ListDetailPaneScaffold(
        modifier = modifier,
        directive = navigator.scaffoldDirective,
        scaffoldState = navigator.scaffoldState,
        detailPane = detailPane,
        listPane = listPane,
        extraPane = extraPane,
        paneExpansionDragHandle = paneExpansionDragHandle,
        paneExpansionState = paneExpansionState,
    )
}
