package com.sorrowblue.comicviewer.app

import android.app.Activity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.navigation.navOptions
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.designsystem.theme.LocalDimension
import com.sorrowblue.comicviewer.framework.designsystem.theme.LocalWindowSize
import com.sorrowblue.comicviewer.framework.designsystem.theme.compactDimension
import com.sorrowblue.comicviewer.framework.designsystem.theme.expandedDimension
import com.sorrowblue.comicviewer.framework.designsystem.theme.mediumDimension
import com.sorrowblue.comicviewer.framework.ui.LifecycleEffect
import com.sorrowblue.comicviewer.framework.ui.preview.rememberMobile

@OptIn(ExperimentalMaterialNavigationApi::class)
@Composable
internal fun ComicViewerApp(
    state: ComicViewerAppState,
    windowsSize: WindowSizeClass,
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
                appFabState = state.appFabState,
                bottomSheetNavigator = state.bottomSheetNavigator,
                navController = state.navController,
                startDestination = state.graphStateHolder.startDestination,
                onTabSelected = state.graphStateHolder::onTabSelected,
                onFabClick = state.graphStateHolder::onTabClick,
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
                                    popUpTo(
                                        com.sorrowblue.comicviewer.feature.authentication.navigation.AuthenticationRoute
                                    ) {
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
                        .fillMaxSize()
                        .drawDiagonalLabel(
                            text = BuildConfig.BUILD_TYPE.uppercase(),
                            color = ComicTheme.colorScheme.tertiaryContainer.copy(alpha = 0.75f),
                            style = TextStyle(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ComicTheme.colorScheme.onTertiaryContainer
                            )
                        )
                )
            }
        }
    }
    LifecycleEffect(Lifecycle.Event.ON_CREATE, action = state::onCreate)
    LifecycleEffect(Lifecycle.Event.ON_START, action = state::onStart)
}
