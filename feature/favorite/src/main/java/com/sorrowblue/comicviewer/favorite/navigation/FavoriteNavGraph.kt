package com.sorrowblue.comicviewer.favorite.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.navigation
import com.sorrowblue.comicviewer.domain.entity.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.entity.file.Book
import com.sorrowblue.comicviewer.domain.entity.file.Folder
import com.sorrowblue.comicviewer.folder.navigation.folderRoute
import com.sorrowblue.comicviewer.folder.navigation.folderScreen
import com.sorrowblue.comicviewer.folder.navigation.navigateToFolder
import com.sorrowblue.comicviewer.framework.compose.FabVisibleState

const val FavoriteGroupRoute = "${FavoriteListRoute}_group"

val ShowNavigationBarFavoriteNavGraph =
    listOf(FavoriteListRoute, FavoriteRoute, folderRoute(FavoriteListRoute))

fun NavGraphBuilder.favoriteGroup(
    navController: NavController,
    fabState: FabVisibleState,
    onBookClick: (BookshelfId, String) -> Unit,
    onSettingsClick: () -> Unit,
    navigateToSearch: (BookshelfId, String) -> Unit,
    onAddFavoriteClick: (BookshelfId, String) -> Unit,
) {
    navigation(route = FavoriteGroupRoute, startDestination = FavoriteListRoute) {

        favoriteListScreen(
            fabState = fabState,
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
                        navController.navigateToFolder(it.bookshelfId, it.path, FavoriteListRoute)
                }
            }
        )
        favoriteEditScreen(
            onBackClick = navController::popBackStack,
            onComplete = navController::popBackStack
        )
        folderScreen(prefix = FavoriteListRoute,
            navigateToSearch = navigateToSearch,
            onClickFile = {
                when (it) {
                    is Book -> onBookClick(it.bookshelfId, it.path)
                    is Folder ->
                        navController.navigateToFolder(it.bookshelfId, it.path, FavoriteListRoute)
                }
            },
            onSettingsClick = onSettingsClick,
            onBackClick = navController::popBackStack,
            onAddFavoriteClick = { onAddFavoriteClick(it.bookshelfId, it.path) }
        )
    }
}
