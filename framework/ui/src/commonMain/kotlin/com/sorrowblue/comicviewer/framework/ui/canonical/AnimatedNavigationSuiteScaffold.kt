package com.sorrowblue.comicviewer.framework.ui.canonical

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationItemColors
import androidx.compose.material3.NavigationItemIconPosition
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ShortNavigationBarItem
import androidx.compose.material3.ShortNavigationBarItemDefaults
import androidx.compose.material3.WideNavigationRail
import androidx.compose.material3.WideNavigationRailItem
import androidx.compose.material3.WideNavigationRailItemDefaults
import androidx.compose.material3.WideNavigationRailState
import androidx.compose.material3.WideNavigationRailValue
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteColors
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteItemColors
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldValue
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collection.MutableVector
import androidx.compose.runtime.collection.mutableVectorOf
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.movableContentWithReceiverOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.designsystem.theme.LocalContainerColor
import com.sorrowblue.comicviewer.framework.ui.NavigationSuiteScaffold2State
import com.sorrowblue.comicviewer.framework.ui.PrimaryActionContentMode
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AnimatedNavigationSuiteScaffold(
    state: NavigationSuiteScaffold2State<*>,
    layoutType: NavigationSuiteType,
    navigationRailState: WideNavigationRailState,
    navigationSuiteItems: NavigationSuiteScope.() -> Unit,
    navigationRail: @Composable (Boolean, @Composable () -> Unit) -> Unit,
    navigationBar: @Composable (Boolean, @Composable () -> Unit) -> Unit,
    modifier: Modifier = Modifier,
    primaryActionContent: @Composable () -> Unit = {},
    primaryActionContentMode: PrimaryActionContentMode,
    colors: NavigationSuiteColors = NavigationSuiteDefaults.colors(),
    content: @Composable (PaddingValues) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val suiteItemProvider by rememberStateOfItems(navigationSuiteItems)
    val movableContent = remember(navigationSuiteItems) {
        movableContentOf {
            suiteItemProvider.itemList.forEach {
                NavigationSuiteItem(
                    navigationRailState = navigationRailState,
                    navigationSuiteType = layoutType,
                    modifier = it.modifier,
                    selected = it.selected,
                    onClick = it.onClick,
                    icon = it.icon,
                    badge = it.badge,
                    enabled = it.enabled,
                    label = it.label,
                    navigationItemColors = null,
                    interactionSource = it.interactionSource
                )
            }
        }
    }
    // TODO(ShortNavigationBar has an animation bug in SharedElementTransition)
    val movableNavigationBarContent = remember(navigationSuiteItems) {
        movableContentWithReceiverOf<RowScope> {
            suiteItemProvider.itemList.forEach {
                NavigationSuiteItem(
                    navigationSuiteType = layoutType,
                    modifier = it.modifier,
                    selected = it.selected,
                    onClick = it.onClick,
                    icon = it.icon,
                    badge = it.badge,
                    enabled = it.enabled,
                    label = it.label,
                    interactionSource = it.interactionSource
                )
            }
        }
    }
    Scaffold(
        containerColor = LocalContainerColor.current,
        bottomBar = {
            val navigationBarContent = movableContentOf {
                // TODO(ShortNavigationBar has an animation bug in SharedElementTransition)
                NavigationBar(
                    containerColor = colors.shortNavigationBarContainerColor,
                    contentColor = colors.shortNavigationBarContentColor,
                    content = movableNavigationBarContent
                )
            }
            navigationBar(
                layoutType.isNavigationBar && state.suiteScaffoldState.targetValue == NavigationSuiteScaffoldValue.Visible,
                navigationBarContent
            )
        },
        contentWindowInsets = WindowInsets.safeDrawing,
        floatingActionButton = {
            if (primaryActionContentMode == PrimaryActionContentMode.Auto && layoutType.isNavigationBar || primaryActionContentMode == PrimaryActionContentMode.NavigationBarOnly) {
                primaryActionContent()
            }
        },
        modifier = modifier
    ) { contentPadding ->
        Row {
            val navigationRailContent = movableContentOf {
                WideNavigationRail(
                    header = {
                        Column {
                            IconButton(
                                onClick = {
                                    scope.launch {
                                        state.navigationRailState.toggle()
                                    }
                                },
                                modifier = Modifier.padding(start = 24.dp)
                            ) {
                                if (state.navigationRailState.targetValue == WideNavigationRailValue.Expanded) {
                                    Icon(ComicIcons.MenuOpen, null)
                                } else {
                                    Icon(ComicIcons.Menu, null)
                                }
                            }
                            if (primaryActionContentMode == PrimaryActionContentMode.Auto) {
                                Spacer(Modifier.size(ComicTheme.dimension.padding))
                                primaryActionContent()
                            }
                        }
                    },
                    state = state.navigationRailState,
                    colors = colors.wideNavigationRailColors,
                    content = movableContent
                )
            }
            navigationRail(
                layoutType.isNavigationRail && state.suiteScaffoldState.targetValue == NavigationSuiteScaffoldValue.Visible,
                navigationRailContent
            )
            Box(
                modifier = if (layoutType.isNavigationRail && state.suiteScaffoldState.targetValue == NavigationSuiteScaffoldValue.Visible) Modifier.consumeWindowInsets(
                    WindowInsets.safeDrawing.only(WindowInsetsSides.Start)
                ) else Modifier.zIndex(2f)
            ) {
                content(contentPadding)
            }
        }
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

    override val itemList: MutableVector<NavigationSuiteItem> =
        mutableVectorOf()

    override val itemsCount: Int
        get() = itemList.size
}

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

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun NavigationSuiteItem(
    navigationSuiteType: NavigationSuiteType,
    navigationRailState: WideNavigationRailState,
    selected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    label: @Composable (() -> Unit)?,
    modifier: Modifier,
    enabled: Boolean,
    badge: @Composable (() -> Unit)?,
    navigationItemColors: NavigationItemColors?,
    interactionSource: MutableInteractionSource?,
) {
    when (navigationSuiteType) {
        NavigationSuiteType.ShortNavigationBarCompact,
        NavigationSuiteType.ShortNavigationBarMedium,
        -> {
            // TODO(ShortNavigationBar has an animation bug in SharedElementTransition)
            val iconPosition =
                if (navigationSuiteType == NavigationSuiteType.ShortNavigationBarCompact) {
                    NavigationItemIconPosition.Top
                } else {
                    NavigationItemIconPosition.Start
                }
            ShortNavigationBarItem(
                selected = selected,
                onClick = onClick,
                icon = { NavigationItemIcon(icon = icon, badge = badge) },
                label = label,
                modifier = modifier,
                enabled = enabled,
                iconPosition = iconPosition,
                colors = navigationItemColors ?: ShortNavigationBarItemDefaults.colors(),
                interactionSource = interactionSource
            )
        }

        NavigationSuiteType.WideNavigationRailCollapsed,
        NavigationSuiteType.WideNavigationRailExpanded,
        -> {
            WideNavigationRailItem(
                selected = selected,
                onClick = onClick,
                icon = { NavigationItemIcon(icon = icon, badge = badge) },
                label = label,
                modifier = modifier,
                enabled = enabled,
                railExpanded = navigationRailState.targetValue == WideNavigationRailValue.Expanded,
                colors = navigationItemColors ?: WideNavigationRailItemDefaults.colors(),
                interactionSource = interactionSource
            )
        }
    }
}

// TODO(ShortNavigationBar has an animation bug in SharedElementTransition)
@Composable
private fun RowScope.NavigationSuiteItem(
    navigationSuiteType: NavigationSuiteType,
    selected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    label: @Composable (() -> Unit)?,
    modifier: Modifier,
    enabled: Boolean,
    badge: @Composable (() -> Unit)?,
    interactionSource: MutableInteractionSource?,
) {
    when (navigationSuiteType) {
        NavigationSuiteType.ShortNavigationBarCompact,
        NavigationSuiteType.ShortNavigationBarMedium,
        -> {
            NavigationBarItem(
                selected = selected,
                onClick = onClick,
                icon = { NavigationItemIcon(icon = icon, badge = badge) },
                label = label,
                modifier = modifier,
                enabled = enabled,
                colors = NavigationBarItemDefaults.colors(),
                interactionSource = interactionSource
            )
        }
    }
}
