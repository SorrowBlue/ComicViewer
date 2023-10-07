package com.sorrowblue.comicviewer.framework.ui.responsive

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.framework.designsystem.animation.fabAnimation
import com.sorrowblue.comicviewer.framework.designsystem.animation.navigationBarAnimation
import com.sorrowblue.comicviewer.framework.designsystem.animation.navigationRailAnimation
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.designsystem.theme.LocalWindowSize
import com.sorrowblue.comicviewer.framework.ui.copy

@Stable
class NavigationState(
    initialValue: Boolean = true,
) {
    fun show() {
        currentValue = true
    }

    fun hide() {
        currentValue = false
    }

    var currentValue by mutableStateOf(initialValue)
        private set

    companion object {
        fun Saver() = Saver<NavigationState, Boolean>(
            save = { it.currentValue },
            restore = { savedValue ->
                NavigationState(savedValue)
            }
        )
    }
}

@Composable
fun rememberNavigationState(
    initialValue: Boolean = true,
): NavigationState {
    return rememberSaveable(saver = NavigationState.Saver()) {
        NavigationState(initialValue)
    }
}

@Stable
class FabState(
    initialValue: Boolean = true,
) {
    fun show() {
        currentValue = true
    }

    fun hide() {
        currentValue = false
    }

    var currentValue by mutableStateOf(initialValue)
        private set

    companion object {
        fun Saver() = Saver<FabState, Boolean>(
            save = { it.currentValue },
            restore = { savedValue ->
                FabState(savedValue)
            }
        )
    }
}

@Composable
fun rememberFabState(
    initialValue: Boolean = true,
): FabState {
    return rememberSaveable(saver = FabState.Saver()) {
        FabState(initialValue)
    }
}

@Stable
class ResponsiveLayoutState internal constructor(
    val navigationState: NavigationState,
    val fabState: FabState,
    val snackbarHostState: SnackbarHostState,
)

@Composable
fun rememberResponsiveLayoutState(
    fabState: FabState = rememberFabState(),
    navigationState: NavigationState = rememberNavigationState(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
): ResponsiveLayoutState {
    return remember {
        ResponsiveLayoutState(navigationState, fabState, snackbarHostState)
    }
}

@Composable
fun ResponsiveLayout(
    state: ResponsiveLayoutState = rememberResponsiveLayoutState(),
    navigationRail: @Composable () -> Unit,
    navigationBar: @Composable () -> Unit,
    floatingActionButton: @Composable () -> Unit,
    content: @Composable (PaddingValues) -> Unit,
) {
    val widthSizeClass = LocalWindowSize.current.widthSizeClass
    val isCompact = remember(widthSizeClass) {
        widthSizeClass == WindowWidthSizeClass.Compact
    }
    Surface(
        color = ComicTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize(),
    ) {
        Row(Modifier.fillMaxSize()) {
            AnimatedContent(
                targetState = !isCompact && state.navigationState.currentValue,
                contentAlignment = Alignment.CenterStart,
                transitionSpec = { navigationRailAnimation() },
                label = "navigationRail"
            ) {
                if (it) {
                    navigationRail()
                } else {
                    Spacer(modifier = Modifier.fillMaxHeight())
                }
            }
            Scaffold(
                bottomBar = {
                    AnimatedContent(
                        targetState = isCompact && state.navigationState.currentValue,
                        transitionSpec = { navigationBarAnimation() },
                        contentAlignment = Alignment.BottomCenter,
                        label = "navigationBar"
                    ) { isVisible ->
                        if (isVisible) {
                            navigationBar()
                        } else {
                            Spacer(
                                Modifier
                                    .fillMaxWidth()
                                    .navigationBarsPadding()
                            )
                        }
                    }
                },
                floatingActionButton = {
                    AnimatedContent(
                        targetState = isCompact && state.fabState.currentValue,
                        contentAlignment = Alignment.BottomEnd,
                        transitionSpec = { fabAnimation() },
                        label = "fab"
                    ) {
                        if (it) {
                            floatingActionButton()
                        } else {
                            Spacer(modifier = Modifier)
                        }
                    }
                },
                contentWindowInsets = if (!isCompact && state.navigationState.currentValue) WindowInsets.safeDrawing.only(
                    WindowInsetsSides.Vertical + WindowInsetsSides.End
                ) else WindowInsets.safeDrawing,
                snackbarHost = { SnackbarHost(state.snackbarHostState) },
                containerColor = if (isCompact) ComicTheme.colorScheme.surface else ComicTheme.colorScheme.surfaceContainer,
                content = {
                    val startPadding by animateDpAsState(
                        targetValue = when {
                            isCompact -> it.calculateStartPadding(LocalLayoutDirection.current)
                            state.navigationState.currentValue -> 0.dp
                            else -> it.calculateStartPadding(LocalLayoutDirection.current)
                        },
                        label = "startPadding"
                    )
                    CompositionLocalProvider(LocalVisibleNavigationRail provides (!isCompact && state.navigationState.currentValue)) {
                        content(it.copy(start = startPadding))
                    }
                }
            )
        }
    }
}

val LocalVisibleNavigationRail = compositionLocalOf { false }
