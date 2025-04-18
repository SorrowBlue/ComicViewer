package com.sorrowblue.comicviewer.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import com.sorrowblue.cmpdestinations.NavGraphNavHost
import com.sorrowblue.comicviewer.app.component.ComicViewerScaffold
import com.sorrowblue.comicviewer.app.navigation.ComicViewerAppNavGraphImpl
import com.sorrowblue.comicviewer.app.navigation.ComicViewerAppNavigator
import com.sorrowblue.comicviewer.feature.book.navigation.BookNavGraphNavigator
import com.sorrowblue.comicviewer.feature.bookshelf.navgraph.BookshelfNavGraphNavigator
import com.sorrowblue.comicviewer.feature.collection.navigation.CollectionNavGraphNavigator
import com.sorrowblue.comicviewer.feature.history.navigation.HistoryNavGraphNavigator
import com.sorrowblue.comicviewer.feature.readlater.navigation.ReadLaterNavGraphNavigator
import com.sorrowblue.comicviewer.feature.search.navigation.SearchNavGraphNavigator
import com.sorrowblue.comicviewer.framework.ui.animation.rememberSlideDistance
import com.sorrowblue.comicviewer.framework.ui.core.isCompactWindowClass
import com.sorrowblue.comicviewer.framework.ui.navigation.DestinationTransitions
import org.koin.compose.module.rememberKoinModules
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.binds
import org.koin.dsl.module

@OptIn(KoinExperimentalAPI::class)
@Composable
internal fun ComicViewerApp(state: ComicViewerAppState = rememberComicViewerAppState()) {
    DestinationTransitions.slideDistance = rememberSlideDistance()
    ComicViewerScaffold(
        uiState = state.uiState,
        onTabSelect = { tab -> state.onTabSelect(tab) },
    ) {
        val navGraph = remember {
            ComicViewerAppNavGraphImpl()
        }
        rememberKoinModules(unloadModules = true) {
            listOf(
                module {
                    single {
                        ComicViewerAppNavigator(
                            onRestoreComplete = state::onNavigationHistoryRestore,
                            navController = state.navController
                        )
                    } binds arrayOf(
                        BookshelfNavGraphNavigator::class,
                        BookNavGraphNavigator::class,
                        ReadLaterNavGraphNavigator::class,
                        CollectionNavGraphNavigator::class,
                        SearchNavGraphNavigator::class,
                        HistoryNavGraphNavigator::class,
                    )
                    single<NavController> { state.navController }
                }
            )
        }
        NavGraphNavHost(
            navGraph = navGraph,
            isCompact = isCompactWindowClass(),
            navController = state.navController
        )
    }
}
