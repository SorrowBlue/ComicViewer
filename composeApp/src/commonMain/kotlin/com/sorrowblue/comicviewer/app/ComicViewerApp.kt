package com.sorrowblue.comicviewer.app

import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.window.core.layout.WindowSizeClass
import com.sorrowblue.comicviewer.app.component.ComicViewerScaffold
import com.sorrowblue.comicviewer.app.navigation.ComicViewerAppNavGraphImpl
import com.sorrowblue.comicviewer.app.navigation.ComicViewerAppNavigator
import com.sorrowblue.comicviewer.favorite.navigation.FavoriteNavGraphNavigator
import com.sorrowblue.comicviewer.feature.bookshelf.navgraph.BookshelfNavGraphNavigator
import com.sorrowblue.comicviewer.feature.favorite.add.FavoriteAddScreenNavigator
import com.sorrowblue.comicviewer.feature.history.navigation.HistoryNavGraphNavigator
import com.sorrowblue.comicviewer.feature.readlater.navigation.ReadLaterNavGraphNavigator
import com.sorrowblue.comicviewer.feature.search.navigation.SearchNavGraphNavigator
import com.sorrowblue.comicviewer.feature.tutorial.navigation.TutorialNavGraphNavigator
import com.sorrowblue.comicviewer.framework.navigation.AppNavController
import com.sorrowblue.comicviewer.framework.navigation.NavGraphNavHost
import com.sorrowblue.comicviewer.framework.ui.EventEffect
import com.sorrowblue.comicviewer.framework.ui.core.isCompactWindowClass
import logcat.logcat
import org.koin.compose.module.rememberKoinModules
import org.koin.core.qualifier.qualifier
import org.koin.dsl.binds
import org.koin.dsl.module

@Composable
internal fun ComicViewerApp(state: ComicViewerAppState = rememberComicViewerAppState()) {
    val info = currentWindowAdaptiveInfo()
    LaunchedEffect(info) {
        logcat { "minWidthDp=${info.windowSizeClass.minWidthDp}, minHeightDp=${info.windowSizeClass.minHeightDp}" }
        logcat { "containsWidthDp MEDIUM=${info.windowSizeClass.containsWidthDp(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)}" }
        logcat { "containsWidthDp EXPANDED=${info.windowSizeClass.containsWidthDp(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)}" }
    }
    ComicViewerScaffold(
        uiState = state.uiState,
        onTabSelect = { tab -> state.onTabSelect(tab) },
    ) {
        val navGraph = remember {
            ComicViewerAppNavGraphImpl()
        }
        rememberKoinModules {
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
                        TutorialNavGraphNavigator::class,
                        HistoryNavGraphNavigator::class,
                    )
                    single<NavController>(qualifier<AppNavController>()) { state.navController }
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
