package com.sorrowblue.comicviewer.favorite.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.navigation
import com.sorrowblue.comicviewer.domain.entity.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.entity.file.Book
import com.sorrowblue.comicviewer.domain.entity.file.Folder
import com.sorrowblue.comicviewer.folder.navigation.folderRoute
import com.sorrowblue.comicviewer.folder.navigation.folderScreen
import com.sorrowblue.comicviewer.folder.navigation.navigateToFolder

const val FavoriteGroupRoute = "${FavoriteListRoute}_group"
val FavoriteFolderRoute = folderRoute(FavoriteListRoute)

fun NavGraphBuilder.favoriteGroup(
    contentPadding: PaddingValues,
    navController: NavController,
    onBookClick: (BookshelfId, String) -> Unit,
    onSettingsClick: () -> Unit,
    navigateToSearch: (BookshelfId, String) -> Unit,
    onAddFavoriteClick: (BookshelfId, String) -> Unit,
) {
    navigation(route = FavoriteGroupRoute, startDestination = FavoriteListRoute) {

        favoriteListScreen(
            contentPadding = contentPadding,
            onSettingsClick = onSettingsClick,
            onFavoriteClick = navController::navigateToFavorite
        )

        favoriteScreen(
            onBackClick = navController::popBackStack,
            onEditClick = navController::navigateToFavoriteEdit,
            onSettingsClick = onSettingsClick,
            onClickFile = {
                when (it) {
                    is Book -> onBookClick(it.bookshelfId, it.path)
                    is Folder ->
                        navController.navigateToFolder(FavoriteListRoute, it.bookshelfId, it.path)
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
            onClickFile = {
                when (it) {
                    is Book -> onBookClick(it.bookshelfId, it.path)
                    is Folder ->
                        navController.navigateToFolder(FavoriteListRoute, it.bookshelfId, it.path)
                }
            },
            onSettingsClick = onSettingsClick,
            onBackClick = navController::popBackStack,
            onAddFavoriteClick = { onAddFavoriteClick(it.bookshelfId, it.path) },
        )
    }
}
