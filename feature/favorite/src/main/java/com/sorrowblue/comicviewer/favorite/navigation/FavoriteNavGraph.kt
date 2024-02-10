package com.sorrowblue.comicviewer.favorite.navigation

import com.ramcosta.composedestinations.navigation.DependenciesContainerBuilder
import com.ramcosta.composedestinations.navigation.dependency
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.spec.DestinationSpec
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteId
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.favorite.FavoriteScreenNavigator
import com.sorrowblue.comicviewer.favorite.destinations.FavoriteFolderScreenDestination
import com.sorrowblue.comicviewer.favorite.destinations.FavoriteListScreenDestination
import com.sorrowblue.comicviewer.favorite.destinations.FavoriteScreenDestination
import com.sorrowblue.comicviewer.favorite.list.FavoriteListNavigator
import com.sorrowblue.comicviewer.feature.favorite.edit.FavoriteEditScreenNavigator
import com.sorrowblue.comicviewer.feature.favorite.edit.destinations.FavoriteEditScreenDestination
import com.sorrowblue.comicviewer.folder.FolderScreenNavigator
import com.sorrowblue.comicviewer.framework.ui.AnimatedNavGraphSpec
import com.sorrowblue.comicviewer.framework.ui.TransitionsConfigure

object FavoriteNavGraph : AnimatedNavGraphSpec {

    override val route = "favorite_graph"

    override val startRoute = FavoriteListScreenDestination

    override val showNavigation = listOf(
        FavoriteListScreenDestination,
        FavoriteScreenDestination,
        FavoriteFolderScreenDestination
    ).map(DestinationSpec<out Any>::route)

    override val destinationsByRoute: Map<String, DestinationSpec<*>> = listOf(
        FavoriteListScreenDestination,
        FavoriteScreenDestination,
        FavoriteEditScreenDestination,
        FavoriteFolderScreenDestination
    ).associateBy(DestinationSpec<*>::route)

    override val transitions = listOf(
        TransitionsConfigure(
            FavoriteListScreenDestination.route,
            FavoriteScreenDestination.route,
            TransitionsConfigure.Type.SharedAxisX
        ),
        TransitionsConfigure(
            FavoriteScreenDestination.route,
            FavoriteFolderScreenDestination.route,
            TransitionsConfigure.Type.SharedAxisX
        ),
        TransitionsConfigure(
            FavoriteFolderScreenDestination.route,
            FavoriteFolderScreenDestination.route,
            TransitionsConfigure.Type.SharedAxisX
        ),
        TransitionsConfigure(
            FavoriteScreenDestination.route,
            FavoriteEditScreenDestination.route,
            TransitionsConfigure.Type.SharedAxisY
        ),
        TransitionsConfigure(
            route,
            null,
            TransitionsConfigure.Type.ContainerTransform
        )
    )
}

fun DependenciesContainerBuilder<*>.dependencyFavoriteNavGraph(
    onBookClick: (Book, FavoriteId?) -> Unit,
    onFavoriteClick: (File) -> Unit,
    onSearchClick: (BookshelfId, String) -> Unit,
    onSettingsClick: () -> Unit,
) {
    dependency(FavoriteNavGraph) {
        object :
            FavoriteListNavigator,
            FavoriteScreenNavigator,
            FolderScreenNavigator,
            FavoriteEditScreenNavigator {

            override fun onFavoriteClick(file: File) = onFavoriteClick(file)

            override fun onSearchClick(bookshelfId: BookshelfId, path: String) =
                onSearchClick(bookshelfId, path)

            override fun onSettingsClick() = onSettingsClick()

            override fun navigateUp() {
                navController.navigateUp()
            }

            override fun onFavoriteClick(favoriteId: FavoriteId) {
                navController.navigate(FavoriteScreenDestination(favoriteId))
            }

            override fun onOpenFolderClick(file: File) {
                navController.navigate(
                    FavoriteFolderScreenDestination(
                        file.bookshelfId,
                        file.parent,
                        null
                    )
                )
            }

            override fun onEditClick(favoriteId: FavoriteId) {
                navController.navigate(FavoriteEditScreenDestination(favoriteId))
            }

            override fun onFileClick(file: File, favoriteId: FavoriteId) {
                when (file) {
                    is Book -> onBookClick(file, favoriteId)
                    is Folder -> navController.navigate(
                        FavoriteFolderScreenDestination(file.bookshelfId, file.path, null)
                    )
                }
            }

            override fun onComplete() {
                navController.popBackStack()
            }

            override fun onFileClick(file: File) {
                when (file) {
                    is Book -> onBookClick(file, null)
                    is Folder -> navController.navigate(
                        FavoriteFolderScreenDestination(file.bookshelfId, file.path, null)
                    )
                }
            }
        }
    }
}
