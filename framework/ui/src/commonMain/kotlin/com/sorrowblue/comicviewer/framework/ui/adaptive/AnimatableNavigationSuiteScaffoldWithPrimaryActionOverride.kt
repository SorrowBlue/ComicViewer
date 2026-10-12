/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.framework.ui.adaptive

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.only
import androidx.compose.material3.DrawerDefaults
import androidx.compose.material3.ExperimentalMaterial3ComponentOverrideApi
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationRailDefaults
import androidx.compose.material3.ShortNavigationBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.WideNavigationRailDefaults
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveComponentOverrideApi
import androidx.compose.material3.adaptive.navigationsuite.LocalNavigationSuiteScaffoldWithPrimaryActionOverride
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuite
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldLayout
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldState
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldValue
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldWithPrimaryActionOverride
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldWithPrimaryActionOverrideScope
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidedValue
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3AdaptiveComponentOverrideApi::class)
val ProvideNavigationSuiteScaffoldWithPrimaryActionOverride: ProvidedValue<*>
    get() = LocalNavigationSuiteScaffoldWithPrimaryActionOverride provides
        AnimatableNavigationSuiteScaffoldWithPrimaryActionOverride

@OptIn(
    ExperimentalMaterial3AdaptiveComponentOverrideApi::class,
    ExperimentalMaterial3ComponentOverrideApi::class,
)
private object AnimatableNavigationSuiteScaffoldWithPrimaryActionOverride :
    NavigationSuiteScaffoldWithPrimaryActionOverride {

    @Composable
    override fun NavigationSuiteScaffoldWithPrimaryActionOverrideScope.NavigationSuiteScaffoldWithPrimaryAction() {
        Surface(modifier = modifier, color = containerColor, contentColor = contentColor) {
            NavigationSuiteScaffoldLayout(
                navigationSuite = {
                    CompositionLocalProvider(ProvidedWideNavigationRail) {
                        NavigationSuite(
                            navigationSuiteType = navigationSuiteType,
                            colors = navigationSuiteColors,
                            primaryActionContent = primaryActionContent,
                            verticalArrangement = navigationItemVerticalArrangement,
                            content = navigationItems,
                        )
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
