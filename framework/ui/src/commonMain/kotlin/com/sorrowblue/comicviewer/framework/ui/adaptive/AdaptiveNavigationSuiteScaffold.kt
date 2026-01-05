package com.sorrowblue.comicviewer.framework.ui.adaptive

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.WideNavigationRailDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteItem
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.LocalSharedTransitionScope
import com.sorrowblue.comicviewer.framework.ui.navigation.LocalNavigator

@Composable
fun AdaptiveNavigationSuiteScaffoldState.AdaptiveNavigationSuiteScaffold(
    modifier: Modifier = Modifier,
    navigationItems: @Composable () -> Unit = {
        val navigator = LocalNavigator.current
        navigator.topLevelRoutes.forEach { key ->
            val isSelected = key == navigator.topLevelKey
            NavigationSuiteItem(
                selected = isSelected,
                label = { Text(key.title) },
                icon = { Icon(key.icon, null) },
                onClick = {
                    if (key == navigator.topLevelKey) {
                        onNavigationSelected()
                    } else {
                        navigator.navigate(key)
                    }
                },
                modifier = Modifier.testTag("NavigationSuiteItem"),
            )
        }
    },
    primaryActionContent: @Composable () -> Unit = {},
    primaryActionContentHorizontalAlignment: Alignment.Horizontal = NavigationSuiteScaffoldDefaults.primaryActionContentAlignment,
    content: @Composable () -> Unit,
) {
    AnimatedNavigationSuiteScaffold(
        visibilityScope = LocalNavAnimatedContentScope.current,
        transitionScope = LocalSharedTransitionScope.current,
        navigationItems = navigationItems,
        modifier = modifier,
        navigationSuiteType = this.navigationSuiteType,
        navigationSuiteColors = NavigationSuiteDefaults.colors(
            shortNavigationBarContainerColor = ComicTheme.colorScheme.surfaceContainer,
            wideNavigationRailColors = WideNavigationRailDefaults.colors(
                containerColor = ComicTheme.colorScheme.surfaceContainer,
            ),
        ),
        containerColor = ComicTheme.colorScheme.surface,
        contentColor = ComicTheme.colorScheme.onSurface,
        state = this,
        navigationItemVerticalArrangement = Arrangement.Top,
        primaryActionContent = primaryActionContent,
        primaryActionContentHorizontalAlignment = primaryActionContentHorizontalAlignment,
        content = content,
    )
}
