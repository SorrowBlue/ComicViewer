package com.sorrowblue.comicviewer.framework.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.PaneAdaptedValue
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.framework.designsystem.animation.topAppBarAnimation
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun <T> CanonicalScaffold(
    navigator: ThreePaneScaffoldNavigator<T>,
    modifier: Modifier = Modifier,
    extraPane: @Composable (PaddingValues) -> Unit = {},
    extraPaneVisible: (@Composable (PaddingValues, T) -> Unit)? = null,
    topBar: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    snackbarHost: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        topBar = {
            AnimatedContent(
                targetState = navigator.scaffoldDirective.maxHorizontalPartitions != 1 ||
                    navigator.scaffoldValue.tertiary == PaneAdaptedValue.Hidden,
                transitionSpec = { topAppBarAnimation() },
                contentAlignment = Alignment.TopCenter,
                label = "top_app_bar"
            ) {
                if (it) {
                    topBar()
                } else {
                    Spacer(modifier = Modifier.fillMaxWidth())
                }
            }
        },
        floatingActionButton = {
            if (navigator.scaffoldValue.tertiary == PaneAdaptedValue.Hidden) {
                floatingActionButton()
            }
        },
        snackbarHost = snackbarHost,
        contentWindowInsets = WindowInsets.safeDrawing,
        containerColor = ComicTheme.colorScheme.surface,
        modifier = modifier
    ) { innerPadding ->
        AnimatedExtraPaneScaffold(
            navigator = navigator,
            extraPane = {
                if (extraPaneVisible != null) {
                    navigator.currentDestination?.content?.let {
                        extraPaneVisible(innerPadding, it)
                    }
                } else {
                    extraPane(innerPadding)
                }
            }
        ) {
            val end by animateDpAsState(
                targetValue = if (navigator.scaffoldValue.tertiary == PaneAdaptedValue.Expanded) {
                    0.dp
                } else {
                    innerPadding.calculateEndPadding(LocalLayoutDirection.current)
                },
                label = "end"
            )
            content(innerPadding.copy(end = end))
        }
    }
}
