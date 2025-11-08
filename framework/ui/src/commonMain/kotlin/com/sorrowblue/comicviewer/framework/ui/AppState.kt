package com.sorrowblue.comicviewer.framework.ui

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.navigation.NavItem
import comicviewer.framework.ui.generated.resources.Res
import comicviewer.framework.ui.generated.resources.label_settings
import org.jetbrains.compose.resources.stringResource

val LocalAppState = staticCompositionLocalOf<AppState> {
    error("No AppState provided")
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun rememberAppState(
    sharedTransitionScope: SharedTransitionScope,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
): AppState {
    val navigationSuiteType = NavigationSuiteScaffoldDefaults.navigationSuiteType(
        currentWindowAdaptiveInfo(),
    )
    val appState = remember {
        AppStateImpl(navigationSuiteType, sharedTransitionScope, snackbarHostState)
    }
    appState.navigationSuiteType = navigationSuiteType
    appState.snackbarHostState = snackbarHostState
    return appState
}

@OptIn(ExperimentalSharedTransitionApi::class)
interface AppState : SharedTransitionScope {
    fun onNavItemClick(navItem: NavItem)

    val navItems: List<NavItem>

    val currentNavItem: NavItem?

    var navigationSuiteType: NavigationSuiteType
    val snackbarHostState: SnackbarHostState
}

@OptIn(ExperimentalSharedTransitionApi::class)
private class AppStateImpl(
    navigationSuiteType: NavigationSuiteType,
    sharedTransitionScope: SharedTransitionScope,
    override var snackbarHostState: SnackbarHostState,
) : AppState,
    SharedTransitionScope by sharedTransitionScope {
    override fun onNavItemClick(navItem: NavItem) = Unit

    override val navItems = mutableStateListOf<NavItem>(
        object : NavItem {
            override val title
                @Composable
                get() = stringResource(Res.string.label_settings)
            override val icon = ComicIcons.Settings
        },
        object : NavItem {
            override val title
                @Composable
                get() = stringResource(Res.string.label_settings)
            override val icon = ComicIcons.Settings
        },
        object : NavItem {
            override val title
                @Composable
                get() = stringResource(Res.string.label_settings)
            override val icon = ComicIcons.Settings
        },
        object : NavItem {
            override val title
                @Composable
                get() = stringResource(Res.string.label_settings)
            override val icon = ComicIcons.Settings
        },
    )
    override var currentNavItem by mutableStateOf<NavItem?>(null)
    override var navigationSuiteType by mutableStateOf(navigationSuiteType)
}
