package com.sorrowblue.comicviewer.app

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Text
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavHostController
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.animations.defaults.NestedNavGraphDefaultAnimations
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.rememberNavHostEngine
import com.ramcosta.composedestinations.spec.NavGraphSpec
import com.sorrowblue.comicviewer.domain.model.AddOn
import com.sorrowblue.comicviewer.feature.authentication.destinations.AuthenticationScreenDestination
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
import kotlinx.coroutines.flow.mapNotNull

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
internal fun ComicViewerApp(
    onTutorial: () -> Unit,
    onAuth: (Boolean) -> Unit,
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
            MainScreen(
                uiState = state.uiState,
                navController = state.navController,
                onTabSelected = { navController, tab ->
                    state.onTabSelected(
                        navController,
                        tab,
                    )
                },
            ) {
                DestinationsNavHost(
                    navGraph = RootNavGraph,
                    navController = state.navController,
                    engine = rememberNavHostEngine(
                        defaultAnimationsForNestedNavGraph = RootNavGraph.allNestedNavGraphs.associateWith {
                            if (it is AnimatedNavGraphSpec) {
                                it.animations(slideDistance)
                            } else {
                                NestedNavGraphDefaultAnimations()
                            }
                        }
                    ),
                    dependenciesContainerBuilder = {
                        mainDependency(
                            addOnList = addOnList,
                            onRestoreComplete = state::completeRestoreHistory,
                            onBack = { ActivityCompat.finishAffinity(activity) },
                            onAuthCompleted = { handleBack ->
                                if (handleBack) {
                                    state.navController.popBackStack()
                                } else {
                                    state.navController.navigate(RootNavGraph.startRoute) {
                                        popUpTo(AuthenticationScreenDestination.route) {
                                            inclusive = true
                                        }
                                    }
                                }
                            },
                            onTutorialExit = state::onCompleteTutorial
                        )
                    }
                )
            }
            @Suppress("KotlinConstantConditions")
            if (BuildConfig.BUILD_TYPE != "release") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(
                            (WindowInsets.statusBars.getTop(LocalDensity.current) / LocalDensity.current.density).dp
                        )
                        .background(ComicTheme.colorScheme.tertiaryContainer.copy(alpha = 0.25f))
                        .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal))
                        .padding(end = 80.dp)
                ) {
                    Text(
                        text = BuildConfig.BUILD_TYPE,
                        color = ComicTheme.colorScheme.onTertiaryContainer,
                        style = ComicTheme.typography.titleMedium,
                        modifier = Modifier.align(Alignment.CenterEnd)
                    )
                }
            }
        }
    }
    LifecycleEffect(Lifecycle.Event.ON_CREATE, action = state::onCreate)
    LifecycleEffect(Lifecycle.Event.ON_START, action = state::onStart)


    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val currentOnTutorial by rememberUpdatedState(onTutorial)
    LaunchedEffect(state, lifecycle) {
        // Whenever the uiState changes, check if the user is logged in and
        // call the `onUserLogin` event when `lifecycle` is at least STARTED
        snapshotFlow { state.appEvent }
            .filter { it.navigateToTutorial }
            .flowWithLifecycle(lifecycle)
            .collect {
                currentOnTutorial()
            }
    }
    val currentOnAuth by rememberUpdatedState(onAuth)
    LaunchedEffect(state, lifecycle) {
        // Whenever the uiState changes, check if the user is logged in and
        // call the `onUserLogin` event when `lifecycle` is at least STARTED
        snapshotFlow { state.appEvent }
            .mapNotNull { it.navigateToAuth }
            .flowWithLifecycle(lifecycle)
            .collect(currentOnAuth)
    }

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
