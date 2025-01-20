package com.sorrowblue.comicviewer.app

import androidx.compose.runtime.Composable
import com.sorrowblue.comicviewer.app.component.ComicViewerScaffold
import com.sorrowblue.comicviewer.app.navigation.ComicViewerAppNavGraphImpl
import com.sorrowblue.comicviewer.feature.readlater.navigation.ReadLaterNavGraph
import com.sorrowblue.comicviewer.framework.navigation.NavGraphNavHost
import com.sorrowblue.comicviewer.framework.ui.EventEffect
import com.sorrowblue.comicviewer.framework.ui.core.isCompactWindowClass

@Composable
internal fun ComicViewerApp(state: ComicViewerAppState = rememberComicViewerAppState()) {
    ComicViewerScaffold(
        uiState = state.uiState,
        onTabSelect = { tab -> state.onTabSelect(tab) },
    ) {
        NavGraphNavHost(
            navGraph = ComicViewerAppNavGraphImpl(),
            isCompact = isCompactWindowClass(),
            navController = state.navController
        )
    }
    EventEffect(state.events) {
        when (it) {
            is ComicViewerAppEvent.Navigate -> {
//                state.navController.navigate(it.route, it.navOptions)
                state.navController.navigate(ReadLaterNavGraph)
            }
        }
    }
}
