package com.sorrowblue.comicviewer.app

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.sorrowblue.cmpdestinations.NavGraphNavHost
import com.sorrowblue.comicviewer.app.component.ComicViewerScaffold
import com.sorrowblue.comicviewer.app.navigation.ComicViewerAppNavGraph
import com.sorrowblue.comicviewer.app.navigation.ComicViewerAppNavigatorImpl
import com.sorrowblue.comicviewer.framework.ui.animation.rememberSlideDistance
import com.sorrowblue.comicviewer.framework.ui.core.isCompactWindowClass
import com.sorrowblue.comicviewer.framework.ui.navigation.DestinationTransitions
import org.koin.compose.module.rememberKoinModules
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.module

@OptIn(KoinExperimentalAPI::class)
@Composable
internal fun ComicViewerApp(state: ComicViewerAppState = rememberComicViewerAppState()) {
    DestinationTransitions.slideDistance = rememberSlideDistance()
    ComicViewerScaffold(
        uiState = state.uiState,
        onTabSelect = { tab -> state.onTabSelect(tab) },
    ) {
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
        NavGraphNavHost(
            graphNavigation = ComicViewerAppNavGraph,
            isCompact = isCompactWindowClass(),
            navController = state.navController
        )
    }
}
