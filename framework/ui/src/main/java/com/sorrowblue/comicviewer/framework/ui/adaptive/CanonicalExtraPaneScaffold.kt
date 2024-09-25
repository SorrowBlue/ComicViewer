package com.sorrowblue.comicviewer.framework.ui.adaptive

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.designsystem.theme.LocalComponentColors
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.LocalNavigationState
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.NavigationState
import com.sorrowblue.comicviewer.framework.ui.add
import com.sorrowblue.comicviewer.framework.ui.material3.CloseIconButton

@Composable
fun CanonicalExtraPaneScaffold(
    title: @Composable () -> Unit,
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (PaddingValues) -> Unit,
) {
    val navigationState = LocalNavigationState.current
    val scrollBehavior =
        if (navigationState is NavigationState.NavigationBar) TopAppBarDefaults.pinnedScrollBehavior() else null
    Scaffold(
        topBar = {
            Box {
                TopAppBar(
                    title = title,
                    actions = {
                        CloseIconButton(onClick = onCloseClick)
                    },
                    windowInsets = if (navigationState is NavigationState.NavigationBar) {
                        WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
                    } else {
                        WindowInsets(0)
                    },
                    scrollBehavior = scrollBehavior,
                )
            }
        },
        containerColor = with(LocalComponentColors.current) {
            if (navigationState is NavigationState.NavigationBar) containerColor else contentColor
        },
        modifier = if (scrollBehavior != null) {
            modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
        } else {
            modifier
                .windowInsetsPadding(WindowInsets.safeDrawing.zero(ignoreStart = true))
                .clip(ComicTheme.shapes.large)
        },
        contentWindowInsets = if (navigationState is NavigationState.NavigationBar) {
            WindowInsets.safeDrawing
        } else {
            WindowInsets(0)
        },
    ) { contentPadding ->
        if (navigationState is NavigationState.NavigationBar) {
            with(ComicTheme.dimension) {
                PaddingValues(start = margin, end = margin, bottom = margin)
            }
        } else {
            PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp)
        }.also {
            content(contentPadding.add(it))
        }
    }
}

@Composable
fun WindowInsets.zero(
    ignoreStart: Boolean = false,
    ignoreTop: Boolean = false,
    ignoreEnd: Boolean = false,
    ignoreBottom: Boolean = false,
): WindowInsets {
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current
    with(density) {
        val left: Dp
        val right: Dp
        if (LocalLayoutDirection.current == LayoutDirection.Ltr) {
            left = if (ignoreStart || getLeft(
                    this,
                    layoutDirection
                ) > 0
            ) 0.dp else ComicTheme.dimension.margin
            right = if (ignoreEnd || getRight(
                    this,
                    layoutDirection
                ) > 0
            ) 0.dp else ComicTheme.dimension.margin
        } else {
            right = if (ignoreStart || getRight(
                    this,
                    layoutDirection
                ) > 0
            ) 0.dp else ComicTheme.dimension.margin
            left = if (ignoreEnd || getLeft(
                    this,
                    layoutDirection
                ) > 0
            ) 0.dp else ComicTheme.dimension.margin
        }
        val top = if (ignoreTop || getTop(this) > 0) 0.dp else ComicTheme.dimension.margin
        val bottom = if (ignoreBottom || getBottom(this) > 0) 0.dp else ComicTheme.dimension.margin
        return add(WindowInsets(left = left, top = top, right = right, bottom = bottom))
    }
}

