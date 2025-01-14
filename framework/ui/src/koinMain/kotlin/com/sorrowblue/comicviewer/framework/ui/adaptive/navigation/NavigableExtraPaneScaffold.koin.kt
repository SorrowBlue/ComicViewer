package com.sorrowblue.comicviewer.framework.ui.adaptive.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.PaneAdaptedValue
import androidx.compose.material3.adaptive.layout.PaneExpansionState
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffold
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldScope
import androidx.compose.material3.adaptive.navigation.BackNavigationBehavior
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
internal actual fun NavigableExtraPaneScaffold(
    navigator: ThreePaneScaffoldNavigator<*>,
    extraPane: @Composable () -> Unit,
    modifier: Modifier,
    defaultBackBehavior: BackNavigationBehavior,
    paneExpansionDragHandle: (@Composable ThreePaneScaffoldScope.(PaneExpansionState) -> Unit)?,
    paneExpansionState: PaneExpansionState,
    content: @Composable () -> Unit,
) {
    SupportingPaneScaffold(
        modifier = modifier,
        directive = navigator.scaffoldDirective,
        scaffoldState = navigator.scaffoldState,
        mainPane = {
            AnimatedPane(
                modifier = if (navigator.scaffoldDirective.maxHorizontalPartitions != 1 && navigator.scaffoldValue.tertiary == PaneAdaptedValue.Expanded) {
                    Modifier.consumeWindowInsets(WindowInsets.safeDrawing.only(WindowInsetsSides.End))
                } else {
                    Modifier
                }
            ) {
                content()
            }
        },
        supportingPane = {},
        extraPane = {
            AnimatedPane(
                modifier = if (navigator.scaffoldDirective.maxHorizontalPartitions != 1 && navigator.scaffoldValue.tertiary == PaneAdaptedValue.Expanded) {
                    Modifier.consumeWindowInsets(WindowInsets.safeDrawing.only(WindowInsetsSides.Start))
                } else {
                    Modifier
                }
            ) {
                extraPane()
            }
        },
        paneExpansionDragHandle = paneExpansionDragHandle,
        paneExpansionState = paneExpansionState,
    )
}
