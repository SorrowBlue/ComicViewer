package com.sorrowblue.comicviewer.framework.ui.adaptive

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.PaneScaffoldDirective
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.designsystem.theme.LocalContainerColor

object ExtraPaneScaffoldDefaults {
    val HorizontalPadding = 24.dp
}

@Composable
fun ExtraPaneScaffold(
    title: @Composable () -> Unit,
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier,
    actions: @Composable (RowScope.() -> Unit)? = null,
    scaffoldDirective: PaneScaffoldDirective =
        calculatePaneScaffoldDirective(currentWindowAdaptiveInfo()),
    scrollState: ScrollState? = null,
    content: @Composable ColumnScope.(PaddingValues) -> Unit,
) {
    val singlePane by remember(scaffoldDirective.maxHorizontalPartitions) {
        mutableStateOf(scaffoldDirective.maxHorizontalPartitions == 1)
    }
    val scrollBehavior = if (singlePane) TopAppBarDefaults.pinnedScrollBehavior() else null
    Scaffold(
        containerColor = if (singlePane) LocalContainerColor.current else ComicTheme.colorScheme.surfaceContainer,
        topBar = {
            Column {
                TopAppBar(
                    title = title,
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = if (singlePane) ComicTheme.colorScheme.surface else ComicTheme.colorScheme.surfaceContainer
                    ),
                    actions = {
                        IconButton(onClick = onCloseClick) {
                            Icon(ComicIcons.Close, null)
                        }
                    },
                    windowInsets = if (singlePane) {
                        WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal)
                    } else {
                        WindowInsets(0)
                    },
                    scrollBehavior = scrollBehavior
                )
                if (scrollState != null && scrollBehavior == null) {
                    if (scrollState.canScrollBackward) {
                        HorizontalDivider()
                    }
                }
            }
        },
        contentWindowInsets = WindowInsets.safeDrawing,
        modifier = if (scrollBehavior != null) {
            modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
        } else {
            val marginWindowInsets = with(LocalLayoutDirection.current) {
                with(ComicTheme.dimension) {
                    WindowInsets(top = margin, end = margin, bottom = margin)
                }
            }
            modifier
                .windowInsetsPadding(WindowInsets.safeDrawing.union(marginWindowInsets))
                .clip(ComicTheme.shapes.large)
        }
    ) {
        Column {
            Column(modifier = Modifier.weight(1f)) {
                content(it)
            }
            if (actions != null) {
                HorizontalDivider()
                Spacer(Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.Start),
                    modifier = Modifier.padding(horizontal = 24.dp)
                ) {
                    actions()
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}
