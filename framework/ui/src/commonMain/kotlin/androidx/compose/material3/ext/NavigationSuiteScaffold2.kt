@file:Suppress("INVISIBLE_REFERENCE", "detekt.all")

package androidx.compose.material3.ext

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.NavigationItemColors
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.WideNavigationRailItem
import androidx.compose.material3.WideNavigationRailItemDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteItemColors
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.material3.adaptive.navigationsuite.WindowAdaptiveInfoDefault
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RowScope.NavigationSuiteItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    label: @Composable (() -> Unit)?,
    modifier: Modifier = Modifier,
    navigationSuiteType: NavigationSuiteType =
        NavigationSuiteScaffoldDefaults.navigationSuiteType(WindowAdaptiveInfoDefault),
    enabled: Boolean = true,
    badge: @Composable (() -> Unit)? = null,
    colors: NavigationItemColors? = null,
    interactionSource: MutableInteractionSource? = null,
) {
    NavigationSuiteItem(
        isNavigationSuite = false,
        navigationSuiteType = navigationSuiteType,
        selected = selected,
        onClick = onClick,
        icon = icon,
        label = label,
        modifier = modifier,
        enabled = enabled,
        badge = badge,
        navigationItemColors = colors,
        navigationSuiteItemColors = null,
        interactionSource = interactionSource,
    )
}

@Composable
private fun RowScope.NavigationSuiteItem(
    isNavigationSuite: Boolean,
    navigationSuiteType: NavigationSuiteType,
    selected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    label: @Composable (() -> Unit)?,
    modifier: Modifier,
    enabled: Boolean,
    badge: @Composable (() -> Unit)?,
    navigationItemColors: NavigationItemColors?,
    navigationSuiteItemColors: NavigationSuiteItemColors?,
    interactionSource: MutableInteractionSource?,
) {
    when (navigationSuiteType) {
        NavigationSuiteType.ShortNavigationBarCompact,
        NavigationSuiteType.ShortNavigationBarMedium,
        -> {
            val defaultColors =
                navigationSuiteItemColors?.navigationBarItemColors
                    ?: NavigationBarItemDefaults.colors()
            NavigationBarItem(
                selected = selected,
                onClick = onClick,
                icon = { NavigationItemIcon(icon = icon, badge = badge) },
                label = label,
                modifier = modifier.padding(top = 8.dp),
                enabled = enabled,
                colors = defaultColors,
                interactionSource = interactionSource,
            )
        }

        NavigationSuiteType.WideNavigationRailCollapsed,
        NavigationSuiteType.WideNavigationRailExpanded,
        -> {
            WideNavigationRailItem(
                railExpanded =
                    navigationSuiteType == NavigationSuiteType.WideNavigationRailExpanded,
                selected = selected,
                onClick = onClick,
                icon = { NavigationItemIcon(icon = icon, badge = badge) },
                label = label,
                modifier = modifier,
                enabled = enabled,
                colors = navigationItemColors ?: WideNavigationRailItemDefaults.colors(),
                interactionSource = interactionSource,
            )
        }
        // Note: This function does not support providing a NavigationBarItem for the
        // NavigationSuiteType.NavigationBar type due to the NavigationBarItem being limited to
        // RowScope. Instead we provide ShortNavigationBarItem with a top padding so that it is
        // visually the same.
        // It's advised to to use NavigationSuiteType.ShortNavigationBarVerticalItems instead.
        NavigationSuiteType.NavigationBar -> {
            val defaultColors =
                navigationSuiteItemColors?.navigationBarItemColors
                    ?: NavigationBarItemDefaults.colors()
            NavigationBarItem(
                selected = selected,
                onClick = onClick,
                icon = { NavigationItemIcon(icon = icon, badge = badge) },
                label = label,
                modifier = modifier.padding(top = 8.dp),
                enabled = enabled,
                colors = defaultColors,
                interactionSource = interactionSource,
            )
        }
        // It's advised to to use NavigationSuiteType.WideNavigationRail instead of
        // NavigationSuiteType.NavigationRail.
        NavigationSuiteType.NavigationRail -> {
            val actualColors =
                if (isNavigationSuite) {
                    navigationSuiteItemColors?.navigationRailItemColors
                        ?: NavigationRailItemDefaults.colors()
                } else {
                    if (navigationItemColors != null) {
                        NavigationRailItemDefaults.colors(
                            selectedIconColor = navigationItemColors.selectedIconColor,
                            selectedTextColor = navigationItemColors.selectedTextColor,
                            indicatorColor = navigationItemColors.selectedIndicatorColor,
                            unselectedIconColor = navigationItemColors.unselectedIconColor,
                            unselectedTextColor = navigationItemColors.unselectedTextColor,
                            disabledIconColor = navigationItemColors.disabledIconColor,
                            disabledTextColor = navigationItemColors.disabledTextColor,
                        )
                    } else {
                        NavigationSuiteDefaults.itemColors().navigationRailItemColors
                    }
                }
            NavigationRailItem(
                selected = selected,
                onClick = onClick,
                icon = { NavigationItemIcon(icon = icon, badge = badge) },
                label = label,
                modifier = modifier,
                enabled = enabled,
                colors = actualColors,
                interactionSource = interactionSource,
            )
        }
        // It's advised to to use NavigationSuiteType.WideNavigationRail instead of
        // NavigationSuiteType.NavigationDrawer.
        NavigationSuiteType.NavigationDrawer -> {
            val actualColors =
                if (isNavigationSuite) {
                    navigationSuiteItemColors?.navigationDrawerItemColors
                        ?: NavigationDrawerItemDefaults.colors()
                } else {
                    if (navigationItemColors != null) {
                        NavigationDrawerItemDefaults.colors(
                            selectedIconColor = navigationItemColors.selectedIconColor,
                            selectedTextColor = navigationItemColors.selectedTextColor,
                            unselectedIconColor = navigationItemColors.unselectedIconColor,
                            unselectedTextColor = navigationItemColors.unselectedTextColor,
                            selectedContainerColor = navigationItemColors.selectedIndicatorColor,
                        )
                    } else {
                        NavigationSuiteDefaults.itemColors().navigationDrawerItemColors
                    }
                }

            NavigationDrawerItem(
                modifier = modifier,
                selected = selected,
                onClick = onClick,
                icon = icon,
                badge = badge,
                label = { label?.invoke() ?: Text("") },
                colors = actualColors,
                interactionSource = interactionSource,
            )
        }
    }
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
