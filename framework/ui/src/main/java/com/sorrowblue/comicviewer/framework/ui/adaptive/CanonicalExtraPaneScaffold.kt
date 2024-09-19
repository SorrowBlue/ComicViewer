package com.sorrowblue.comicviewer.framework.ui.adaptive

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
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
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.designsystem.theme.LocalComponentColors
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.LocalNavigationState
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.NavigationState
import com.sorrowblue.comicviewer.framework.ui.add
import com.sorrowblue.comicviewer.framework.ui.asWindowInsets
import com.sorrowblue.comicviewer.framework.ui.material3.CloseIconButton

@Composable
fun CanonicalExtraPaneScaffold(
    title: @Composable () -> Unit,
    onCloseClick: () -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    content: @Composable (PaddingValues) -> Unit,
) {
    val navigationState = LocalNavigationState.current
    val scrollBehavior =
        if (navigationState is NavigationState.NavigationBar) TopAppBarDefaults.pinnedScrollBehavior() else null
    Scaffold(
        topBar = {
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
        },
        containerColor = if (navigationState is NavigationState.NavigationBar) {
            LocalComponentColors.current.containerColor
        } else {
            LocalComponentColors.current.contentColor
        },
        modifier = if (navigationState is NavigationState.NavigationBar) {
            modifier.nestedScroll(scrollBehavior!!.nestedScrollConnection)
        } else {
            modifier
                .windowInsetsPadding(contentPadding.asWindowInsets())
                .clip(ComicTheme.shapes.large)
        },
        contentWindowInsets = if (navigationState is NavigationState.NavigationBar) {
            with(ComicTheme.dimension) {
                contentPadding.add(PaddingValues(start = margin, end = margin, bottom = margin))
                    .asWindowInsets()
            }
        } else {
            with(ComicTheme.dimension) {
                contentPadding.add(PaddingValues(start = padding, end = padding, bottom = padding))
                    .asWindowInsets()
            }
        },
    ) {
        content(it)
    }
}
