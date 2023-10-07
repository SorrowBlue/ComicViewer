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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
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
import com.sorrowblue.comicviewer.framework.designsystem.theme.LocalWindowSize
import com.sorrowblue.comicviewer.framework.designsystem.theme.MotionTokens
import com.sorrowblue.comicviewer.framework.ui.ComicPreviews
import com.sorrowblue.comicviewer.framework.ui.add
import com.sorrowblue.comicviewer.framework.ui.copy
import com.sorrowblue.comicviewer.framework.ui.material3.PreviewTheme
import com.sorrowblue.comicviewer.framework.ui.material3.ReversePermanentNavigationDrawer

@Stable
data class ResponsiveScaffoldState<T : Any> internal constructor(
    val isVisibleFab: Boolean,
    val sheetState: SideSheetValueState<T>,
    val snackbarHostState: SnackbarHostState,
)

@Composable
fun <T : Any> rememberResponsiveScaffoldState(
    isVisibleFab: Boolean = false,
    sideSheetState: SideSheetValueState<T>,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
): ResponsiveScaffoldState<T> {
    return remember(sideSheetState, snackbarHostState) {
        ResponsiveScaffoldState(
            isVisibleFab = isVisibleFab,
            sheetState = sideSheetState,
            snackbarHostState = snackbarHostState
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T : Any> ResponsiveScaffold(
    state: ResponsiveScaffoldState<T>,
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    bottomSheet: @Composable (ColumnScope.(T) -> Unit),
    sideSheet: @Composable (T, PaddingValues) -> Unit,
    onBottomSheetDismissRequest: () -> Unit = { state.sheetState.hide() },
    windowSizeClass: WindowSizeClass = LocalWindowSize.current,
    contentWindowInsets: WindowInsets = ScaffoldDefaults.contentWindowInsets,
    content: @Composable (PaddingValues) -> Unit,
) {
    val isCompact = remember(windowSizeClass.widthSizeClass) {
        windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact
    }
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
                targetValue = if (state.sheetState.show) {
                    ComicTheme.dimension.spacer
                } else {
                    innerPadding.calculateEndPadding(LocalLayoutDirection.current) + ComicTheme.dimension.margin
                }, label = "end"
            )
            val padding = if (isCompact) {
                innerPadding
                    .add(
                        PaddingValues(
                            start = ComicTheme.dimension.margin,
                            end = ComicTheme.dimension.margin,
                            bottom = ComicTheme.dimension.margin + if (state.isVisibleFab) 56.dp + 16.dp else 0.dp
                        )
                    )
            } else {
                innerPadding
                    .copy(end = end)
                    .add(
                        paddingValues = PaddingValues(
                            top = ComicTheme.dimension.spacer,
                            bottom = ComicTheme.dimension.margin
                        )
                    )
            }
            content(padding)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@ComicPreviews
@Composable
private fun PreviewResponsiveScaffold() {
    PreviewTheme {

        val state =
            rememberResponsiveScaffoldState(sideSheetState = rememberSideSheetBooleanState(true))
        ResponsiveScaffold(
            state = state,
            topBar = {
                ResponsiveTopAppBar(
                    title = {
                        Text(text = "PreviewResponsiveScaffold")
                    },
                )
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
