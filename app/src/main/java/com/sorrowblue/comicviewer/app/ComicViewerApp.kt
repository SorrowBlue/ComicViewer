package com.sorrowblue.comicviewer.app

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigation.suite.ExperimentalMaterial3AdaptiveNavigationSuiteApi
import androidx.compose.material3.adaptive.navigation.suite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigation.suite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigation.suite.NavigationSuiteType
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavHostController
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.animations.defaults.NestedNavGraphDefaultAnimations
import com.ramcosta.composedestinations.navigation.DependenciesContainerBuilder
import com.ramcosta.composedestinations.rememberNavHostEngine
import com.ramcosta.composedestinations.spec.NavGraphSpec
import com.sorrowblue.comicviewer.app.component.BuildTypeStatusBar
import com.sorrowblue.comicviewer.app.navigation.RootNavGraph
import com.sorrowblue.comicviewer.app.navigation.mainDependency
import com.sorrowblue.comicviewer.app.section.LockScreen
import com.sorrowblue.comicviewer.domain.model.AddOn
import com.sorrowblue.comicviewer.feature.library.serviceloader.AddOnNavGraph
import com.sorrowblue.comicviewer.feature.library.serviceloader.BoxNavGraph
import com.sorrowblue.comicviewer.feature.library.serviceloader.DropBoxNavGraph
import com.sorrowblue.comicviewer.feature.library.serviceloader.GoogleDriveNavGraph
import com.sorrowblue.comicviewer.feature.library.serviceloader.OneDriveNavGraph
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.designsystem.theme.LocalDimension
import com.sorrowblue.comicviewer.framework.designsystem.theme.LocalWindowSize
import com.sorrowblue.comicviewer.framework.designsystem.theme.compactDimension
import com.sorrowblue.comicviewer.framework.designsystem.theme.expandedDimension
import com.sorrowblue.comicviewer.framework.designsystem.theme.mediumDimension
import com.sorrowblue.comicviewer.framework.ui.AnimatedNavGraphSpec
import com.sorrowblue.comicviewer.framework.ui.LifecycleEffect
import com.sorrowblue.comicviewer.framework.ui.rememberSlideDistance
import kotlinx.coroutines.flow.filter

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
internal fun ComicViewerApp(
    onTutorial: () -> Unit,
    navController: NavHostController,
    state: ComicViewerAppState = rememberComicViewerAppState(navController = navController),
    windowsSize: WindowSizeClass = calculateWindowSizeClass(LocalContext.current as ComponentActivity),
    activity: ComponentActivity = LocalContext.current as ComponentActivity,
) {
    val dimension = when (windowsSize.widthSizeClass) {
        WindowWidthSizeClass.Compact -> compactDimension
        WindowWidthSizeClass.Medium -> mediumDimension
        WindowWidthSizeClass.Expanded -> expandedDimension
        else -> compactDimension
    }
    CompositionLocalProvider(
        LocalWindowSize provides windowsSize,
        LocalDimension provides dimension,
    ) {
        ComicTheme {
            val addOnList = state.addOnList
            val slideDistance = rememberSlideDistance()
            ComicViewerApp(
                uiState = state.uiState,
                onTabSelected = { tab -> state.onTabSelected(navController, tab) },
            ) {
                AppNavHost(
                    navController = state.navController,
                    slideDistance = slideDistance
                ) {
                    mainDependency(
                        addOnList = addOnList,
                        onRestoreComplete = state::completeRestoreHistory,
                        onTutorialExit = state::onCompleteTutorial
                    )
                }
            }

            BuildTypeStatusBar(BuildConfig.BUILD_TYPE)

            LockScreen(
                uiState = state.uiState,
                onBack = { ActivityCompat.finishAffinity(activity) },
                onCompleted = state::onAuthCompleted,
            )
        }
    }
    LifecycleEffect(Lifecycle.Event.ON_CREATE, action = state::onCreate)
    LifecycleEffect(Lifecycle.Event.ON_START, action = state::onStart)

    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val currentOnTutorial by rememberUpdatedState(onTutorial)
    LaunchedEffect(state, lifecycle) {
        snapshotFlow { state.appEvent }
            .filter { it.navigateToTutorial }
            .flowWithLifecycle(lifecycle)
            .collect {
                currentOnTutorial()
            }
    }
}

@Composable
@OptIn(
    ExperimentalMaterial3AdaptiveNavigationSuiteApi::class,
    ExperimentalMaterial3AdaptiveApi::class
)
private fun ComicViewerApp(
    uiState: MainScreenUiState,
    onTabSelected: (MainScreenTab) -> Unit,
    content: @Composable () -> Unit,
) {
    val navSuiteType: NavigationSuiteType = if (uiState.currentTab != null) {
        NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(currentWindowAdaptiveInfo())
    } else {
        NavigationSuiteType.None
    }
    val colors = NavigationSuiteItemColors2.def(
        NavigationBarItemDefaults.colors(),
        NavigationRailItemDefaults.colors(),
        NavigationDrawerItemDefaults.colors()
    )
    NavigationSuiteScaffold(
        modifier = if (navSuiteType == NavigationSuiteType.NavigationBar || navSuiteType == NavigationSuiteType.None) {
            Modifier
        } else {
            Modifier
                .background(ComicTheme.colorScheme.background)
                .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Start))
        },
        navigationSuiteItems = {
            uiState.tabs.forEach {
                item(
                    selected = it == uiState.currentTab,
                    onClick = { onTabSelected(it) },
                    icon = {
                        Icon(
                            imageVector = it.icon,
                            contentDescription = stringResource(id = it.label)
                        )
                    },
                    label = {
                        Text(text = stringResource(id = it.label))
                    },
                    colors = colors
                )
            }
        },
        layoutType = navSuiteType,
        content = {
            Box(
                modifier = if (navSuiteType == NavigationSuiteType.NavigationBar) {
                    Modifier.consumeWindowInsets(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom))
                } else {
                    Modifier
                }
            ) {
                content()
            }
        }
    )
}

fun AddOn.findNavGraph(): AddOnNavGraph? {
    return when (this) {
        AddOn.Document -> null
        AddOn.GoogleDrive -> GoogleDriveNavGraph()
        AddOn.OneDrive -> OneDriveNavGraph()
        AddOn.Dropbox -> DropBoxNavGraph()
        AddOn.Box -> BoxNavGraph()
    }
}

val NavGraphSpec.allNestedNavGraphs: List<NavGraphSpec>
    get() = nestedNavGraphs + nestedNavGraphs.flatMap(NavGraphSpec::allNestedNavGraphs)

@Composable
fun AppNavHost(
    navController: NavHostController,
    slideDistance: Int,
    dependenciesContainerBuilder: @Composable (DependenciesContainerBuilder<*>.() -> Unit),
) {
    DestinationsNavHost(
        navGraph = RootNavGraph,
        navController = navController,
        engine = rememberNavHostEngine(
            defaultAnimationsForNestedNavGraph = RootNavGraph.allNestedNavGraphs.associateWith {
                if (it is AnimatedNavGraphSpec) {
                    it.animations(slideDistance)
                } else {
                    NestedNavGraphDefaultAnimations()
                }
            }
        ),
        dependenciesContainerBuilder = dependenciesContainerBuilder
    )
}
