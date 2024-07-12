package com.sorrowblue.comicviewer.framework.ui

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffold
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
