package com.sorrowblue.comicviewer.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.sorrowblue.comicviewer.app.component.ComicViewerScaffold
import com.sorrowblue.comicviewer.app.navigation.ComicViewerAppNavGraphImpl
import com.sorrowblue.comicviewer.app.navigation.ComicViewerAppNavigator
import com.sorrowblue.comicviewer.framework.navigation.NavGraphNavHost
import com.sorrowblue.comicviewer.framework.ui.EventEffect
import com.sorrowblue.comicviewer.framework.ui.core.isCompactWindowClass
import org.koin.compose.module.rememberKoinModules
import org.koin.dsl.module

@Composable
internal fun ComicViewerApp(state: ComicViewerAppState = rememberComicViewerAppState()) {
    ComicViewerScaffold(
        uiState = state.uiState,
        onTabSelect = { tab -> state.onTabSelect(tab) },
    ) {
        val navGraph = remember {
            ComicViewerAppNavGraphImpl()
        }
        rememberKoinModules {
            listOf(module {
                single {
                    ComicViewerAppNavigator(
                        onRestoreComplete = state::onNavigationHistoryRestore,
                        navController = state.navController
                    )
                }
            })
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
