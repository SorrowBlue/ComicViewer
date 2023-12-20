package com.sorrowblue.comicviewer.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.navOptions
import com.sorrowblue.comicviewer.bookshelf.navigation.BookshelfGraphRoute
import com.sorrowblue.comicviewer.bookshelf.navigation.routeInBookshelfGraph
import com.sorrowblue.comicviewer.favorite.navigation.FavoriteGraphRoute
import com.sorrowblue.comicviewer.favorite.navigation.RouteInFavoriteGraph
import com.sorrowblue.comicviewer.feature.library.navigation.LibraryGraphRoute
import com.sorrowblue.comicviewer.feature.library.navigation.RouteInLibraryGraph
import com.sorrowblue.comicviewer.feature.readlater.navigation.ReadlaterGraphRoute
import com.sorrowblue.comicviewer.feature.readlater.navigation.RouteInReadlaterGraph

internal interface GraphStateHolder {
    val startDestination: String
    fun routeToTab(route: String): MainScreenTab?
    fun onTabSelected(navController: NavController, tab: MainScreenTab)
}

@Composable
internal fun rememberGraphStateHolder(): GraphStateHolder = remember {
    ComicViewerAppGraphStateHolder()
}

private class ComicViewerAppGraphStateHolder : GraphStateHolder {
    override val startDestination: String = BookshelfGraphRoute

    override fun routeToTab(route: String): MainScreenTab? {
        return when (route) {
            in routeInBookshelfGraph -> MainScreenTab.Bookshelf
            in RouteInFavoriteGraph -> MainScreenTab.Favorite
            in RouteInReadlaterGraph -> MainScreenTab.Readlater
            in RouteInLibraryGraph -> MainScreenTab.Library
            else -> null
        }
    }

    override fun onTabSelected(navController: NavController, tab: MainScreenTab) {
        when (tab) {
            MainScreenTab.Bookshelf -> BookshelfGraphRoute
            MainScreenTab.Favorite -> FavoriteGraphRoute
            MainScreenTab.Readlater -> ReadlaterGraphRoute
            MainScreenTab.Library -> LibraryGraphRoute
        }.let { route ->
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
