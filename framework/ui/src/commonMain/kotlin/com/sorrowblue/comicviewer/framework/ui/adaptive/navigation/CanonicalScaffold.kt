package com.sorrowblue.comicviewer.framework.ui.adaptive.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.adaptive.layout.PaneAdaptedValue
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onLayoutRectChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.framework.designsystem.animation.fabEnter
import com.sorrowblue.comicviewer.framework.designsystem.animation.fabExit
import com.sorrowblue.comicviewer.framework.designsystem.animation.topAppBarAnimation
import com.sorrowblue.comicviewer.framework.designsystem.theme.LocalContainerColor
import com.sorrowblue.comicviewer.framework.ui.layout.asWindowInsets
import com.sorrowblue.comicviewer.framework.ui.layout.plus
import kotlin.coroutines.EmptyCoroutineContext
import kotlinx.coroutines.CoroutineScope

val LocalCoroutineScope = compositionLocalOf { CoroutineScope(EmptyCoroutineContext) }

@Composable
fun <T : Any> CanonicalScaffold(
    navigator: ThreePaneScaffoldNavigator<T>,
    modifier: Modifier = Modifier,
    topBar: @Composable (() -> Unit)? = null,
    extraPane: @Composable ((T) -> Unit)? = null,
    snackbarHost: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    var fabHeight by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current
    CompositionLocalProvider(LocalCoroutineScope provides rememberCoroutineScope()) {
        Scaffold(
            floatingActionButton = {
                AnimatedVisibility(
                    navigator.scaffoldState.targetState.tertiary == PaneAdaptedValue.Hidden,
                    enter = fabEnter(),
                    exit = fabExit(),
                    label = "fab"
                ) {
                    Box(
                        modifier = Modifier
                            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.End))
                            .onLayoutRectChanged {
                                with(density) {
                                    fabHeight = it.height.toDp() + FabSpacing
                                }
                            }
                    ) {
                        floatingActionButton()
                    }
                }
            },
            contentWindowInsets = WindowInsets.safeDrawing,
            snackbarHost = snackbarHost,
            modifier = modifier
        ) { padding ->
            NavigableExtraPaneScaffold(
                navigator = navigator,
                extraPane = {
                    if (extraPane != null) {
                        navigator.currentDestination?.contentKey?.let { contentKey ->
                            extraPane(contentKey)
                        }
                    }
                },
            ) {
                Scaffold(
                    topBar = if (topBar != null) {
                        {
                            AnimatedContent(
                                targetState = navigator.scaffoldValue.primary == PaneAdaptedValue.Expanded,
                                transitionSpec = { topAppBarAnimation() },
                                contentAlignment = Alignment.TopCenter,
                                label = "topBar",
                            ) {
                                if (it) {
                                    topBar()
                                } else {
                                    Spacer(
                                        Modifier
                                            .fillMaxWidth()
                                            .windowInsetsPadding(
                                                WindowInsets.safeDrawing.only(
                                                    WindowInsetsSides.Horizontal + WindowInsetsSides.Top
                                                )
                                            )
                                    )
                                }
                            }
                        }
                    } else {
                        {}
                    },
                    contentWindowInsets = padding.asWindowInsets(),
                    containerColor = LocalContainerColor.current
                ) { contentPadding ->
                    val navigationState = LocalNavigationState.current
                    val scaffoldBound by remember(
                        navigator.scaffoldDirective.maxHorizontalPartitions,
                        navigator.scaffoldState.targetState.tertiary,
                        navigationState,
                        topBar
                    ) {
                        mutableStateOf(
                            CanonicalScaffoldBound(
                                start = navigationState is NavigationState.NavigationBar || !navigationState.visible,
                                top = topBar == null,
                                end = navigator.scaffoldDirective.maxHorizontalPartitions == 1 || navigator.scaffoldState.targetState.tertiary != PaneAdaptedValue.Expanded,
                                bottom = true
                            )
                        )
                    }
                    CompositionLocalProvider(LocalCanonicalScaffoldBound provides scaffoldBound) {
                        content(contentPadding + PaddingValues(bottom = fabHeight))
                    }
                }
            }
        }
    }
}

/** 境界が必要な場合はtrue */
internal data class CanonicalScaffoldBound(
    val start: Boolean = false,
    val top: Boolean = false,
    val end: Boolean = false,
    val bottom: Boolean = false,
)

internal val LocalCanonicalScaffoldBound = compositionLocalOf { CanonicalScaffoldBound() }

private val FabSpacing = 16.dp
