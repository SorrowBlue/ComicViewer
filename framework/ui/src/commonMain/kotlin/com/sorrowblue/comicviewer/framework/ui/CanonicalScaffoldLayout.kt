package com.sorrowblue.comicviewer.framework.ui

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.WideNavigationRailDefaults
import androidx.compose.material3.WideNavigationRailState
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldState
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.material3.adaptive.navigationsuite.rememberNavigationSuiteScaffoldState
import androidx.compose.material3.rememberWideNavigationRailState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.sorrowblue.cmpdestinations.animation.LocalAnimatedContentScope
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.NavigableExtraPaneScaffold
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.rememberCanonicalScaffoldNavigator
import com.sorrowblue.comicviewer.framework.ui.canonical.AnimatedNavigationSuiteScaffold
import com.sorrowblue.comicviewer.framework.ui.canonical.AppBarState
import com.sorrowblue.comicviewer.framework.ui.canonical.DefaultAppBarScope
import com.sorrowblue.comicviewer.framework.ui.canonical.FloatingActionButtonState
import com.sorrowblue.comicviewer.framework.ui.canonical.NavigationSuiteScope
import com.sorrowblue.comicviewer.framework.ui.canonical.isNavigationRail
import com.sorrowblue.comicviewer.framework.ui.canonical.rememberAppBarState
import com.sorrowblue.comicviewer.framework.ui.canonical.rememberFloatingActionButtonState
import com.sorrowblue.comicviewer.framework.ui.layout.asWindowInsets
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalSharedTransitionApi::class)
interface NavigationSuiteScaffold2State<T : Any> : AnimatedVisibilityScope, SharedTransitionScope {
    val suiteScaffoldState: NavigationSuiteScaffoldState
    val navigationRailState: WideNavigationRailState
    fun toggleNavigationRail()
    val appBarState: AppBarState
    val floatingActionButtonState: FloatingActionButtonState
    val navigator: ThreePaneScaffoldNavigator<T>

    val navItems: List<NavItem>
    val currentNavItem: NavItem?
    fun onNavItemClick(navItem: NavItem)
    val navigationSuiteType: NavigationSuiteType
    val snackbarHostState: SnackbarHostState
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
inline fun <reified T : @Serializable Any> rememberCanonicalScaffoldLayoutState(
    navigator: ThreePaneScaffoldNavigator<T> = rememberCanonicalScaffoldNavigator(),
    animatedContentScope: AnimatedContentScope = LocalAnimatedContentScope.current,
    suiteScaffoldState: NavigationSuiteScaffoldState = rememberNavigationSuiteScaffoldState(),
    navigationRailState: WideNavigationRailState = rememberWideNavigationRailState(),
    appBarState: AppBarState = rememberAppBarState(),
    floatingActionButtonState: FloatingActionButtonState = rememberFloatingActionButtonState(),
    appState: AppState = LocalAppState.current,
    scope: CoroutineScope = rememberCoroutineScope(),
): NavigationSuiteScaffold2State<T> {
    return remember {
        NavigationSuiteScaffold2StateImpl(
            animatedVisibilityScope = animatedContentScope,
            suiteScaffoldState = suiteScaffoldState,
            navigationRailState = navigationRailState,
            appBarState = appBarState,
            floatingActionButtonState = floatingActionButtonState,
            navigator = navigator,
            appState = appState,
            scope = scope
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalSharedTransitionApi::class)
class NavigationSuiteScaffold2StateImpl<T : Any>(
    animatedVisibilityScope: AnimatedVisibilityScope,
    override val suiteScaffoldState: NavigationSuiteScaffoldState,
    override val navigationRailState: WideNavigationRailState,
    override val appBarState: AppBarState,
    override val floatingActionButtonState: FloatingActionButtonState,
    override val navigator: ThreePaneScaffoldNavigator<T>,
    private val appState: AppState,
    private val scope: CoroutineScope,
) : NavigationSuiteScaffold2State<T>,
    AnimatedVisibilityScope by animatedVisibilityScope,
    SharedTransitionScope by appState.sharedTransitionScope {

    override fun toggleNavigationRail() {
        scope.launch {
            navigationRailState.toggle()
        }
    }

    override val navItems: List<NavItem> get() = appState.navItems
    override val currentNavItem: NavItem? get() = appState.currentNavItem
    override fun onNavItemClick(navItem: NavItem) {
        appState.onNavItemClick(navItem)
    }

    override val navigationSuiteType: NavigationSuiteType get() = appState.navigationSuiteType
    override val snackbarHostState: SnackbarHostState get() = appState.snackbarHostState
}

enum class PrimaryActionContentMode {
    Auto,
    NavigationBarOnly,
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun <T : Any> NavigationSuiteScaffold2State<T>.CanonicalScaffoldLayout(
    modifier: Modifier = Modifier,
    topBar: @Composable NavigationSuiteScaffold2State<T>.() -> Unit = {},
    primaryActionContent: @Composable NavigationSuiteScaffold2State<T>.() -> Unit = {},
    primaryActionContentMode: PrimaryActionContentMode = PrimaryActionContentMode.Auto,
    navigationSuiteItems: NavigationSuiteScope.() -> Unit = {
        navItems.forEach { navItem ->
            item(
                selected = navItem == currentNavItem,
                label = { Text(navItem.title) },
                icon = { Icon(navItem.icon, null) },
                onClick = { onNavItemClick(navItem) },
            )
        }
    },
    extraPane: @Composable (T) -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    val containerColor by animateColorAsState(
        if (navigationSuiteType.isNavigationRail) ComicTheme.colorScheme.surfaceContainer else ComicTheme.colorScheme.surface
    )
    NavigableExtraPaneScaffold(
        navigator = navigator,
        modifier = modifier.background(containerColor),
        extraPane = {
            val destination = navigator.currentDestination
            if (destination?.pane == SupportingPaneScaffoldRole.Extra && destination.contentKey != null) {
                extraPane(destination.contentKey!!)
            }
        },
    ) {
        Surface(color = containerColor) {
            AnimatedNavigationSuiteScaffold(
                state = this,
                navigationRailState = navigationRailState,
                layoutType = navigationSuiteType,
                navigationSuiteItems = navigationSuiteItems,
                primaryActionContent = { primaryActionContent() },
                primaryActionContentMode = primaryActionContentMode,
                colors = NavigationSuiteDefaults.colors(
                    shortNavigationBarContainerColor = ComicTheme.colorScheme.surfaceContainer,
                    wideNavigationRailColors = WideNavigationRailDefaults.colors(
                        containerColor = containerColor
                    ),
                )
            ) {
                Scaffold(
                    topBar = {
                        DefaultAppBarScope {
                            topBar()
                        }
                    },
                    containerColor = containerColor,
                    contentWindowInsets = it.asWindowInsets(),
                    modifier = appBarState.scrollBehavior?.nestedScrollConnection?.let(Modifier::nestedScroll)
                        ?: Modifier
                ) { contentPadding ->
                    content(contentPadding)
                }
            }
        }
    }
}
