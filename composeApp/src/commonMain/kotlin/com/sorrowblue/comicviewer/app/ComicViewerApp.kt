package com.sorrowblue.comicviewer.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.sorrowblue.comicviewer.app.component.ComicViewerScaffold
import com.sorrowblue.comicviewer.app.navigation.ComicViewerAppNavGraphImpl
import com.sorrowblue.comicviewer.app.navigation.ComicViewerAppNavigator
import com.sorrowblue.comicviewer.favorite.navigation.FavoriteNavGraphNavigator
import com.sorrowblue.comicviewer.feature.bookshelf.navgraph.BookshelfNavGraphNavigator
import com.sorrowblue.comicviewer.feature.favorite.add.FavoriteAddScreenNavigator
import com.sorrowblue.comicviewer.feature.readlater.navigation.ReadLaterNavGraphNavigator
import com.sorrowblue.comicviewer.feature.search.navigation.SearchNavGraphNavigator
import com.sorrowblue.comicviewer.feature.tutorial.navigation.TutorialNavGraphNavigator
import com.sorrowblue.comicviewer.framework.navigation.NavGraphNavHost
import com.sorrowblue.comicviewer.framework.ui.EventEffect
import com.sorrowblue.comicviewer.framework.ui.core.isCompactWindowClass
import logcat.logcat
import org.koin.compose.module.rememberKoinModules
import org.koin.dsl.binds
import org.koin.dsl.module

@Composable
internal fun ComicViewerApp(state: ComicViewerAppState = rememberComicViewerAppState()) {
    logcat("ComicViewerApp") { "LocalViewModelStoreOwner: ${LocalViewModelStoreOwner.current}" }
    ComicViewerScaffold(
        uiState = state.uiState,
        onTabSelect = { tab -> state.onTabSelect(tab) },
    ) {
        val navGraph = remember {
            ComicViewerAppNavGraphImpl()
        }
        rememberKoinModules {
            logcat("APPAPP") { "rememberKoinModules" }
            listOf(
                module {
                    single {
                        ComicViewerAppNavigator(
                            onRestoreComplete = state::onNavigationHistoryRestore,
                            navController = state.navController
                        )
                    } binds arrayOf(
                        BookshelfNavGraphNavigator::class,
                        ReadLaterNavGraphNavigator::class,
                        FavoriteNavGraphNavigator::class,
                        SearchNavGraphNavigator::class,
                        FavoriteAddScreenNavigator::class,
                        TutorialNavGraphNavigator::class
                    )
                }
            )
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
