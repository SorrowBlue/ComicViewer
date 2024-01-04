package com.sorrowblue.comicviewer.feature.readlater.navigation

import androidx.navigation.NavController
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.scope.DestinationScopeWithNoDependencies
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

fun DestinationScopeWithNoDependencies<*>.readLaterNavGraphNavigator(navigator: ReadLaterNavGraphNavigator) =
    ReadLaterNavGraphNavigatorImpl(navigator, navController)

interface ReadLaterNavGraphNavigator {
    fun navigateToBook(book: Book)
    fun onFavoriteClick(file: File)
    fun onSearchClick(bookshelfId: BookshelfId, path: String)
    fun onSettingsClick()
}

class ReadLaterNavGraphNavigatorImpl internal constructor(
    navigator: ReadLaterNavGraphNavigator,
    private val navController: NavController,
) : ReadLaterScreenNavigator,
    FolderScreenNavigator,
    ReadLaterNavGraphNavigator by navigator {
    override fun onFileClick(file: File) {
        when (file) {
            is Book -> navigateToBook(file)
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

val RouteInReadlaterGraph = listOf(
    ReadLaterScreenDestination.route,
    ReadLaterFolderScreenDestination.route
)
