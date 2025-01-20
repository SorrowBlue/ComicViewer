package com.sorrowblue.comicviewer.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.sorrowblue.comicviewer.app.component.ComicViewerScaffold
import com.sorrowblue.comicviewer.app.navigation.ComicViewerAppNavGraphImpl
import com.sorrowblue.comicviewer.framework.navigation.NavGraphNavHost
import com.sorrowblue.comicviewer.framework.navigation.navigationModule
import com.sorrowblue.comicviewer.framework.ui.EventEffect
import com.sorrowblue.comicviewer.framework.ui.core.isCompactWindowClass
import logcat.logcat
import org.koin.compose.module.rememberKoinModules

@Composable
internal fun ComicViewerApp(state: ComicViewerAppState = rememberComicViewerAppState()) {
    ComicViewerScaffold(
        uiState = state.uiState,
        onTabSelect = { tab -> state.onTabSelect(tab) },
    ) {
//        LaunchedEffect(state.navController) {
//            state.navController.addOnDestinationChangedListener { controller, destination, arguments ->
//                logcat { destination.hierarchy.joinToString(",") { it.route.orEmpty().ifEmpty { "???" } } }
//            }
//        }

        val navGraph = remember {
            ComicViewerAppNavGraphImpl()
        }
        NavGraphNavHost(
            navGraph = navGraph,
            isCompact = isCompactWindowClass(),
            navController = state.navController
        )
    }
    EventEffect(state.events) {
        when (it) {
            is ComicViewerAppEvent.Navigate -> {
                state.navController.navigate(it.route, it.navOptions)
            }
        }
    }
}
