package com.sorrowblue.comicviewer.framework.ui.layout

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.WideNavigationRailDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteColors
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.material3.animateFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.canonical.isNavigationBar
import com.sorrowblue.comicviewer.framework.ui.canonical.isNavigationRail

private var navigationSuiteColorsCache: NavigationSuiteColors? = null
private val navigationSuiteColors
    @Composable get() = navigationSuiteColorsCache ?: NavigationSuiteDefaults.colors(
        wideNavigationRailColors = WideNavigationRailDefaults.colors(
            containerColor = ComicTheme.colorScheme.surfaceContainer
        )
    ).also {
        navigationSuiteColorsCache = it
    }


@Composable
fun AdaptiveNavigationSuiteScaffold(
    state: AdaptiveNavigationSuiteScaffoldState,
    navigationItems: @Composable (() -> Unit),
    content: @Composable () -> Unit,
) {
    val containerColor by animateColorAsState(
        if (state.navigationSuiteType.isNavigationRail) ComicTheme.colorScheme.surfaceContainer else ComicTheme.colorScheme.surface
    )
    NavigationSuiteScaffold(
        navigationSuiteColors = navigationSuiteColors,
        containerColor = containerColor,
        state = state.navigationSuiteScaffoldState,
        navigationSuiteType = state.navigationSuiteType,
        navigationItems = navigationItems,
        primaryActionContent = {
            Column {
                if (state.navigationSuiteType.isNavigationRail) {
                    IconButton(
                        onClick = state::toggleNavigationRail,
                        modifier = Modifier.padding(start = 24.dp)
                    ) {
                        Icon(
                            if (state.navigationSuiteType == NavigationSuiteType.WideNavigationRailCollapsed) {
                                ComicIcons.Menu
                            } else {
                                ComicIcons.MenuOpen
                            },
                            null
                        )
                    }
                    Spacer(Modifier.size(8.dp))
                }
                ExtendedFloatingActionButton(
                    modifier = if (state.navigationSuiteType.isNavigationBar) {
                        Modifier
                            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.End))
                            .animateFloatingActionButton(
                                visible = state.floatingActionButtonState.targetValue.isVisible,
                                alignment = Alignment.BottomEnd
                            )
                    } else {
                        Modifier
                            .padding(start = 20.dp)
                            .animateFloatingActionButton(
                                visible = state.floatingActionButtonState.targetValue.isVisible,
                                alignment = Alignment.Center
                            )
                    },
                    expanded = state.navigationSuiteType == NavigationSuiteType.WideNavigationRailExpanded,
                    elevation = if (state.navigationSuiteType.isNavigationBar) FloatingActionButtonDefaults.elevation() else FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                    onClick = {
                    },
                    text = { Text(text = "Add") },
                    icon = {
                        Icon(
                            imageVector = ComicIcons.Add,
                            contentDescription = null
                        )
                    },
                )
            }
        }
    ) {
        content()
    }
}
