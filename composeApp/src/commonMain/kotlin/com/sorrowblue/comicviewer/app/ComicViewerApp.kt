package com.sorrowblue.comicviewer.app

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.sorrowblue.cmpdestinations.NavGraphNavHost
import com.sorrowblue.comicviewer.app.navigation.ComicViewerAppNavGraph
import com.sorrowblue.comicviewer.app.navigation.ComicViewerAppNavigatorImpl
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.designsystem.theme.LocalContainerColor
import com.sorrowblue.comicviewer.framework.ui.LocalAppState
import com.sorrowblue.comicviewer.framework.ui.animation.Transitions
import com.sorrowblue.comicviewer.framework.ui.canonical.isNavigationRail
import com.sorrowblue.comicviewer.framework.ui.core.isCompactWindowClass
import org.koin.compose.module.rememberKoinModules
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.module

/** ComicViewerApp */
@OptIn(KoinExperimentalAPI::class, ExperimentalSharedTransitionApi::class)
@Composable
internal fun ComicViewerApp() {
    Transitions.InitSlideDistance()
    SharedTransitionLayout {
        val state = rememberComicViewerAppState(this)
        rememberKoinModules {
            listOf(
                module {
                    single<ComicViewerAppNavigatorImpl> {
                        object : ComicViewerAppNavigatorImpl {
                            override fun onRestoreComplete() = state.onNavigationHistoryRestore()
                        }
                    }
                    single<NavController> { state.navController }
                }
            )
        }
        val containerColor by animateColorAsState(
            if (state.navigationSuiteType.isNavigationRail) ComicTheme.colorScheme.surfaceContainer else ComicTheme.colorScheme.surface
        )
        CompositionLocalProvider(
            LocalAppState provides state,
            LocalContainerColor provides containerColor
        ) {
            NavGraphNavHost(
                graphNavigation = ComicViewerAppNavGraph,
                navController = state.navController,
                isCompact = isCompactWindowClass(),
                modifier = Modifier.fillMaxSize().background(LocalContainerColor.current)
            )
        }
    }
}
