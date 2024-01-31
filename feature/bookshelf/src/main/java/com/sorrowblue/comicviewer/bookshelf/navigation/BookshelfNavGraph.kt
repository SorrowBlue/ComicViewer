package com.sorrowblue.comicviewer.bookshelf.navigation

import com.ramcosta.composedestinations.spec.DestinationSpec
import com.ramcosta.composedestinations.spec.Route
import com.sorrowblue.comicviewer.bookshelf.destinations.BookshelfFolderScreenDestination
import com.sorrowblue.comicviewer.bookshelf.destinations.BookshelfScreenDestination
import com.sorrowblue.comicviewer.feature.bookshelf.edit.destinations.BookshelfEditScreenDestination
import com.sorrowblue.comicviewer.feature.bookshelf.selection.destinations.BookshelfSelectionScreenDestination
import com.sorrowblue.comicviewer.framework.ui.AnimatedNavGraphSpec
import com.sorrowblue.comicviewer.framework.ui.TransitionsConfigure

object BookshelfNavGraph : AnimatedNavGraphSpec {
    override val route = "bookshelf_graph"
    override val startRoute: Route = BookshelfScreenDestination
    override val destinationsByRoute: Map<String, DestinationSpec<*>> = listOf(
        BookshelfScreenDestination,
        BookshelfSelectionScreenDestination,
        BookshelfEditScreenDestination,
        BookshelfFolderScreenDestination
    ).associateBy { it.route }

    override val transitions: List<TransitionsConfigure> = listOf(
        TransitionsConfigure(
            BookshelfScreenDestination.route,
            BookshelfFolderScreenDestination.route,
            TransitionsConfigure.Type.SharedAxisX
        ),
        TransitionsConfigure(
            BookshelfFolderScreenDestination.route,
            BookshelfFolderScreenDestination.route,
            TransitionsConfigure.Type.SharedAxisX
        ),
        TransitionsConfigure(
            BookshelfScreenDestination.route,
            BookshelfSelectionScreenDestination.route,
            TransitionsConfigure.Type.SharedAxisY
        ),
        TransitionsConfigure(
            BookshelfScreenDestination.route,
            BookshelfEditScreenDestination.route,
            TransitionsConfigure.Type.SharedAxisY
        ),
        TransitionsConfigure(
            BookshelfSelectionScreenDestination.route,
            BookshelfEditScreenDestination.route,
            TransitionsConfigure.Type.SharedAxisX
        ),
        TransitionsConfigure(
            route,
            null,
            TransitionsConfigure.Type.ContainerTransform
        )
    )
}
