package com.sorrowblue.comicviewer.app

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.NavController
import com.sorrowblue.cmpdestinations.NavGraphNavHost
import com.sorrowblue.comicviewer.app.navigation.ComicViewerAppNavGraph
import com.sorrowblue.comicviewer.app.navigation.ComicViewerAppNavigatorImpl
import com.sorrowblue.comicviewer.framework.ui.LocalAppState
import com.sorrowblue.comicviewer.framework.ui.animation.rememberSlideDistance
import com.sorrowblue.comicviewer.framework.ui.core.isCompactWindowClass
import com.sorrowblue.comicviewer.framework.ui.navigation.DestinationTransitions
import org.koin.compose.module.rememberKoinModules
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.module

/**
 * ComicViewerApp
 */
@OptIn(KoinExperimentalAPI::class, ExperimentalSharedTransitionApi::class)
@Composable
internal fun ComicViewerApp() {
    DestinationTransitions.slideDistance = rememberSlideDistance()
    SharedTransitionLayout {
        val state = rememberComicViewerAppState(this)
        rememberKoinModules(unloadModules = true) {
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
        CompositionLocalProvider(LocalAppState provides state) {
            NavGraphNavHost(
                graphNavigation = ComicViewerAppNavGraph,
                isCompact = isCompactWindowClass(),
                navController = state.navController
            )
        }
    }
}
