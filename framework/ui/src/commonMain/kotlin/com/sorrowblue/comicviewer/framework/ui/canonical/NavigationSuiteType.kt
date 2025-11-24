package com.sorrowblue.comicviewer.framework.ui.canonical

import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType

val NavigationSuiteType.isNavigationBar: Boolean get() = this ==
    NavigationSuiteType.ShortNavigationBarMedium ||
    this == NavigationSuiteType.ShortNavigationBarCompact
val NavigationSuiteType.isNavigationRail: Boolean get() =
    this == NavigationSuiteType.WideNavigationRailCollapsed ||
        this == NavigationSuiteType.WideNavigationRailExpanded ||
        this == NavigationSuiteType.NavigationRail
