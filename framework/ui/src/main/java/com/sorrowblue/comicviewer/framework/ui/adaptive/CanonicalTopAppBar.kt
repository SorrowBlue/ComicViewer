package com.sorrowblue.comicviewer.framework.ui.adaptive

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.sorrowblue.comicviewer.framework.designsystem.theme.LocalComponentColors
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.LocalNavigationState
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.NavigationState

@Composable
fun CanonicalTopAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable (RowScope.() -> Unit) = {},
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    Column(modifier = modifier) {
        TopAppBar(
            title = title,
            navigationIcon = navigationIcon,
            actions = actions,
            colors = with(LocalComponentColors.current) {
                if (LocalNavigationState.current is NavigationState.NavigationBar) {
                    TopAppBarDefaults.topAppBarColors()
                } else {
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = containerColor,
                        scrolledContainerColor = contentColor
                    )
                }
            },
            scrollBehavior = scrollBehavior,
            windowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top),
        )
        if (scrollBehavior != null && LocalNavigationState.current !is NavigationState.NavigationBar) {
            val overlappedFraction by remember(scrollBehavior.state) {
                derivedStateOf { scrollBehavior.state.overlappedFraction > 0 || scrollBehavior.state.collapsedFraction > 0 }
            }
            if (overlappedFraction) {
                HorizontalDivider()
            }
        }
    }
}
