package com.sorrowblue.comicviewer.framework.ui.adaptive.navigation

import androidx.compose.material3.adaptive.layout.PaneExpansionState
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldPaneScope
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldScope
import androidx.compose.material3.adaptive.layout.rememberPaneExpansionState
import androidx.compose.material3.adaptive.navigation.BackNavigationBehavior
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
internal expect fun NavigableExtraPaneScaffold(
    navigator: ThreePaneScaffoldNavigator<*>,
    extraPane: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    defaultBackBehavior: BackNavigationBehavior = BackNavigationBehavior.PopUntilScaffoldValueChange,
    paneExpansionDragHandle: (@Composable ThreePaneScaffoldScope.(PaneExpansionState) -> Unit)? = null,
    paneExpansionState: PaneExpansionState = rememberPaneExpansionState(navigator.scaffoldValue),
    content: @Composable () -> Unit,
)
