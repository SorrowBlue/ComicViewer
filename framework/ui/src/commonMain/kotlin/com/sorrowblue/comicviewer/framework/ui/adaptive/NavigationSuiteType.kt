/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.framework.ui.adaptive

import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable

@Composable
fun currentNavigationSuiteType(): NavigationSuiteType =
    NavigationSuiteScaffoldDefaults.navigationSuiteType(currentWindowAdaptiveInfoV2())

val NavigationSuiteType.isNavigationBar: Boolean
    get() = this ==
        NavigationSuiteType.ShortNavigationBarMedium ||
        this == NavigationSuiteType.ShortNavigationBarCompact

val NavigationSuiteType.isNavigationRail: Boolean
    get() =
        this == NavigationSuiteType.WideNavigationRailCollapsed ||
            this == NavigationSuiteType.WideNavigationRailExpanded ||
            this == NavigationSuiteType.NavigationRail
