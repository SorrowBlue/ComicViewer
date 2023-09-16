package com.sorrowblue.comicviewer.favorite.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.navigation
import com.sorrowblue.comicviewer.domain.entity.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.entity.file.Book
import com.sorrowblue.comicviewer.domain.entity.file.Folder
import com.sorrowblue.comicviewer.feature.favorite.edit.navigation.favoriteEditScreen
import com.sorrowblue.comicviewer.feature.favorite.edit.navigation.navigateToFavoriteEdit
import com.sorrowblue.comicviewer.folder.navigation.folderRoute
import com.sorrowblue.comicviewer.folder.navigation.folderScreen
import com.sorrowblue.comicviewer.folder.navigation.navigateToFolder

const val favoriteGraphRoute = "favorite_graph"
val routeInFavoriteGraph
    get() = listOf(FavoriteListRoute, favoriteRoute, folderRoute(FavoriteListRoute))

fun NavGraphBuilder.favoriteGroup(
    contentPadding: PaddingValues,
    navController: NavController,
    onBookClick: (BookshelfId, String, Int) -> Unit,
    onSettingsClick: () -> Unit,
    navigateToSearch: (BookshelfId, String) -> Unit,
) {
    navigation(route = favoriteGraphRoute, startDestination = FavoriteListRoute) {

        favoriteListScreen(
            contentPadding = contentPadding,
            onSettingsClick = onSettingsClick,
            onFavoriteClick = navController::navigateToFavorite
        )

        favoriteScreen(
            onBackClick = navController::popBackStack,
            onEditClick = navController::navigateToFavoriteEdit,
            onSettingsClick = onSettingsClick,
            onClickFile = { file, position ->
                when (file) {
                    is Book -> onBookClick(file.bookshelfId, file.path, position)
                    is Folder ->
                        navController.navigateToFolder(
                            FavoriteListRoute,
                            file.bookshelfId,
                            file.path
                        )
                }
            }
        )

        favoriteEditScreen(
            onBackClick = navController::popBackStack,
            onComplete = navController::popBackStack
        )
        folderScreen(
            contentPadding = contentPadding,
            prefix = FavoriteListRoute,
            navigateToSearch = navigateToSearch,
            onClickFile = { file, position ->
                when (file) {
                    is Book -> onBookClick(file.bookshelfId, file.path, position)
                    is Folder ->
                        navController.navigateToFolder(
                            FavoriteListRoute,
                            file.bookshelfId,
                            file.path
                        )
                }
            },
            onSettingsClick = onSettingsClick,
            onBackClick = navController::popBackStack,
        )
    }
}
