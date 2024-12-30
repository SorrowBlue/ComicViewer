package com.sorrowblue.comicviewer.feature.bookshelf.navigation

import com.ramcosta.composedestinations.spec.DestinationSpec
import com.sorrowblue.comicviewer.feature.bookshelf.NavGraphs
import com.sorrowblue.comicviewer.feature.bookshelf.destinations.BookshelfFolderScreenDestination
import com.sorrowblue.comicviewer.feature.bookshelf.destinations.BookshelfScreenDestination
import com.sorrowblue.comicviewer.feature.bookshelf.edit.destinations.BookshelfEditScreenDestination
import com.sorrowblue.comicviewer.feature.bookshelf.selection.destinations.BookshelfSelectionScreenDestination
import com.sorrowblue.comicviewer.framework.ui.AnimatedOrDialog
import com.sorrowblue.comicviewer.framework.ui.DestinationTransitions
import com.sorrowblue.comicviewer.framework.ui.TransitionsConfigure

internal object BookshelfGraphTransitions : DestinationTransitions() {

    override val directionToDisplayNavigation: List<DestinationSpec> = listOf(
        BookshelfScreenDestination,
        BookshelfFolderScreenDestination
    )

    override val transitions = listOf(
        TransitionsConfigure(
            BookshelfScreenDestination,
            BookshelfFolderScreenDestination,
            TransitionsConfigure.Type.SharedAxisX
        ),
        TransitionsConfigure(
            BookshelfFolderScreenDestination,
            BookshelfFolderScreenDestination,
            TransitionsConfigure.Type.SharedAxisX
        ),
        TransitionsConfigure(
            BookshelfScreenDestination,
            BookshelfSelectionScreenDestination,
            TransitionsConfigure.Type.SharedAxisY
        ),
        TransitionsConfigure(
            BookshelfScreenDestination,
            BookshelfEditScreenDestination,
            TransitionsConfigure.Type.SharedAxisY
        ),
        TransitionsConfigure(
            BookshelfSelectionScreenDestination,
            BookshelfEditScreenDestination,
            TransitionsConfigure.Type.SharedAxisX
        ),
        TransitionsConfigure(
            NavGraphs.bookshelf,
            null,
            TransitionsConfigure.Type.ContainerTransform
        )
    )
}

internal object BookshelfGraphTransitionsWithDialog : AnimatedOrDialog() {

    override val directionToDisplayNavigation: List<DestinationSpec> = listOf(
        BookshelfScreenDestination,
        BookshelfFolderScreenDestination
    )

    override val transitions = listOf(
        TransitionsConfigure(
            BookshelfScreenDestination,
            BookshelfFolderScreenDestination,
            TransitionsConfigure.Type.SharedAxisX
        ),
        TransitionsConfigure(
            BookshelfFolderScreenDestination,
            BookshelfFolderScreenDestination,
            TransitionsConfigure.Type.SharedAxisX
        ),
        TransitionsConfigure(
            BookshelfScreenDestination,
            BookshelfSelectionScreenDestination,
            TransitionsConfigure.Type.SharedAxisY
        ),
        TransitionsConfigure(
            BookshelfScreenDestination,
            BookshelfEditScreenDestination,
            TransitionsConfigure.Type.SharedAxisY
        ),
        TransitionsConfigure(
            BookshelfSelectionScreenDestination,
            BookshelfEditScreenDestination,
            TransitionsConfigure.Type.SharedAxisX
        ),
        TransitionsConfigure(
            NavGraphs.bookshelf,
            null,
            TransitionsConfigure.Type.ContainerTransform
        )
    )
}
