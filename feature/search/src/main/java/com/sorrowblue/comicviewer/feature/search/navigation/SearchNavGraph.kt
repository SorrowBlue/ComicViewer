package com.sorrowblue.comicviewer.feature.search.navigation

import androidx.navigation.NavController
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.scope.DestinationScopeWithNoDependencies
import com.ramcosta.composedestinations.spec.DestinationSpec
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.feature.search.SearchScreenNavigator
import com.sorrowblue.comicviewer.feature.search.destinations.SearchFolderScreenDestination
import com.sorrowblue.comicviewer.feature.search.destinations.SearchScreenDestination
import com.sorrowblue.comicviewer.folder.FolderScreenNavigator
import com.sorrowblue.comicviewer.framework.ui.AnimatedNavGraphSpec
import com.sorrowblue.comicviewer.framework.ui.TransitionsConfigure

object SearchNavGraph : AnimatedNavGraphSpec {
    override val route = "search_graph"
    override val startRoute = SearchScreenDestination
    override val destinationsByRoute = listOf(
        SearchScreenDestination,
        SearchFolderScreenDestination
    ).associateBy(DestinationSpec<*>::route)
    override val transitions = listOf(
        TransitionsConfigure(
            SearchScreenDestination.route,
            SearchFolderScreenDestination.route,
            TransitionsConfigure.Type.SharedAxisX
        ),
        TransitionsConfigure(
            SearchFolderScreenDestination.route,
            SearchFolderScreenDestination.route,
            TransitionsConfigure.Type.SharedAxisX
        ),
        TransitionsConfigure(
            route,
            null,
            TransitionsConfigure.Type.ContainerTransform
        )
    )
}

fun DestinationScopeWithNoDependencies<*>.searchNavGraphNavigator(navigator: SearchNavGraphNavigator) =
    SearchNavGraphNavigatorImpl(navigator, navController)

interface SearchNavGraphNavigator {
    fun navigateToBook(book: Book)
    fun onFavoriteClick(file: File)
    fun onSettingsClick()
    fun onSearchClick(bookshelfId: BookshelfId, path: String)
}

class SearchNavGraphNavigatorImpl internal constructor(
    navigator: SearchNavGraphNavigator,
    private val navController: NavController,
) : SearchScreenNavigator,
    FolderScreenNavigator,
    SearchNavGraphNavigator by navigator {
    override fun onFileClick(file: File) {
        when (file) {
            is Book -> navigateToBook(file)
            is Folder -> navController.navigate(
                SearchFolderScreenDestination(file.bookshelfId, file.path, null)
            )
        }
    }

    override fun onOpenFolderClick(file: File) {
        navController.navigate(
            SearchFolderScreenDestination(file.bookshelfId, file.parent, null)
        )
    }

    override fun navigateUp() {
        navController.navigateUp()
    }
}
