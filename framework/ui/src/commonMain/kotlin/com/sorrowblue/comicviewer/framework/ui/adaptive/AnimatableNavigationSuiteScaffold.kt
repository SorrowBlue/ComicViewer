/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.framework.ui.adaptive

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
import androidx.compose.material3.WideNavigationRailValue
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuite
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteColors
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldLayout
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldState
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldValue
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.material3.adaptive.navigationsuite.rememberNavigationSuiteScaffoldState
import androidx.compose.material3.rememberWideNavigationRailState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

/**
 * Custom navigation suite scaffold that uses [FixedWideNavigationRail] for wide navigation rail
 * layout types and properly handles window insets during hide/show animations.
 *
 * @param navigationItems the navigation items to be displayed
 * @param modifier the [Modifier] to be applied to the navigation suite scaffold
 * @param navigationSuiteType the current [NavigationSuiteType]
 * @param navigationSuiteColors [NavigationSuiteColors] for the navigation component
 * @param containerColor the color used for the background of the navigation suite scaffold
 * @param contentColor the preferred color for content inside the navigation suite scaffold
 * @param state the [NavigationSuiteScaffoldState] of this navigation suite scaffold
 * @param navigationItemVerticalArrangement the vertical arrangement of items inside vertical navigation components
 * @param primaryActionContent optional primary action content such as a floating action button
 * @param primaryActionContentHorizontalAlignment horizontal alignment of the primary action content
 * @param content the content of the screen
 */
@Composable
fun AnimatableNavigationSuiteScaffold(
    navigationItems: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    navigationSuiteType: NavigationSuiteType = currentNavigationSuiteType(),
    navigationSuiteColors: NavigationSuiteColors = NavigationSuiteDefaults.colors(),
    containerColor: Color = NavigationSuiteScaffoldDefaults.containerColor,
    contentColor: Color = NavigationSuiteScaffoldDefaults.contentColor,
    state: NavigationSuiteScaffoldState = rememberNavigationSuiteScaffoldState(),
    navigationItemVerticalArrangement: Arrangement.Vertical =
        NavigationSuiteDefaults.verticalArrangement,
    primaryActionContent: @Composable (() -> Unit) = {},
    primaryActionContentHorizontalAlignment: Alignment.Horizontal =
        NavigationSuiteScaffoldDefaults.primaryActionContentAlignment,
    content: @Composable () -> Unit,
) {
    Surface(modifier = modifier, color = containerColor, contentColor = contentColor) {
        NavigationSuiteScaffoldLayout(
            navigationSuite = {
                val movableNavigationItems = remember(navigationItems) {
                    movableContentOf(navigationItems)
                }
                when (navigationSuiteType) {
                    NavigationSuiteType.WideNavigationRailCollapsed -> {
                        FixedWideNavigationRail(
                            header = primaryActionContent,
                            arrangement = navigationItemVerticalArrangement,
                            colors = navigationSuiteColors.wideNavigationRailColors,
                            content = movableNavigationItems,
                        )
                    }

                    NavigationSuiteType.WideNavigationRailExpanded -> {
                        FixedWideNavigationRail(
                            header = primaryActionContent,
                            state = rememberWideNavigationRailState(
                                initialValue = WideNavigationRailValue.Expanded,
                            ),
                            arrangement = navigationItemVerticalArrangement,
                            colors = navigationSuiteColors.wideNavigationRailColors,
                            content = movableNavigationItems,
                        )
                    }

                    else -> {
                        NavigationSuite(
                            navigationSuiteType = navigationSuiteType,
                            colors = navigationSuiteColors,
                            primaryActionContent = primaryActionContent,
                            verticalArrangement = navigationItemVerticalArrangement,
                            content = movableNavigationItems,
                        )
                    }
                }
            },
            navigationSuiteType = navigationSuiteType,
            state = state,
            primaryActionContent = primaryActionContent,
            primaryActionContentHorizontalAlignment = primaryActionContentHorizontalAlignment,
            content = {
                Box(
                    Modifier.navigationSuiteScaffoldConsumeWindowInsets(
                        navigationSuiteType,
                        state,
                    ),
                ) {
                    content()
                }
            },
        )
    }
}

@Composable
private fun Modifier.navigationSuiteScaffoldConsumeWindowInsets(
    navigationSuiteType: NavigationSuiteType,
    state: NavigationSuiteScaffoldState,
): Modifier = consumeWindowInsets(
    if (state.currentValue == NavigationSuiteScaffoldValue.Hidden &&
        (state.targetValue == NavigationSuiteScaffoldValue.Hidden || !state.isAnimating)
    ) {
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
    },
)

private val NoWindowInsets = WindowInsets(0, 0, 0, 0)
