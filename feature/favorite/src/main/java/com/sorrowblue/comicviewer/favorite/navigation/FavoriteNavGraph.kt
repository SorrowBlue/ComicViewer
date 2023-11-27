package com.sorrowblue.comicviewer.favorite.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.navigation
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteId
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.feature.favorite.edit.navigation.favoriteEditScreen
import com.sorrowblue.comicviewer.feature.favorite.edit.navigation.navigateToFavoriteEdit
import com.sorrowblue.comicviewer.folder.navigation.folderRoute
import com.sorrowblue.comicviewer.folder.navigation.folderScreen
import com.sorrowblue.comicviewer.folder.navigation.navigateToFolder

const val FavoriteGraphRoute = "favorite_graph"
val RouteInFavoriteGraph = listOf(FavoriteListRoute, FavoriteRoute, folderRoute(FavoriteListRoute))

fun NavGraphBuilder.favoriteGroup(
    contentPadding: PaddingValues,
    navController: NavController,
    navigateToBook: (Book) -> Unit,
    onFavoriteBookClick: (Book, FavoriteId) -> Unit,
    onClickLongFile: (File) -> Unit,
    onSettingsClick: () -> Unit,
    onSearchClick: (BookshelfId, String) -> Unit,
    onFavoriteClick: (File) -> Unit,
) {
    navigation(route = FavoriteGraphRoute, startDestination = FavoriteListRoute) {
        favoriteListScreen(
            contentPadding = contentPadding,
            onSettingsClick = onSettingsClick,
            onFavoriteClick = navController::navigateToFavorite
        )

        favoriteScreen(
            onBackClick = navController::popBackStack,
            onEditClick = navController::navigateToFavoriteEdit,
            onSettingsClick = onSettingsClick,
            onClickFile = { file, favoriteId ->
                when (file) {
                    is Book -> onFavoriteBookClick(file, favoriteId)

                    is Folder ->
                        navController.navigateToFolder(
                            FavoriteListRoute,
                            file.bookshelfId,
                            file.path
                        )
                }
            },
            onClickLongFile = onClickLongFile
        )

        favoriteEditScreen(
            onBackClick = navController::popBackStack,
            onComplete = navController::popBackStack
        )
        folderScreen(
            prefix = FavoriteListRoute,
            contentPadding = contentPadding,
            onBackClick = navController::popBackStack,
            onSearchClick = onSearchClick,
            onSettingsClick = onSettingsClick,
            onClickFile = { file ->
                when (file) {
                    is Book -> navigateToBook(file)
                    is Folder -> navController.navigateToFolder(
                        FavoriteListRoute,
                        file.bookshelfId,
                        file.path
                    )
                }
            },
            onFavoriteClick = onFavoriteClick
        )
    }
}
