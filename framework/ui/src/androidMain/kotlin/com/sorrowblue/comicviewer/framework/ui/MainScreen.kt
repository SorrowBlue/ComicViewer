package com.sorrowblue.comicviewer.framework.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedIconToggleButton
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteItem
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldValue
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.symbols.BottomNavigation
import com.sorrowblue.comicviewer.framework.designsystem.icon.symbols.ButtonsAlt
import com.sorrowblue.comicviewer.framework.designsystem.icon.symbols.SideNavigation
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.designsystem.theme.LocalContainerColor
import com.sorrowblue.comicviewer.framework.ui.canonical.isNavigationRail
import com.sorrowblue.comicviewer.framework.ui.layout.AdaptiveNavigationSuiteScaffold
import com.sorrowblue.comicviewer.framework.ui.layout.LocalScaffoldState
import com.sorrowblue.comicviewer.framework.ui.layout.rememberAdaptiveNavigationSuiteScaffoldState
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Composable
fun MainScreen() {
    ComicTheme {
        val state = rememberAdaptiveNavigationSuiteScaffoldState()
        val containerColor by animateColorAsState(
            if (state.navigationSuiteType.isNavigationRail) ComicTheme.colorScheme.surfaceContainer else ComicTheme.colorScheme.surface
        )
        val scope = rememberCoroutineScope()
        AdaptiveNavigationSuiteScaffold(
            state = state,
            navigationItems = {
                NavigationSuiteItem(
                    navigationSuiteType = state.navigationSuiteType,
                    selected = false,
                    label = { Text("Fab") },
                    icon = { Icon(ComicIcons.ButtonsAlt, null) },
                    onClick = {
                        scope.launch {
                            state.floatingActionButtonState.toggle()
                        }
                    }
                )
                NavigationSuiteItem(
                    navigationSuiteType = state.navigationSuiteType,
                    selected = state.navigationSuiteType.isNavigationRail,
                    label = { Text("Rail") },
                    icon = { Icon(ComicIcons.SideNavigation, null) },
                    onClick = {
                        state.updateNavigationSuiteType(NavigationSuiteType.WideNavigationRailCollapsed)
                    }
                )
                NavigationSuiteItem(
                    navigationSuiteType = state.navigationSuiteType,
                    selected = state.navigationSuiteType == NavigationSuiteType.ShortNavigationBarCompact,
                    label = { Text("CompactBar") },
                    icon = { Icon(ComicIcons.BottomNavigation, null) },
                    onClick = {
                        state.updateNavigationSuiteType(NavigationSuiteType.ShortNavigationBarCompact)
                    }
                )
                NavigationSuiteItem(
                    navigationSuiteType = state.navigationSuiteType,
                    selected = state.navigationSuiteType == NavigationSuiteType.ShortNavigationBarMedium,
                    label = { Text("MediumBar") },
                    icon = { Icon(ComicIcons.BottomNavigation, null) },
                    onClick = {
                        state.updateNavigationSuiteType(NavigationSuiteType.ShortNavigationBarMedium)
                    }
                )
            }
        ) {
            val navController = rememberNavController()
            CompositionLocalProvider(
                LocalScaffoldState provides state,
                LocalContainerColor provides containerColor
            ) {
                NavHost(navController = navController, startDestination = TopPage::class) {
                    composable<TopPage> {
                        TopPageScreen {
                            navController.navigate(SearchPage)
                        }
                    }
                    composable<SearchPage> {
                        SearchPageScreen {
                            navController.navigate(DetailPage)
                        }
                    }
                    composable<DetailPage> {
                        DetailPageScreen()
                    }
                }
            }
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                OutlinedIconToggleButton(
                    checked = state.navigationSuiteScaffoldState.targetValue == NavigationSuiteScaffoldValue.Visible,
                    onCheckedChange = { scope.launch { state.navigationSuiteScaffoldState.toggle() } }
                ) {
                    val imageVector =
                        if (state.navigationSuiteScaffoldState.targetValue == NavigationSuiteScaffoldValue.Visible) {
                            ComicIcons.Visibility
                        } else {
                            ComicIcons.VisibilityOff
                        }
                    Icon(imageVector, null)
                }
            }
        }
    }
}

@Serializable
data object TopPage

@Serializable
data object SearchPage

@Serializable
data object DetailPage
