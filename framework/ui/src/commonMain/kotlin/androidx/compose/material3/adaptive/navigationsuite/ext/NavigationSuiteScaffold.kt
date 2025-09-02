package androidx.compose.material3.adaptive.navigationsuite.ext

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.ShortNavigationBar
import androidx.compose.material3.WideNavigationRail
import androidx.compose.material3.WideNavigationRailValue
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteColors
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.material3.rememberWideNavigationRailState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * The default Material navigation component according to the current
 * [NavigationSuiteType] to be used with the [NavigationSuiteScaffold].
 *
 * For specifics about each navigation component, see [ShortNavigationBar],
 * [WideNavigationRail], [NavigationRail], and [PermanentDrawerSheet].
 *
 * Changed from ShortNavigationBar to NavigationBar.
 *
 * @param navigationSuiteType the [NavigationSuiteType] of the associated
 *    [NavigationSuiteScaffold]. Usually
 *    [NavigationSuiteScaffoldDefaults.navigationSuiteType]
 * @param modifier the [Modifier] to be applied to the navigation component
 * @param colors [NavigationSuiteColors] that will be used to determine the
 *    container (background) color of the navigation component and the
 *    preferred color for content inside the navigation component
 * @param verticalArrangement the vertical arrangement of the items inside
 *    vertical navigation components, such as the wide navigation rail.
 *    It's recommended to use [Arrangement.Top], [Arrangement.Center], or
 *    [Arrangement.Bottom].
 * @param primaryActionContent The optional primary action content of the
 *    navigation suite scaffold, if any. Typically a
 *    [androidx.compose.material3.FloatingActionButton]. It'll be displayed
 *    inside vertical navigation components as their header, and above
 *    horizontal navigation components.
 * @param content the content inside the current navigation component,
 *    typically [NavigationSuiteItem]s
 * @see androidx.compose.material3.adaptive.navigationsuite.NavigationSuite
 */
@Composable
fun NavigationSuite(
    navigationSuiteType: NavigationSuiteType,
    modifier: Modifier = Modifier,
    colors: NavigationSuiteColors = NavigationSuiteDefaults.colors(),
    verticalArrangement: Arrangement.Vertical = NavigationSuiteDefaults.verticalArrangement,
    primaryActionContent: @Composable (() -> Unit) = {},
    content: @Composable RowScope.() -> Unit,
) {
    val movableContent = remember(content) { movableContentOf(content) }
    val movablePrimaryActionContent = remember(primaryActionContent) { movableContentOf(primaryActionContent) }
    when (navigationSuiteType) {
        NavigationSuiteType.Companion.ShortNavigationBarCompact -> {
            NavigationBar(
                modifier = modifier.heightIn(min = 80.dp),
                containerColor = colors.navigationBarContainerColor,
                contentColor = colors.navigationBarContentColor,
                content = { Row { movableContent(this) } },
            )
        }

        NavigationSuiteType.Companion.ShortNavigationBarMedium -> {
            NavigationBar(
                modifier = modifier.heightIn(min = 80.dp),
                containerColor = colors.navigationBarContainerColor,
                contentColor = colors.navigationBarContentColor,
                content = { Row { movableContent(this) } },
            )
        }

        NavigationSuiteType.Companion.WideNavigationRailCollapsed -> {
            WideNavigationRail(
                modifier = modifier,
                header = movablePrimaryActionContent,
                arrangement = verticalArrangement,
                colors = colors.wideNavigationRailColors,
                content = { Row { movableContent(this) } },
            )
        }

        NavigationSuiteType.Companion.WideNavigationRailExpanded -> {
            WideNavigationRail(
                modifier = modifier,
                header = movablePrimaryActionContent,
                state =
                rememberWideNavigationRailState(
                    initialValue = WideNavigationRailValue.Expanded
                ),
                arrangement = verticalArrangement,
                colors = colors.wideNavigationRailColors,
                content = { Row { movableContent(this) } },
            )
        }
        // Note: This function does not support providing a NavigationBar for the
        // NavigationSuiteType.NavigationBar type instead provides a ShortNavigationBar with a
        // taller height so that it is visually the same.
        // It's advised to to use NavigationSuiteType.ShortNavigationBarVerticalItems instead.
        NavigationSuiteType.Companion.NavigationBar -> {
            NavigationBar(
                modifier = modifier.heightIn(min = 80.dp),
                containerColor = colors.navigationBarContainerColor,
                contentColor = colors.navigationBarContentColor,
                content = { Row { movableContent(this) } },
            )
        }
        // It's advised to to use NavigationSuiteType.WideNavigationRail instead of
        // NavigationSuiteType.NavigationRail.
        NavigationSuiteType.Companion.NavigationRail -> {
            NavigationRail(
                modifier = modifier,
                header = { movablePrimaryActionContent() },
                containerColor = colors.navigationRailContainerColor,
                contentColor = colors.navigationRailContentColor,
            ) {
                if (
                    verticalArrangement == Arrangement.Center ||
                    verticalArrangement == Arrangement.Bottom
                ) {
                    Spacer(Modifier.weight(1f))
                }
                Row { movableContent(this) }
                if (verticalArrangement == Arrangement.Center) {
                    Spacer(Modifier.weight(1f))
                }
            }
        }
        // It's advised to to use NavigationSuiteType.WideNavigationRail instead of
        // NavigationSuiteType.NavigationDrawer.
        NavigationSuiteType.Companion.NavigationDrawer -> {
            PermanentDrawerSheet(
                modifier = modifier,
                drawerContainerColor = colors.navigationDrawerContainerColor,
                drawerContentColor = colors.navigationDrawerContentColor,
            ) {
                movablePrimaryActionContent()
                if (
                    verticalArrangement == Arrangement.Center ||
                    verticalArrangement == Arrangement.Bottom
                ) {
                    Spacer(Modifier.weight(1f))
                }
                Row { movableContent(this) }
                if (verticalArrangement == Arrangement.Center) {
                    Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}
