package com.sorrowblue.comicviewer.framework.ui.adaptive.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScope
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.window.core.layout.WindowWidthSizeClass
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
    } else if (
        windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.EXPANDED ||
        windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.MEDIUM
    ) {
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
    NavigationSuiteScaffold(
        navigationSuiteItems = navigationSuiteItems,
        layoutType = navigationState.suiteType,
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
