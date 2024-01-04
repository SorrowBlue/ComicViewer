package com.sorrowblue.comicviewer.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.navOptions
import com.sorrowblue.comicviewer.bookshelf.navigation.BookshelfNavGraph
import com.sorrowblue.comicviewer.bookshelf.navigation.routeInBookshelfGraph
import com.sorrowblue.comicviewer.favorite.navigation.FavoriteNavGraph
import com.sorrowblue.comicviewer.favorite.navigation.RouteInFavoriteGraph
import com.sorrowblue.comicviewer.feature.library.navigation.LibraryNavGraph
import com.sorrowblue.comicviewer.feature.library.navigation.RouteInLibraryNavigation
import com.sorrowblue.comicviewer.feature.readlater.navigation.ReadLaterNavGraph
import com.sorrowblue.comicviewer.feature.readlater.navigation.RouteInReadlaterGraph
import com.sorrowblue.comicviewer.framework.ui.NavTabHandler

internal interface GraphStateHolder {
    val startDestination: String
    fun routeToTab(route: String): MainScreenTab?
    fun onTabSelected(navController: NavController, tab: MainScreenTab, viewModel: NavTabHandler)
}

@Composable
internal fun rememberGraphStateHolder(): GraphStateHolder = remember {
    ComicViewerAppGraphStateHolder()
}

private class ComicViewerAppGraphStateHolder : GraphStateHolder {
    override val startDestination: String = BookshelfNavGraph.route

    override fun routeToTab(route: String): MainScreenTab? {
        return when (route) {
            in routeInBookshelfGraph -> MainScreenTab.Bookshelf
            in RouteInFavoriteGraph -> MainScreenTab.Favorite
            in RouteInReadlaterGraph -> MainScreenTab.Readlater
            in RouteInLibraryNavigation -> MainScreenTab.Library
            else -> null
        }
    }

    override fun onTabSelected(
        navController: NavController,
        tab: MainScreenTab,
        viewModel: NavTabHandler,
    ) {
        when (tab) {
            MainScreenTab.Bookshelf -> BookshelfNavGraph.route
            MainScreenTab.Favorite -> FavoriteNavGraph.route
            MainScreenTab.Readlater -> ReadLaterNavGraph.route
            MainScreenTab.Library -> LibraryNavGraph.route
        }.let { route ->
            if (navController.currentBackStackEntry?.destination?.hierarchy?.any { it.route == route } == true) {
                viewModel.currentOnClick?.let { it() }
            } else {
                navController.navigate(
                    route,
                    navOptions {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                )
            }
        }
    }
}
