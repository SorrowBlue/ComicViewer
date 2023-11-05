package com.sorrowblue.comicviewer.framework.ui.responsive

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.designsystem.theme.MotionTokens
import com.sorrowblue.comicviewer.framework.ui.ComicPreviews
import com.sorrowblue.comicviewer.framework.ui.copy
import com.sorrowblue.comicviewer.framework.ui.material3.PreviewTheme
import com.sorrowblue.comicviewer.framework.ui.material3.ReversePermanentNavigationDrawer
import com.sorrowblue.comicviewer.framework.ui.material3.rememberSnackbarHostState
import com.sorrowblue.comicviewer.framework.ui.preview.rememberMobile

@Stable
open class ResponsiveScaffoldState<T : Any>(
    val sheetState: SideSheetValueState<T>,
    val snackbarHostState: SnackbarHostState,
)

@Composable
fun <T : Any> rememberResponsiveScaffoldState(
    sideSheetState: SideSheetValueState<T>,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
): ResponsiveScaffoldState<T> {
    return remember(sideSheetState, snackbarHostState) {
        ResponsiveScaffoldState(
            sheetState = sideSheetState,
            snackbarHostState = snackbarHostState
        )
    }
}

@Composable
fun <T : Any> ResponsiveScaffold(
    state: ResponsiveScaffoldState<T>,
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    bottomSheet: @Composable ((T) -> Unit),
    sideSheet: @Composable (T, PaddingValues) -> Unit,
    contentWindowInsets: WindowInsets = WindowInsets.safeDrawing,
    content: @Composable (PaddingValues) -> Unit,
) {
    val isCompact = rememberMobile()

    if (isCompact && state.sheetState.show) {
        bottomSheet(state.sheetState.currentValue!!)
    }
    Scaffold(
        modifier = modifier,
        topBar = topBar,
        snackbarHost = { SnackbarHost(state.snackbarHostState) },
        containerColor = if (isCompact) ComicTheme.colorScheme.surface else ComicTheme.colorScheme.surfaceContainer,
        contentWindowInsets = contentWindowInsets
    ) { innerPadding ->
        ReversePermanentNavigationDrawer(
            drawerContent = {
                AnimatedContent(
                    targetState = !isCompact && state.sheetState.show,
                    label = "drawerContent",
                    contentAlignment = Alignment.CenterStart,
                    transitionSpec = {
                        expandHorizontally(
                            animationSpec = tween(
                                durationMillis = MotionTokens.DurationMedium4,
                                delayMillis = 0,
                                easing = MotionTokens.EasingEmphaizedDecelerateInterpolator
                            ), expandFrom = Alignment.Start
                        ) togetherWith shrinkHorizontally(
                            animationSpec = tween(
                                durationMillis = MotionTokens.DurationMedium1,
                                delayMillis = 0,
                                easing = MotionTokens.EasingEmphasizedAccelerateInterpolator
                            ), shrinkTowards = Alignment.Start
                        )
                    }
                ) { visible ->
                    if (visible) {
                        sideSheet(state.sheetState.currentValue!!, innerPadding)
                    } else {
                        Spacer(modifier = Modifier.fillMaxHeight())
                    }
                }
            },
        ) {
            val end by animateDpAsState(
                targetValue = if (state.sheetState.show) 0.dp else innerPadding.calculateEndPadding(
                    LocalLayoutDirection.current
                ), label = "end"
            )

            val padding = innerPadding.copy(end = end)
            content(padding)
        }
    }
}

@ComicPreviews
@Composable
private fun PreviewResponsiveScaffold() {
    PreviewTheme {

        val state =
            rememberResponsiveScaffoldState(sideSheetState = rememberSideSheetBooleanState(true))
        ResponsiveScaffold(
            state = state,
            topBar = {
                ResponsiveTopAppBar(title = "PreviewResponsiveScaffold")
            },
            bottomSheet = {
                Box(Modifier.fillMaxSize())
            },
            sideSheet = { value, contentPadding ->
                SideSheet(title = "Title", innerPadding = contentPadding) {
                    Surface(
                        Modifier
                            .fillMaxSize(), shape = ComicTheme.shapes.large
                    ) {

                    }
                }
            }
        ) { contentPadding ->
            Surface(
                Modifier
                    .padding(contentPadding)
                    .fillMaxSize(), shape = ComicTheme.shapes.large,
                color = Color.Cyan
            ) {

            }
        }
    }
}

@Stable
open class ResponsiveScaffoldState2<T : Any>(
    val sheetState: SideSheetValueState<T>,
    val snackbarHostState: com.sorrowblue.comicviewer.framework.ui.material3.SnackbarHostState,
)

@Composable
fun <T : Any> rememberResponsiveScaffoldState2(
    sideSheetState: SideSheetValueState<T>,
    snackbarHostState: com.sorrowblue.comicviewer.framework.ui.material3.SnackbarHostState = rememberSnackbarHostState(),
): ResponsiveScaffoldState2<T> {
    return remember(sideSheetState, snackbarHostState) {
        ResponsiveScaffoldState2(
            sheetState = sideSheetState,
            snackbarHostState = snackbarHostState
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T : Any> ResponsiveScaffold2(
    state: ResponsiveScaffoldState2<T>,
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    bottomSheet: @Composable (ColumnScope.(T) -> Unit),
    sideSheet: @Composable (T, PaddingValues) -> Unit,
    onBottomSheetDismissRequest: () -> Unit = { state.sheetState.hide() },
    content: @Composable (PaddingValues) -> Unit,
) {
    val isCompact = rememberMobile()
    if (isCompact && state.sheetState.show) {
        ModalBottomSheet(
            onDismissRequest = onBottomSheetDismissRequest,
            containerColor = ComicTheme.colorScheme.surfaceContainerLow,
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            windowInsets = WindowInsets(0)
        ) {
            bottomSheet(state.sheetState.currentValue!!)
        }
    }
    com.sorrowblue.comicviewer.framework.ui.material3.Scaffold(
        topBar = topBar,
        snackbarHostState = state.snackbarHostState,
        modifier = modifier,
    ) { innerPadding ->
        ReversePermanentNavigationDrawer(
            drawerContent = {
                AnimatedContent(
                    targetState = !isCompact && state.sheetState.show,
                    label = "drawerContent",
                    contentAlignment = Alignment.CenterStart,
                    transitionSpec = {
                        expandHorizontally(
                            animationSpec = tween(
                                durationMillis = MotionTokens.DurationMedium4,
                                delayMillis = 0,
                                easing = MotionTokens.EasingEmphaizedDecelerateInterpolator
                            ), expandFrom = Alignment.Start
                        ) togetherWith shrinkHorizontally(
                            animationSpec = tween(
                                durationMillis = MotionTokens.DurationMedium1,
                                delayMillis = 0,
                                easing = MotionTokens.EasingEmphasizedAccelerateInterpolator
                            ), shrinkTowards = Alignment.Start
                        )
                    }
                ) { visible ->
                    if (visible) {
                        sideSheet(state.sheetState.currentValue!!, innerPadding)
                    } else {
                        Spacer(modifier = Modifier.fillMaxHeight())
                    }
                }
            },
        ) {
            val end by animateDpAsState(
                targetValue = if (state.sheetState.show) 0.dp else innerPadding.calculateEndPadding(
                    LocalLayoutDirection.current
                ), label = "end"
            )

            val padding = innerPadding.copy(end = end)
            content(padding)
        }
    }
}
