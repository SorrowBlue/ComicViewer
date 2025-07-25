package com.sorrowblue.comicviewer.framework.ui

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.AppBarRow2
import androidx.compose.material3.AppBarRowScope2
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.canonical.isNavigationBar

@Composable
fun CenteredAppBar(
    navigationSuiteType: NavigationSuiteType,
    title: @Composable () -> Unit,
    navigationIcon: @Composable () -> Unit = {},
    actions: AppBarRowScope2.() -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    CenterAlignedTopAppBar(
        title = title,
        navigationIcon = navigationIcon,
        actions = {
            AppBarRow2(
                overflowIndicator = {
                    IconButton(onClick = it::show) {
                        Icon(ComicIcons.MoreVert, null)
                    }
                },
                content = actions
            )
        },
        windowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = if (navigationSuiteType.isNavigationBar) ComicTheme.colorScheme.surface else ComicTheme.colorScheme.surfaceContainer

        ),
        scrollBehavior = scrollBehavior,
    )
}
