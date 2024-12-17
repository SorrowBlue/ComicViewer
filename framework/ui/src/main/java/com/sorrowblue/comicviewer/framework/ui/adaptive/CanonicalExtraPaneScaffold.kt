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
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.LocalNavigationState
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.NavigationState
import com.sorrowblue.comicviewer.framework.ui.add
import com.sorrowblue.comicviewer.framework.ui.asWindowInsets
import com.sorrowblue.comicviewer.framework.ui.material3.CloseIconButton

data class CanonicalExtraPaneScaffoldColors(
    val containerColor: Color,
)

object CanonicalExtraPaneScaffoldDefault {

    @Composable
    fun colors(navigator: ThreePaneScaffoldNavigator<Any>): CanonicalExtraPaneScaffoldColors =
        CanonicalExtraPaneScaffoldColors(
            containerColor = if (navigator.scaffoldDirective.maxHorizontalPartitions == 1) ComicTheme.colorScheme.surface else ComicTheme.colorScheme.surfaceContainer
        )
}

@Deprecated("")
@Composable
fun CanonicalExtraPaneScaffold(
    title: @Composable () -> Unit,
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
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
                colors = TopAppBarDefaults.topAppBarColors(),
                scrollBehavior = scrollBehavior,
            )
        },
        modifier = if (scrollBehavior != null) {
            modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
        } else {
            modifier
                .windowInsetsPadding(
                    contentPadding
                        .copyWhenZero(skipStart = true)
                        .asWindowInsets()
                )
                .clip(ComicTheme.shapes.large)
        },
        contentWindowInsets = if (navigationState is NavigationState.NavigationBar) {
            WindowInsets.safeDrawing
        } else {
            WindowInsets(0)
        },
    ) { innerPadding ->
        content(innerPadding)
    }
}

@Composable
fun CanonicalScaffoldExtraPaneScope.ExtraPaneScaffold(
    title: @Composable () -> Unit,
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: CanonicalExtraPaneScaffoldColors = CanonicalExtraPaneScaffoldDefault.colors(navigator),
    content: @Composable (PaddingValues) -> Unit,
) {
    val scrollBehavior =
        if (navigator.scaffoldDirective.maxHorizontalPartitions == 1) TopAppBarDefaults.pinnedScrollBehavior() else null
    Scaffold(
        topBar = {
            TopAppBar(
                title = title,
                actions = {
                    CloseIconButton(onClick = onCloseClick)
                },
                windowInsets = if (navigator.scaffoldDirective.maxHorizontalPartitions == 1) {
                    WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
                } else {
                    WindowInsets(0)
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colors.containerColor),
                scrollBehavior = scrollBehavior,
            )
        },
        containerColor = colors.containerColor,
        modifier = if (scrollBehavior != null) {
            modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
        } else {
            modifier
                .windowInsetsPadding(
                    contentPadding
                        .copyWhenZero(skipStart = true)
                        .add(if (topAppBar) PaddingValues(top = ComicTheme.dimension.margin) else PaddingValues())
                        .asWindowInsets()
                )
                .clip(ComicTheme.shapes.large)
        },
        contentWindowInsets = if (navigator.scaffoldDirective.maxHorizontalPartitions == 1) {
            WindowInsets.safeDrawing
        } else {
            WindowInsets(0)
        },
    ) { contentPadding ->
        content(contentPadding)
    }
}
