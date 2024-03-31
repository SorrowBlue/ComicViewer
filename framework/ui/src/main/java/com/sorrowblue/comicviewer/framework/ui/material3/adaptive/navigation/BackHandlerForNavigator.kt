package com.sorrowblue.comicviewer.framework.ui.material3.adaptive.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.PaneAdaptedValue
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun BackHandlerForNavigator(navigator: ThreePaneScaffoldNavigator<*>) {
    BackHandler(enabled = navigator.scaffoldValue.tertiary == PaneAdaptedValue.Expanded) {
        navigator.navigateBack()
    }
}
