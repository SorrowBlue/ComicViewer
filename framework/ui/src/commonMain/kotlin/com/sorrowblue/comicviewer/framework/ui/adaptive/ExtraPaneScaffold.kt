/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.framework.ui.adaptive

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.material3.adaptive.layout.PaneScaffoldDirective
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.layout.WindowInsets
import com.sorrowblue.comicviewer.framework.ui.material3.CloseIconButton

/**
 * An adaptive scaffold designed for extra or secondary panes in a multi-pane layout.
 *
 * This scaffold adapts its appearance and layout behavior based on whether it is displayed
 * in a single-pane or multi-pane configuration (determined by [scaffoldDirective]):
 * - **Single-pane layout**: Acts like a standard full-screen scaffold with a pinned [TopAppBar],
 *   a surface container color, and full safe drawing insets.
 * - **Multi-pane layout**: Renders as a floating card-style pane with a higher surface container color
 *   ([ComicTheme.colorScheme.surfaceContainerHigh]), clipped to [ComicTheme.shapes.large],
 *   with appropriate margins and window insets applied. When [scrollState] is provided and content
 *   is scrolled, a [HorizontalDivider] is displayed beneath the top bar.
 *
 * @param title The title composable displayed in the [TopAppBar].
 * @param onCloseClick Callback invoked when the close icon button is clicked.
 * @param modifier The [Modifier] to be applied to the scaffold.
 * @param actions The composable displayed in the bottom bar slot (e.g. action buttons).
 * @param scaffoldDirective The [PaneScaffoldDirective] specifying pane layout configuration,
 * defaults to calculating from [currentWindowAdaptiveInfoV2].
 * @param scrollState The [ScrollState] of the inner content, used in multi-pane mode to display
 * a divider under the top app bar when scrolled backward. Pass `null` if scrolling is not tracked.
 * @param content The main content of the scaffold, receiving the scaffold's [PaddingValues].
 */
@Composable
fun ExtraPaneScaffold(
    title: @Composable () -> Unit,
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier,
    actions: @Composable () -> Unit = {},
    scaffoldDirective: PaneScaffoldDirective =
        calculatePaneScaffoldDirective(currentWindowAdaptiveInfoV2()),
    scrollState: ScrollState? = null,
    content: @Composable (PaddingValues) -> Unit,
) {
    val singlePane = scaffoldDirective.maxHorizontalPartitions == 1
    val scrollBehavior = if (singlePane) TopAppBarDefaults.pinnedScrollBehavior() else null
    val containerColor by animateColorAsState(
        if (singlePane) ComicTheme.colorScheme.surface else ComicTheme.colorScheme.surfaceContainerHigh,
    )
    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = title,
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = containerColor),
                    actions = {
                        CloseIconButton(onClick = onCloseClick)
                    },
                    windowInsets = if (singlePane) {
                        WindowInsets.safeDrawing.only(
                            WindowInsetsSides.Top + WindowInsetsSides.Horizontal,
                        )
                    } else {
                        WindowInsets(0)
                    },
                    scrollBehavior = scrollBehavior,
                )
                if (scrollState != null && scrollBehavior == null) {
                    if (scrollState.canScrollBackward) {
                        HorizontalDivider()
                    }
                }
            }
        },
        contentWindowInsets = if (singlePane) {
            WindowInsets.safeDrawing
        } else {
            WindowInsets.safeDrawing.only(
                WindowInsetsSides.Vertical + WindowInsetsSides.End,
            )
        },
        containerColor = containerColor,
        modifier = if (scrollBehavior != null) {
            modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
        } else {
            val marginWindowInsets = with(LocalLayoutDirection.current) {
                with(ComicTheme.dimension) {
                    WindowInsets(top = margin, end = margin, bottom = margin)
                }
            }
            modifier
                .windowInsetsPadding(
                    WindowInsets.safeDrawing
                        .only(
                            WindowInsetsSides.Vertical + WindowInsetsSides.End,
                        ).union(marginWindowInsets),
                ).clip(ComicTheme.shapes.large)
        },
        bottomBar = actions,
        content = content,
    )
}

/**
 * Default values used by [ExtraPaneScaffold].
 */
object ExtraPaneScaffoldDefaults {

    /**
     * Default content padding value for the extra pane.
     */
    val Padding = 24.dp
}
