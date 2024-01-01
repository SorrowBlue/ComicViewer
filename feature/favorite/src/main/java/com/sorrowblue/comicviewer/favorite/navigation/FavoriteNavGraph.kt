package com.sorrowblue.comicviewer.favorite.navigation

import androidx.navigation.NavGraphBuilder
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteId
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.favorite.destinations.FavoriteListScreenDestination
import com.sorrowblue.comicviewer.favorite.destinations.FavoriteScreenDestination
import com.sorrowblue.comicviewer.feature.favorite.edit.destinations.FavoriteEditScreenDestination
import com.sorrowblue.comicviewer.feature.favorite.edit.navigation.favoriteEditScreen
import com.sorrowblue.comicviewer.feature.favorite.edit.navigation.navigateToFavoriteEdit
import com.sorrowblue.comicviewer.folder.navigation.folderRoute
import com.sorrowblue.comicviewer.folder.navigation.folderScreen
import com.sorrowblue.comicviewer.folder.navigation.navigateToFolder
import com.sorrowblue.comicviewer.framework.ui.ComposeTransition
import com.sorrowblue.comicviewer.framework.ui.ComposeValue
import com.sorrowblue.comicviewer.framework.ui.animatedNavigation

const val FavoriteGraphRoute = "favorite_graph"
val RouteInFavoriteGraph =
    listOf(FavoriteListScreenDestination.route, FavoriteScreenDestination.route, folderRoute(FavoriteListScreenDestination.route))

context(ComposeValue)
fun NavGraphBuilder.favoriteGroup(
    navigateToBook: (Book) -> Unit,
    onFavoriteBookClick: (Book, FavoriteId) -> Unit,
    onSettingsClick: () -> Unit,
    onSearchClick: (BookshelfId, String) -> Unit,
    onFavoriteClick: (File) -> Unit,
) {
    animatedNavigation(
        startDestination = FavoriteListScreenDestination.route,
        route = FavoriteGraphRoute,
        transitions = listOf(
            ComposeTransition(
                FavoriteListScreenDestination.route,
                FavoriteScreenDestination.route,
                ComposeTransition.Type.SharedAxisX
            ),
            ComposeTransition(
                FavoriteScreenDestination.route,
                folderRoute(FavoriteListScreenDestination.route),
                ComposeTransition.Type.SharedAxisX
            ),
            ComposeTransition(
                FavoriteScreenDestination.route,
                FavoriteEditScreenDestination.route,
                ComposeTransition.Type.SharedAxisY
            ),
            ComposeTransition(
                FavoriteGraphRoute,
                null,
                ComposeTransition.Type.ContainerTransform
            )
        )
    ) {
        favoriteListScreen(
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
                            FavoriteListScreenDestination.route,
                            file.bookshelfId,
                            file.path
                        )
                }
            },
            onFavoriteClick = onFavoriteClick,
            onOpenFolderClick = { file ->
                when (file) {
                    is Book -> navController.navigateToFolder(
                        FavoriteListScreenDestination.route,
                        file.bookshelfId,
                        file.parent
                    )

                    is Folder -> navController.navigateToFolder(
                        FavoriteListScreenDestination.route,
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
            prefix = FavoriteListScreenDestination.route,
            onBackClick = navController::popBackStack,
            onSearchClick = onSearchClick,
            onSettingsClick = onSettingsClick,
            onClickFile = { file ->
                when (file) {
                    is Book -> navigateToBook(file)
                    is Folder -> navController.navigateToFolder(
                        FavoriteListScreenDestination.route,
                        file.bookshelfId,
                        file.path
                    )
                }
            },
            onFavoriteClick = onFavoriteClick
        )
    }
}
