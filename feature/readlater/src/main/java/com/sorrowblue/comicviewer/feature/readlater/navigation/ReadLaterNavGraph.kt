package com.sorrowblue.comicviewer.feature.readlater.navigation

import com.ramcosta.composedestinations.navigation.DependenciesContainerBuilder
import com.ramcosta.composedestinations.navigation.dependency
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.spec.DestinationSpec
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.feature.readlater.ReadLaterScreenNavigator
import com.sorrowblue.comicviewer.feature.readlater.destinations.ReadLaterFolderScreenDestination
import com.sorrowblue.comicviewer.feature.readlater.destinations.ReadLaterScreenDestination
import com.sorrowblue.comicviewer.folder.FolderScreenNavigator
import com.sorrowblue.comicviewer.framework.ui.AnimatedNavGraphSpec
import com.sorrowblue.comicviewer.framework.ui.TransitionsConfigure

object ReadLaterNavGraph : AnimatedNavGraphSpec {

    override val route = "readlater_graph"

    override val startRoute = ReadLaterScreenDestination

    override val showNavigation = listOf(
        ReadLaterScreenDestination,
        ReadLaterFolderScreenDestination
    ).map(DestinationSpec<out Any>::route)

    override val destinationsByRoute = listOf(
        ReadLaterScreenDestination,
        ReadLaterFolderScreenDestination
    ).associateBy(DestinationSpec<*>::route)

    override val transitions = listOf(
        TransitionsConfigure(
            ReadLaterScreenDestination.route,
            ReadLaterFolderScreenDestination.route,
            TransitionsConfigure.Type.SharedAxisX
        ),
        TransitionsConfigure(
            ReadLaterFolderScreenDestination.route,
            ReadLaterFolderScreenDestination.route,
            TransitionsConfigure.Type.SharedAxisX
        ),
        TransitionsConfigure(
            route,
            null,
            TransitionsConfigure.Type.ContainerTransform
        )
    )
}

fun DependenciesContainerBuilder<*>.dependencyReadLaterNavGraph(
    onBookClick: (Book) -> Unit,
    onFavoriteClick: (File) -> Unit,
    onSearchClick: (BookshelfId, String) -> Unit,
    onSettingsClick: () -> Unit,
) {
    dependency(ReadLaterNavGraph) {
        object : ReadLaterScreenNavigator, FolderScreenNavigator {
            override fun onFavoriteClick(file: File) = onFavoriteClick(file)
            override fun onSearchClick(bookshelfId: BookshelfId, path: String) =
                onSearchClick(bookshelfId, path)

            override fun onSettingsClick() = onSettingsClick()
            override fun onFileClick(file: File) {
                when (file) {
                    is Book -> onBookClick(file)
                    is Folder -> navController.navigate(
                        ReadLaterFolderScreenDestination(file.bookshelfId, file.path, null)
                    )
                }
            }

            override fun onOpenFolderClick(file: File) {
                navController.navigate(
                    ReadLaterFolderScreenDestination(file.bookshelfId, file.parent, null)
                )
            }

            override fun navigateUp() {
                navController.navigateUp()
            }
        }
    }
}
