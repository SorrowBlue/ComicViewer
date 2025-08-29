package com.sorrowblue.comicviewer.framework.ui.canonical

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.only
import androidx.compose.material3.DrawerDefaults
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationRailDefaults
import androidx.compose.material3.ShortNavigationBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.WideNavigationRailDefaults
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuite
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteColors
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldLayout
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldState
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldValue
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.material3.adaptive.navigationsuite.rememberNavigationSuiteScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun AnimatedNavigationSuiteScaffold(
    visibilityScope: AnimatedVisibilityScope,
    transitionScope: SharedTransitionScope,
    navigationItems: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    navigationSuiteType: NavigationSuiteType =
        NavigationSuiteScaffoldDefaults.navigationSuiteType(currentWindowAdaptiveInfo()),
    navigationSuiteColors: NavigationSuiteColors = NavigationSuiteDefaults.colors(),
    containerColor: Color = NavigationSuiteScaffoldDefaults.containerColor,
    contentColor: Color = NavigationSuiteScaffoldDefaults.contentColor,
    state: NavigationSuiteScaffoldState = rememberNavigationSuiteScaffoldState(),
    navigationItemVerticalArrangement: Arrangement.Vertical =
        NavigationSuiteDefaults.verticalArrangement,
    primaryActionContent: @Composable () -> Unit = {},
    primaryActionContentHorizontalAlignment: Alignment.Horizontal =
        NavigationSuiteScaffoldDefaults.primaryActionContentAlignment,
    content: @Composable () -> Unit,
) {
    Surface(modifier = modifier, color = containerColor, contentColor = contentColor) {
        NavigationSuiteScaffoldLayout(
            navigationSuite = {
                NavigationSuite(
                    navigationSuiteType = navigationSuiteType,
                    colors = navigationSuiteColors,
                    primaryActionContent = primaryActionContent,
                    verticalArrangement = navigationItemVerticalArrangement,
                    content = navigationItems,
                    modifier = Modifier.navigationSuiteScaffoldSharedElement(
                        navigationSuiteType,
                        visibilityScope,
                        transitionScope
                    )
                )
            },
            navigationSuiteType = navigationSuiteType,
            state = state,
            primaryActionContent = primaryActionContent,
            primaryActionContentHorizontalAlignment = primaryActionContentHorizontalAlignment,
            content = {
                Box(
                    Modifier.navigationSuiteScaffoldConsumeWindowInsets(navigationSuiteType, state)
                ) {
                    content()
                }
            },
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun Modifier.navigationSuiteScaffoldSharedElement(
    navigationSuiteType: NavigationSuiteType,
    visibilityScope: AnimatedVisibilityScope,
    transitionScope: SharedTransitionScope,
): Modifier =
    with(visibilityScope) {
        with(transitionScope) {
            if (navigationSuiteType.isNavigationBar) {
                Modifier
                    .animateEnterExit(
                        enter = NavigationBarTransitionEnter,
                        exit = NavigationBarTransitionExit
                    )
                    .sharedElement(
                        animatedVisibilityScope = visibilityScope,
                        sharedContentState = rememberSharedContentState(
                            NavigationBarSharedElementKey
                        )
                    )
            } else if (navigationSuiteType.isNavigationRail) {
                Modifier
                    .animateEnterExit(
                        enter = NavigationRailTransitionEnter,
                        exit = NavigationRailTransitionExit
                    )
                    .sharedElement(
                        animatedVisibilityScope = visibilityScope,
                        sharedContentState = rememberSharedContentState(
                            NavigationRailSharedElementKey
                        )
                    )
            } else {
                Modifier
            }
        }
    }

@Composable
private fun Modifier.navigationSuiteScaffoldConsumeWindowInsets(
    navigationSuiteType: NavigationSuiteType,
    state: NavigationSuiteScaffoldState,
): Modifier =
    consumeWindowInsets(
        if (state.currentValue == NavigationSuiteScaffoldValue.Hidden && !state.isAnimating) {
            NoWindowInsets
        } else {
            when (navigationSuiteType) {
                NavigationSuiteType.ShortNavigationBarCompact,
                NavigationSuiteType.ShortNavigationBarMedium,
                ->
                    ShortNavigationBarDefaults.windowInsets.only(WindowInsetsSides.Bottom)

                NavigationSuiteType.WideNavigationRailCollapsed,
                NavigationSuiteType.WideNavigationRailExpanded,
                ->
                    WideNavigationRailDefaults.windowInsets.only(WindowInsetsSides.Start)

                NavigationSuiteType.NavigationBar ->
                    NavigationBarDefaults.windowInsets.only(WindowInsetsSides.Bottom)

                NavigationSuiteType.NavigationRail ->
                    NavigationRailDefaults.windowInsets.only(WindowInsetsSides.Start)

                NavigationSuiteType.NavigationDrawer ->
                    DrawerDefaults.windowInsets.only(WindowInsetsSides.Start)

                else -> NoWindowInsets
            }
        }
    )

private val NoWindowInsets = WindowInsets(0, 0, 0, 0)
