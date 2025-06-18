package com.sorrowblue.comicviewer.framework.ui.adaptive.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Surface
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuite
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteColors
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldLayout
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScope
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.designsystem.theme.LocalContainerColor

@Immutable
sealed interface NavigationState {
    val visible: Boolean

    val suiteType: NavigationSuiteType
        get() = if (visible) {
            when (this) {
                is NavigationBar -> NavigationSuiteType.NavigationBar
                is NavigationRail -> NavigationSuiteType.NavigationRail
            }
        } else {
            NavigationSuiteType.None
        }

    @Immutable
    data class NavigationBar(override val visible: Boolean) : NavigationState

    @Immutable
    data class NavigationRail(override val visible: Boolean) : NavigationState
}

val LocalNavigationState =
    compositionLocalOf<NavigationState> { NavigationState.NavigationBar(false) }

/**
 * Calculate from adaptive info
 *
 * @param adaptiveInfo
 * @param defaultVisible
 * @return [NavigationState]
 * @see [NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo]
 */
@Suppress("UnusedReceiverParameter")
fun NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo2(
    adaptiveInfo: WindowAdaptiveInfo,
    defaultVisible: Boolean = true,
): NavigationState = with(adaptiveInfo) {
    if (windowPosture.isTabletop) {
        NavigationState.NavigationBar(defaultVisible)
    } else if (windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)) {
        NavigationState.NavigationRail(defaultVisible)
    } else {
        NavigationState.NavigationBar(defaultVisible)
    }
}

/**
 * @see [androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold]
 */
@Composable
fun CompliantNavigationSuiteScaffold(
    navigationSuiteItems: NavigationSuiteScope.() -> Unit,
    modifier: Modifier = Modifier,
    navigationState: NavigationState =
        NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo2(currentWindowAdaptiveInfo()),
    content: @Composable () -> Unit,
) {
    val navigationSuiteColors = NavigationSuiteDefaults.colors()
    NavigationSuiteScaffold2(
        navigationSuiteItems = navigationSuiteItems,
        navigationState = navigationState,
        navigationSuiteColors = navigationSuiteColors,
        containerColor = ComicTheme.colorScheme.surface,
        contentColor = ComicTheme.colorScheme.onSurface,
        modifier = modifier
            .background(
                when (navigationState) {
                    is NavigationState.NavigationBar -> navigationSuiteColors.navigationBarContainerColor
                    is NavigationState.NavigationRail -> navigationSuiteColors.navigationRailContainerColor
                }
            )
            .windowInsetsPadding(
                if (navigationState.visible) {
                    when (navigationState) {
                        is NavigationState.NavigationBar ->
                            WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom)

                        is NavigationState.NavigationRail ->
                            WindowInsets.safeDrawing.only(WindowInsetsSides.Start)
                    }
                } else {
                    WindowInsets(0)
                }
            ),
        content = {
            CompositionLocalProvider(
                LocalContainerColor provides ComicTheme.colorScheme.surface,
                LocalNavigationState provides navigationState
            ) {
                content()
            }
        }
    )
}

@Composable
fun NavigationSuiteScaffold2(
    navigationSuiteItems: NavigationSuiteScope.() -> Unit,
    navigationState: NavigationState,
    modifier: Modifier = Modifier,
    navigationSuiteColors: NavigationSuiteColors = NavigationSuiteDefaults.colors(),
    containerColor: Color = NavigationSuiteScaffoldDefaults.containerColor,
    contentColor: Color = NavigationSuiteScaffoldDefaults.contentColor,
    content: @Composable () -> Unit = {},
) {
    Surface(modifier = modifier, color = containerColor, contentColor = contentColor) {
        NavigationSuiteScaffoldLayout(
            navigationSuite = {
                NavigationSuite(
                    layoutType = navigationState.suiteType,
                    colors = navigationSuiteColors,
                    content = navigationSuiteItems,
                    modifier = Modifier.windowInsetsPadding(
                        if (navigationState.visible) {
                            when (navigationState) {
                                is NavigationState.NavigationBar ->
                                    WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom)

                                is NavigationState.NavigationRail ->
                                    WindowInsets.safeDrawing.only(WindowInsetsSides.Start).add(
                                        WindowInsets(top = 16.dp)
                                    )
                            }
                        } else {
                            WindowInsets(0)
                        }
                    )

                )
            },
            layoutType = navigationState.suiteType,
            content = {
                Box(
                    Modifier.consumeWindowInsets(
                        when (navigationState.suiteType) {
                            NavigationSuiteType.NavigationBar ->
                                WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom)

                            NavigationSuiteType.NavigationRail ->
                                WindowInsets.safeDrawing.only(WindowInsetsSides.Start)

                            NavigationSuiteType.NavigationDrawer ->
                                WindowInsets.safeDrawing.only(WindowInsetsSides.Start)

                            else -> WindowInsets(0)
                        }
                    )
                ) {
                    content()
                }
            }
        )
    }
}
