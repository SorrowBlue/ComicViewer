package com.sorrowblue.comicviewer.app

import android.app.Activity
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.navigation.navOptions
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.sorrowblue.comicviewer.feature.authentication.navigation.AuthenticationRoute
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.designsystem.theme.LocalDimension
import com.sorrowblue.comicviewer.framework.designsystem.theme.LocalWindowSize
import com.sorrowblue.comicviewer.framework.designsystem.theme.compactDimension
import com.sorrowblue.comicviewer.framework.designsystem.theme.expandedDimension
import com.sorrowblue.comicviewer.framework.designsystem.theme.mediumDimension
import com.sorrowblue.comicviewer.framework.ui.LifecycleEffect
import com.sorrowblue.comicviewer.framework.ui.preview.rememberMobile

@OptIn(ExperimentalMaterialNavigationApi::class, ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
internal fun ComicViewerApp(
    state: ComicViewerAppState,
    windowsSize: WindowSizeClass = calculateWindowSizeClass(LocalContext.current as ComponentActivity),
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
            val activity = LocalContext.current as Activity
            val isMobile = rememberMobile()
            val uiState = state.uiState
            MainScreen(
                uiState = uiState,
                bottomSheetNavigator = state.bottomSheetNavigator,
                navController = state.navController,
                startDestination = state.graphStateHolder.startDestination,
                onTabSelected = state.graphStateHolder::onTabSelected,
            ) { navHostController, contentPadding ->
                mainGraph(
                    isMobile = isMobile,
                    navController = navHostController,
                    contentPadding = contentPadding,
                    restoreComplete = state::completeRestoreHistory,
                    onTutorialExit = state::onCompleteTutorial,
                    onBackClick = {
                        ActivityCompat.finishAffinity(activity)
                    },
                    onAuthCompleted = { handleBack ->
                        if (handleBack) {
                            state.navController.popBackStack()
                        } else {
                            state.navController.navigate(
                                state.graphStateHolder.startDestination,
                                navOptions {
                                    popUpTo(AuthenticationRoute) {
                                        inclusive = true
                                    }
                                }
                            )
                        }
                    },
                    addOnList = addOnList,
                )
            }
            if (BuildConfig.BUILD_TYPE != "release") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(
                            (WindowInsets.statusBars.getTop(LocalDensity.current) / LocalDensity.current.density).dp
                        )
                        .background(ComicTheme.colorScheme.tertiaryContainer.copy(alpha = 0.75f))
                        .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal))
                        .padding(end = 80.dp)
                ) {
                    Text(
                        text = BuildConfig.BUILD_TYPE,
                        style = ComicTheme.typography.titleMedium,
                        modifier = Modifier.align(Alignment.CenterEnd)
                    )
                }
            }
        }
    }
    LifecycleEffect(Lifecycle.Event.ON_CREATE, action = state::onCreate)
    LifecycleEffect(Lifecycle.Event.ON_START, action = state::onStart)
}
