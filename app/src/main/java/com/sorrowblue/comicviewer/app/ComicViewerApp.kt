package com.sorrowblue.comicviewer.app

import androidx.activity.ComponentActivity
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavHostController
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.spec.NavGraphSpec
import com.sorrowblue.comicviewer.app.component.BuildTypeStatusBar
import com.sorrowblue.comicviewer.app.navigation.MainDependencies
import com.sorrowblue.comicviewer.app.section.LockScreen
import com.sorrowblue.comicviewer.domain.model.AddOn
import com.sorrowblue.comicviewer.feature.library.serviceloader.AddOnNavGraph
import com.sorrowblue.comicviewer.feature.library.serviceloader.BoxNavGraph
import com.sorrowblue.comicviewer.feature.library.serviceloader.DropBoxNavGraph
import com.sorrowblue.comicviewer.feature.library.serviceloader.GoogleDriveNavGraph
import com.sorrowblue.comicviewer.feature.library.serviceloader.OneDriveNavGraph
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.designsystem.theme.LocalDimension
import com.sorrowblue.comicviewer.framework.designsystem.theme.compactDimension
import com.sorrowblue.comicviewer.framework.designsystem.theme.expandedDimension
import com.sorrowblue.comicviewer.framework.designsystem.theme.mediumDimension
import com.sorrowblue.comicviewer.framework.ui.DestinationTransitions
import com.sorrowblue.comicviewer.framework.ui.LifecycleEffect
import com.sorrowblue.comicviewer.framework.ui.LocalWindowAdaptiveInfo
import com.sorrowblue.comicviewer.framework.ui.LocalWindowSize
import com.sorrowblue.comicviewer.framework.ui.rememberSlideDistance
import kotlinx.coroutines.flow.filter

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
internal fun ComicViewerApp(
    onTutorial: () -> Unit,
    navController: NavHostController,
    state: ComicViewerAppState = rememberComicViewerAppState(navController = navController),
    windowsSize: WindowSizeClass = LocalConfiguration.current.run {
        WindowSizeClass.compute(screenWidthDp.toFloat(), screenHeightDp.toFloat())
    },
    activity: ComponentActivity = LocalContext.current as ComponentActivity,
) {
    val dimension = when (windowsSize.windowWidthSizeClass) {
        WindowWidthSizeClass.COMPACT -> compactDimension
        WindowWidthSizeClass.MEDIUM -> mediumDimension
        WindowWidthSizeClass.EXPANDED -> expandedDimension
        else -> compactDimension
    }
    CompositionLocalProvider(
        LocalWindowSize provides windowsSize,
        LocalDimension provides dimension,
        LocalWindowAdaptiveInfo provides currentWindowAdaptiveInfo()
    ) {
        ComicTheme {
            val addOnList = state.addOnList
            val slideDistance = rememberSlideDistance()
            LaunchedEffect(slideDistance) {
                DestinationTransitions.slideDistance = slideDistance
            }
            ComicViewerApp(
                uiState = state.uiState,
                onTabSelect = { tab -> state.onTabSelected(navController, tab) },
            ) {
                DestinationsNavHost(
                    navGraph = NavGraphs.main,
                    navController = state.navController,
                    dependenciesContainerBuilder = {
                        MainDependencies(
                            addOnList = addOnList,
                            onRestoreComplete = state::completeRestoreHistory,
                            onTutorialExit = state::onCompleteTutorial
                        )
                    },
                )
            }

            BuildTypeStatusBar(BuildConfig.BUILD_TYPE)

            LockScreen(
                uiState = state.uiState,
                onBack = { ActivityCompat.finishAffinity(activity) },
                onComplete = state::onAuthCompleted,
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
private fun ComicViewerApp(
    uiState: MainScreenUiState,
    onTabSelect: (MainScreenTab) -> Unit,
    content: @Composable () -> Unit,
) {
    val navSuiteType: NavigationSuiteType = if (uiState.currentTab != null) {
        NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(currentWindowAdaptiveInfo())
    } else {
        NavigationSuiteType.None
    }
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
                    onClick = { onTabSelect(it) },
                    icon = {
                        Icon(
                            imageVector = it.icon,
                            contentDescription = stringResource(id = it.label)
                        )
                    },
                    label = {
                        Text(text = stringResource(id = it.label))
                    }
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

val NavGraphSpec.allNestedNavGraphs: List<NavGraphSpec>
    get() = nestedNavGraphs + nestedNavGraphs.flatMap(NavGraphSpec::allNestedNavGraphs)

fun AddOn.findNavGraph(): AddOnNavGraph? {
    return when (this) {
        AddOn.Document -> null
        AddOn.GoogleDrive -> GoogleDriveNavGraph()
        AddOn.OneDrive -> OneDriveNavGraph()
        AddOn.Dropbox -> DropBoxNavGraph()
        AddOn.Box -> BoxNavGraph()
    }
}
