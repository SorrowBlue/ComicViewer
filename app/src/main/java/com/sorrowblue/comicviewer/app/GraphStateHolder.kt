package com.sorrowblue.comicviewer.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.navOptions
import com.sorrowblue.comicviewer.bookshelf.navigation.BookshelfGraphRoute
import com.sorrowblue.comicviewer.bookshelf.navigation.BookshelfRoute
import com.sorrowblue.comicviewer.bookshelf.navigation.navigateToBookshelfSelection
import com.sorrowblue.comicviewer.bookshelf.navigation.routeInBookshelfGraph
import com.sorrowblue.comicviewer.favorite.navigation.FavoriteListRoute
import com.sorrowblue.comicviewer.favorite.navigation.favoriteGraphRoute
import com.sorrowblue.comicviewer.favorite.navigation.routeInFavoriteGraph
import com.sorrowblue.comicviewer.feature.favorite.create.navigation.navigateToFavoriteCreate
import com.sorrowblue.comicviewer.feature.library.navigation.libraryGraphRoute
import com.sorrowblue.comicviewer.feature.library.navigation.routeInLibraryGraph
import com.sorrowblue.comicviewer.feature.readlater.navigation.readlaterGraphRoute
import com.sorrowblue.comicviewer.feature.readlater.navigation.routeInReadlaterGraph

internal interface GraphStateHolder {
    val startDestination: String
    fun routeToTab(route: String): MainScreenTab?
    fun onTabSelected(navController: NavController, tab: MainScreenTab)
    fun routeToFab(route: String): MainScreenFab?
    fun onTabClick(navController: NavController, fab: MainScreenFab)
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
            in routeInFavoriteGraph -> MainScreenTab.Favorite
            in routeInReadlaterGraph -> MainScreenTab.Readlater
            in routeInLibraryGraph -> MainScreenTab.Library
            else -> null
        }
    }

    override fun onTabSelected(navController: NavController, tab: MainScreenTab) {
        when (tab) {
            MainScreenTab.Bookshelf -> BookshelfGraphRoute
            MainScreenTab.Favorite -> favoriteGraphRoute
            MainScreenTab.Readlater -> readlaterGraphRoute
            MainScreenTab.Library -> libraryGraphRoute
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

    override fun routeToFab(route: String): MainScreenFab? {
        return when (route) {
            BookshelfRoute -> MainScreenFab.Bookshelf
            FavoriteListRoute -> MainScreenFab.Favorite
            else -> null
        }
    }

    override fun onTabClick(navController: NavController, fab: MainScreenFab) {
        when (fab) {
            MainScreenFab.Bookshelf -> navController.navigateToBookshelfSelection()
            MainScreenFab.Favorite -> navController.navigateToFavoriteCreate()
        }
    }
}