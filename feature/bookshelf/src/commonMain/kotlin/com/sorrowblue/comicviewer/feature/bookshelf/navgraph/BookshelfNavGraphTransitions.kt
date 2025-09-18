package com.sorrowblue.comicviewer.feature.bookshelf.navgraph

import com.sorrowblue.comicviewer.feature.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.feature.bookshelf.BookshelfFolder
import com.sorrowblue.comicviewer.feature.bookshelf.edit.navigation.BookshelfEditNavGraph
import com.sorrowblue.comicviewer.feature.bookshelf.selection.BookshelfSelection
import com.sorrowblue.comicviewer.framework.ui.navigation.DestinationTransitions
import com.sorrowblue.comicviewer.framework.ui.navigation.BetweenScreen
import com.sorrowblue.comicviewer.framework.ui.navigation.GraphFrom
import com.sorrowblue.comicviewer.framework.ui.navigation.TransitionsConfigure

object BookshelfNavGraphTransitions : DestinationTransitions() {
    override val transitions = listOf(
        BetweenScreen(
            Bookshelf::class,
            BookshelfFolder::class,
            TransitionsConfigure.Type.SharedAxisX
        ),
        BetweenScreen(
            BookshelfFolder::class,
            BookshelfFolder::class,
            TransitionsConfigure.Type.SharedAxisX
        ),
        BetweenScreen(
            Bookshelf::class,
            BookshelfSelection::class,
            TransitionsConfigure.Type.SharedAxisZ
        ),
        GraphFrom(
            Bookshelf::class,
            BookshelfEditNavGraph::class,
            TransitionsConfigure.Type.SharedAxisZ
        ),
        GraphFrom(
            BookshelfSelection::class,
            BookshelfEditNavGraph::class,
            TransitionsConfigure.Type.SharedAxisX
        ),
        GraphFrom(
            null,
            BookshelfNavGraph::class,
            TransitionsConfigure.Type.ContainerTransform
        )
    )
}
