package com.sorrowblue.comicviewer.app;

import androidx.annotation.OptIn;
import androidx.compose.material3.NavigationBarItemColors;
import androidx.compose.material3.NavigationDrawerItemColors;
import androidx.compose.material3.NavigationRailItemColors;
import androidx.compose.material3.adaptive.navigation.suite.ExperimentalMaterial3AdaptiveNavigationSuiteApi;
import androidx.compose.material3.adaptive.navigation.suite.NavigationSuiteItemColors;

public class NavigationSuiteItemColors2 {

    @OptIn(markerClass = ExperimentalMaterial3AdaptiveNavigationSuiteApi.class)
    public static NavigationSuiteItemColors def(
            NavigationBarItemColors navigationBarItemColors,
            NavigationRailItemColors navigationRailItemColors,
            NavigationDrawerItemColors navigationDrawerItemColors
    ) {
        return new NavigationSuiteItemColors(navigationBarItemColors, navigationRailItemColors, navigationDrawerItemColors);
    }
}
