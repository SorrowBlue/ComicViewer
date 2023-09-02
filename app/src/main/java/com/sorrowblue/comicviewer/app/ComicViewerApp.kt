package com.sorrowblue.comicviewer.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Book
import androidx.compose.material.icons.twotone.Favorite
import androidx.compose.material.icons.twotone.LibraryBooks
import androidx.compose.material.icons.twotone.WatchLater
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.sorrowblue.comicviewer.feature.book.navigation.bookScreen
import com.sorrowblue.comicviewer.feature.book.navigation.navigateToBook
import com.sorrowblue.comicviewer.bookshelf.navigation.BookshelfFolderRoute
import com.sorrowblue.comicviewer.bookshelf.navigation.BookshelfGroupRoute
import com.sorrowblue.comicviewer.bookshelf.navigation.BookshelfRoute
import com.sorrowblue.comicviewer.bookshelf.navigation.bookshelfGroup
import com.sorrowblue.comicviewer.favorite.navigation.FavoriteFolderRoute
import com.sorrowblue.comicviewer.favorite.navigation.FavoriteGroupRoute
import com.sorrowblue.comicviewer.favorite.navigation.FavoriteListRoute
import com.sorrowblue.comicviewer.favorite.navigation.FavoriteRoute
import com.sorrowblue.comicviewer.favorite.navigation.favoriteGroup
import com.sorrowblue.comicviewer.feature.favorite.add.navigation.favoriteAddScreen
import com.sorrowblue.comicviewer.feature.favorite.add.navigation.navigateToFavoriteAdd
import com.sorrowblue.comicviewer.feature.history.navigation.HistoryFolderRoute
import com.sorrowblue.comicviewer.feature.history.navigation.HistoryGroupRoute
import com.sorrowblue.comicviewer.feature.history.navigation.HistoryRoute
import com.sorrowblue.comicviewer.feature.history.navigation.historyGroup
import com.sorrowblue.comicviewer.feature.readlater.navigation.ReadLaterFolderRoute
import com.sorrowblue.comicviewer.feature.readlater.navigation.ReadLaterRoute
import com.sorrowblue.comicviewer.feature.readlater.navigation.ReadlaterGroupRoute
import com.sorrowblue.comicviewer.feature.readlater.navigation.readlaterGroup
import com.sorrowblue.comicviewer.feature.search.navigation.navigateToSearch
import com.sorrowblue.comicviewer.feature.search.navigation.searchScreen
import com.sorrowblue.comicviewer.framework.compose.AppMaterialTheme
import com.sorrowblue.comicviewer.framework.compose.LocalWindowSize
import com.sorrowblue.comicviewer.settings.navigateToSettings
import com.sorrowblue.comicviewer.settings.settingsScreen

@Composable
fun ComicViewerApp(
    windowsSize: WindowSizeClass,
    modifier: Modifier = Modifier
) {
    AppMaterialTheme {
        CompositionLocalProvider(LocalWindowSize provides windowsSize) {
            Surface(
                modifier = modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background,
            ) {
                ComicViewerNavHost(windowsSize = windowsSize)
            }
        }
    }
}

@Composable
fun ComicViewerNavHost(
    windowsSize: WindowSizeClass,
    navController: NavHostController = rememberNavController()
) {

    NavHostWithSharedAxisX(navController = navController, startDestination = mainScreenRoute) {
        mainScreen(windowsSize, navController)
        searchScreen(navController::popBackStack)
        settingsScreen()
        bookScreen(
            onBackClick = navController::popBackStack,
            onNextBookClick = {
                navController.navigateToBook(it.bookshelfId, it.path)
            })

        favoriteAddScreen(onBackClick = navController::popBackStack)
    }
}


private fun NavGraphBuilder.mainScreen(
    windowSize: WindowSizeClass,
    navController: NavHostController,
) {
    mainScreen(
        windowSize = windowSize,
        mainNestedGraphStateHolder = ComicViewerAppMainNestedGraphStateHolder(),
        mainNestedGraph = { mainNestedNavController, contentPadding ->
            bookshelfGroup(
                contentPadding = contentPadding,
                navController = mainNestedNavController,
                onSettingsClick = navController::navigateToSettings,
                navigateToBook = navController::navigateToBook,
                navigateToSearch = navController::navigateToSearch,
                onAddFavoriteClick = navController::navigateToFavoriteAdd,
            )
            favoriteGroup(
                contentPadding = contentPadding,
                navController = mainNestedNavController,
                onBookClick = navController::navigateToBook,
                onSettingsClick = navController::navigateToSettings,
                navigateToSearch = navController::navigateToSearch,
                onAddFavoriteClick = navController::navigateToFavoriteAdd,
            )
            readlaterGroup(
                contentPadding = contentPadding,
                navController = mainNestedNavController,
                onBookClick = navController::navigateToBook,
                onSettingsClick = navController::navigateToSettings,
                onAddFavoriteClick = navController::navigateToFavoriteAdd,
                navigateToSearch = navController::navigateToSearch
            )
            historyGroup(
                contentPadding = contentPadding,
                navController = mainNestedNavController,
                onBookClick = navController::navigateToBook,
                onSettingsClick = navController::navigateToSettings,
                onAddFavoriteClick = navController::navigateToFavoriteAdd,
                navigateToSearch = navController::navigateToSearch
            )
        },
    )
}

class ComicViewerAppMainNestedGraphStateHolder : MainNestedGraphStateHolder {
    override val startDestination: String = BookshelfGroupRoute

    override fun routeToTab(route: String): MainScreenTab {
        return when (route) {
            BookshelfRoute, BookshelfFolderRoute -> MainScreenTab.Bookshelf
            FavoriteListRoute, FavoriteRoute, FavoriteFolderRoute -> MainScreenTab.Favorite
            ReadLaterRoute, ReadLaterFolderRoute -> MainScreenTab.Readlater
            HistoryRoute, HistoryFolderRoute -> MainScreenTab.Library
            else -> MainScreenTab.Bookshelf
        }
    }

    override fun onTabSelected(
        navController: NavController,
        tab: MainScreenTab,
    ) {
        when (tab) {
            MainScreenTab.Bookshelf -> BookshelfGroupRoute
            MainScreenTab.Favorite -> FavoriteGroupRoute
            MainScreenTab.Readlater -> ReadlaterGroupRoute
            MainScreenTab.Library -> HistoryGroupRoute
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

enum class MainScreenTab(
    val label: Int,
    val icon: ImageVector,
    val contentDescription: Int
) {

    Bookshelf(R.string.app_label_bookshelf, Icons.TwoTone.Book, R.string.app_label_bookshelf),
    Favorite(R.string.app_label_favorite, Icons.TwoTone.Favorite, R.string.app_label_favorite),
    Readlater(
        R.string.app_label_read_later,
        Icons.TwoTone.WatchLater,
        R.string.app_label_read_later
    ),
    Library(R.string.app_label_library, Icons.TwoTone.LibraryBooks, R.string.app_label_library),

}
