package com.sorrowblue.comicviewer.framework.ui.adaptive.navigation

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteColors
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteItemColors
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldLayout
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.State
import androidx.compose.runtime.collection.MutableVector
import androidx.compose.runtime.collection.mutableVectorOf
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.window.core.layout.WindowWidthSizeClass
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComponentColors
import com.sorrowblue.comicviewer.framework.designsystem.theme.LocalComponentColors
import com.sorrowblue.comicviewer.framework.ui.adaptive.nonZero

@Immutable
sealed interface NavigationState {
    val visible: Boolean

    val suiteType: NavigationSuiteType
        get() = if (visible) {
            when (this) {
                is NavigationBar -> NavigationSuiteType.NavigationBar
                is NavigationRail -> NavigationSuiteType.NavigationRail
                is NavigationDrawer -> NavigationSuiteType.NavigationDrawer
            }
        } else {
            NavigationSuiteType.None
        }

    @Immutable
    data class NavigationBar(override val visible: Boolean) : NavigationState

    @Immutable
    data class NavigationRail(override val visible: Boolean) : NavigationState

    @Immutable
    data class NavigationDrawer(override val visible: Boolean) : NavigationState
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

@Composable
fun NavigationState.containerColor() = if (this is NavigationState.NavigationBar) {
    ComicTheme.colorScheme.surface
} else {
    ComicTheme.colorScheme.surfaceContainer
}

@Composable
fun NavigationState.contentColor() = if (this is NavigationState.NavigationBar) {
    ComicTheme.colorScheme.surfaceContainerHighest
} else {
    ComicTheme.colorScheme.surface
}

@Composable
fun CompliantNavigationSuiteScaffold(
    navigationSuiteItems: NavigationSuiteScope.() -> Unit,
    modifier: Modifier = Modifier,
    navigationState: NavigationState =
        NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo2(currentWindowAdaptiveInfo()),
    content: @Composable () -> Unit,
) {
    val componentColors = ComponentColors(
        containerColor = navigationState.containerColor(),
        contentColor = navigationState.contentColor()
    )
    CompositionLocalProvider(
        LocalComponentColors provides componentColors,
        LocalNavigationState provides navigationState
    ) {
        Surface(
            modifier = modifier,
            color = componentColors.containerColor,
            contentColor = componentColors.contentColor
        ) {
            NavigationSuiteScaffoldLayout(
                navigationSuite = {
                    NavigationSuite(
                        layoutType = navigationState.suiteType,
                        content = navigationSuiteItems
                    )
                },
                layoutType = navigationState.suiteType,
                content = {
                    Box(
                        Modifier.consumeWindowInsets(
                            if (navigationState.visible) {
                                when (navigationState) {
                                    is NavigationState.NavigationBar ->
                                        WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom)

                                    is NavigationState.NavigationRail ->
                                        WindowInsets.safeDrawing.only(WindowInsetsSides.Start)

                                    is NavigationState.NavigationDrawer ->
                                        WindowInsets.safeDrawing.only(WindowInsetsSides.Start)
                                }
                            } else {
                                WindowInsets(0)
                            }
                        )
                    ) {
                        content()
                    }
                }
            )
        }
    }
}

@Composable
private fun NavigationSuite(
    modifier: Modifier = Modifier,
    layoutType: NavigationSuiteType =
        NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(currentWindowAdaptiveInfo()),
    colors: NavigationSuiteColors = NavigationSuiteDefaults.compliantColors(),
    content: NavigationSuiteScope.() -> Unit,
) {
    val scope by rememberStateOfItems(content)
    // Define defaultItemColors here since we can't set NavigationSuiteDefaults.itemColors() as a
    // default for the colors param of the NavigationSuiteScope.item non-composable function.
    val defaultItemColors = NavigationSuiteDefaults.itemColors()
    when (layoutType) {
        NavigationSuiteType.NavigationBar -> {
            NavigationBar(
                modifier = modifier,
                containerColor = colors.navigationBarContainerColor,
                contentColor = colors.navigationBarContentColor,
                windowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom)
            ) {
                scope.itemList.forEach {
                    NavigationBarItem(
                        modifier = it.modifier,
                        selected = it.selected,
                        onClick = it.onClick,
                        icon = { NavigationItemIcon(icon = it.icon, badge = it.badge) },
                        enabled = it.enabled,
                        label = it.label,
                        alwaysShowLabel = it.alwaysShowLabel,
                        colors =
                        it.colors?.navigationBarItemColors
                            ?: defaultItemColors.navigationBarItemColors,
                        interactionSource = it.interactionSource
                    )
                }
            }
        }

        NavigationSuiteType.NavigationRail -> {
            NavigationRail(
                modifier = modifier,
                containerColor = colors.navigationRailContainerColor,
                contentColor = colors.navigationRailContentColor,
                windowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Start + WindowInsetsSides.Vertical)
            ) {
                Spacer(Modifier.weight(1f))
                scope.itemList.forEach {
                    NavigationRailItem(
                        modifier = it.modifier,
                        selected = it.selected,
                        onClick = it.onClick,
                        icon = { NavigationItemIcon(icon = it.icon, badge = it.badge) },
                        enabled = it.enabled,
                        label = it.label,
                        alwaysShowLabel = it.alwaysShowLabel,
                        colors =
                        it.colors?.navigationRailItemColors
                            ?: defaultItemColors.navigationRailItemColors,
                        interactionSource = it.interactionSource
                    )
                }
                Spacer(Modifier.weight(1f))
            }
        }

        NavigationSuiteType.NavigationDrawer -> {
            PermanentDrawerSheet(
                modifier = modifier,
                drawerContainerColor = colors.navigationDrawerContainerColor,
                drawerContentColor = colors.navigationDrawerContentColor,
                windowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Start + WindowInsetsSides.Vertical)
                    .nonZero(WindowInsets(top = ComicTheme.dimension.margin))
            ) {
                scope.itemList.forEach {
                    NavigationDrawerItem(
                        modifier = it.modifier,
                        selected = it.selected,
                        onClick = it.onClick,
                        icon = it.icon,
                        badge = it.badge,
                        label = { it.label?.invoke() ?: Text("") },
                        colors =
                        it.colors?.navigationDrawerItemColors
                            ?: defaultItemColors.navigationDrawerItemColors,
                        interactionSource = it.interactionSource
                    )
                }
            }
        }

        NavigationSuiteType.None -> {
            /* Do nothing. */
        }
    }
}

@Composable
private fun NavigationSuiteDefaults.compliantColors() =
    colors(
        navigationBarContainerColor = ComicTheme.colorScheme.surfaceContainer,
        navigationRailContainerColor = ComicTheme.colorScheme.surfaceContainer,
        navigationDrawerContainerColor = ComicTheme.colorScheme.surfaceContainer
    )

@Composable
private fun rememberStateOfItems(
    content: NavigationSuiteScope.() -> Unit,
): State<NavigationSuiteItemProvider> {
    val latestContent = rememberUpdatedState(content)
    return remember { derivedStateOf { NavigationSuiteScopeImpl().apply(latestContent.value) } }
}

@Composable
private fun NavigationItemIcon(
    icon: @Composable () -> Unit,
    badge: (@Composable () -> Unit)? = null,
) {
    if (badge != null) {
        BadgedBox(badge = { badge.invoke() }) { icon() }
    } else {
        icon()
    }
}

private interface NavigationSuiteItemProvider {
    val itemsCount: Int
    val itemList: MutableVector<NavigationSuiteItem>
}

private class NavigationSuiteItem(
    val selected: Boolean,
    val onClick: () -> Unit,
    val icon: @Composable () -> Unit,
    val modifier: Modifier,
    val enabled: Boolean,
    val label: @Composable (() -> Unit)?,
    val alwaysShowLabel: Boolean,
    val badge: (@Composable () -> Unit)?,
    val colors: NavigationSuiteItemColors?,
    val interactionSource: MutableInteractionSource?,
)

sealed interface NavigationSuiteScope {

    fun item(
        selected: Boolean,
        onClick: () -> Unit,
        icon: @Composable () -> Unit,
        modifier: Modifier = Modifier,
        enabled: Boolean = true,
        label: @Composable (() -> Unit)? = null,
        alwaysShowLabel: Boolean = true,
        badge: (@Composable () -> Unit)? = null,
        colors: NavigationSuiteItemColors? = null,
        interactionSource: MutableInteractionSource? = null,
    )
}

private class NavigationSuiteScopeImpl : NavigationSuiteScope, NavigationSuiteItemProvider {

    override fun item(
        selected: Boolean,
        onClick: () -> Unit,
        icon: @Composable () -> Unit,
        modifier: Modifier,
        enabled: Boolean,
        label: @Composable (() -> Unit)?,
        alwaysShowLabel: Boolean,
        badge: (@Composable () -> Unit)?,
        colors: NavigationSuiteItemColors?,
        interactionSource: MutableInteractionSource?,
    ) {
        itemList.add(
            NavigationSuiteItem(
                selected = selected,
                onClick = onClick,
                icon = icon,
                modifier = modifier,
                enabled = enabled,
                label = label,
                alwaysShowLabel = alwaysShowLabel,
                badge = badge,
                colors = colors,
                interactionSource = interactionSource
            )
        )
    }

    override val itemList: MutableVector<NavigationSuiteItem> = mutableVectorOf()

    override val itemsCount: Int
        get() = itemList.size
}
