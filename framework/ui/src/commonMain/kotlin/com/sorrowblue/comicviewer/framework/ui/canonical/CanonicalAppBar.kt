package com.sorrowblue.comicviewer.framework.ui.canonical

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.NavigationSuiteScaffold2State

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun NavigationSuiteScaffold2State<*>.CanonicalAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: @Composable () -> Unit = {},
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    titleHorizontalAlignment: Alignment.Horizontal = Alignment.Start,
) {
    val containerColor by animateColorAsState(if (navigationSuiteType.isNavigationRail) ComicTheme.colorScheme.surfaceContainer else ComicTheme.colorScheme.surface)
    TopAppBar(
        title = title,
        subtitle = subtitle,
        modifier = modifier,
        navigationIcon = navigationIcon,
        actions = actions,
        scrollBehavior = appBarState.scrollBehavior,
        colors = TopAppBarDefaults.topAppBarColors(containerColor = containerColor),
        windowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal),
        titleHorizontalAlignment = titleHorizontalAlignment,
    )
}
