package com.sorrowblue.comicviewer.framework.ui

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.HingePolicy
import androidx.compose.material3.adaptive.layout.PaneScaffoldDirective
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffold
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun <T> AnimatedExtraPaneScaffold(
    extraPane: @Composable () -> Unit,
    navigator: ThreePaneScaffoldNavigator<T>,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    navigator.scaffoldDirective
    SupportingPaneScaffold(
        directive = navigator.scaffoldDirective,
        value = navigator.scaffoldValue,
        mainPane = {
            AnimatedPane(modifier = Modifier) {
                content()
            }
        },
        supportingPane = {
        },
        extraPane = {
            AnimatedPane(modifier = Modifier) {
                extraPane()
            }
        },
        modifier = modifier
    )
}

@ExperimentalMaterial3AdaptiveApi
fun calculateStandardPaneScaffoldDirective(
    windowAdaptiveInfo: WindowAdaptiveInfo,
    verticalHingePolicy: HingePolicy = HingePolicy.AvoidSeparating,
): PaneScaffoldDirective {
    return calculatePaneScaffoldDirective(
        windowAdaptiveInfo = windowAdaptiveInfo,
        verticalHingePolicy = verticalHingePolicy,
    )
}
