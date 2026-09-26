/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.framework.ui.adaptive

import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable

/**
 * Returns the current [NavigationSuiteType], falling back to window calculation if not inside
 * an [AdaptiveNavigationSuiteScaffold].
 */
@Composable
fun currentNavigationSuiteType(): NavigationSuiteType =
    LocalAdaptiveNavigationSuiteScaffoldState.current?.navigationSuiteType
        ?: NavigationSuiteScaffoldDefaults.navigationSuiteType(currentWindowAdaptiveInfoV2())

/**
 * Returns `true` if this [NavigationSuiteType] represents a navigation bar layout
 * (e.g. [NavigationSuiteType.ShortNavigationBarMedium] or [NavigationSuiteType.ShortNavigationBarCompact]).
 *
 * @receiver The [NavigationSuiteType] to check.
 * @return `true` if this type is a navigation bar, `false` otherwise.
 */
val NavigationSuiteType.isNavigationBar: Boolean
    get() = this ==
        NavigationSuiteType.ShortNavigationBarMedium ||
        this == NavigationSuiteType.ShortNavigationBarCompact

/**
 * Returns `true` if this [NavigationSuiteType] represents a navigation rail layout
 * (e.g. [NavigationSuiteType.WideNavigationRailCollapsed], [NavigationSuiteType.WideNavigationRailExpanded],
 * or [NavigationSuiteType.NavigationRail]).
 *
 * @receiver The [NavigationSuiteType] to check.
 * @return `true` if this type is a navigation rail, `false` otherwise.
 */
val NavigationSuiteType.isNavigationRail: Boolean
    get() =
        this == NavigationSuiteType.WideNavigationRailCollapsed ||
            this == NavigationSuiteType.WideNavigationRailExpanded ||
            this == NavigationSuiteType.NavigationRail
