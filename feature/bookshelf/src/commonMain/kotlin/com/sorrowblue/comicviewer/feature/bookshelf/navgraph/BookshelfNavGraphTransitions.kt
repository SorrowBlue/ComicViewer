package com.sorrowblue.comicviewer.feature.bookshelf.navgraph

import com.sorrowblue.comicviewer.feature.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.feature.bookshelf.BookshelfFolder
import com.sorrowblue.comicviewer.feature.bookshelf.edit.BookshelfEdit
import com.sorrowblue.comicviewer.feature.bookshelf.selection.BookshelfSelection
import com.sorrowblue.comicviewer.framework.ui.navigation.DestinationTransitions
import com.sorrowblue.comicviewer.framework.ui.navigation.TransitionsConfigure

internal object BookshelfNavGraphTransitions : DestinationTransitions() {
    override val transitions: List<TransitionsConfigure> = listOf(
        TransitionsConfigure(
            Bookshelf::class,
            BookshelfFolder::class,
            TransitionsConfigure.Type.SharedAxisX
        ),
        TransitionsConfigure(
            BookshelfFolder::class,
            BookshelfFolder::class,
            TransitionsConfigure.Type.SharedAxisX
        ),
        TransitionsConfigure(
            Bookshelf::class,
            BookshelfSelection::class,
            TransitionsConfigure.Type.SharedAxisY
        ),
        TransitionsConfigure(
            Bookshelf::class,
            BookshelfEdit::class,
            TransitionsConfigure.Type.SharedAxisY
        ),
        TransitionsConfigure(
            BookshelfSelection::class,
            BookshelfEdit::class,
            TransitionsConfigure.Type.SharedAxisX
        ),
        TransitionsConfigure(
            BookshelfNavGraph::class,
            null,
            TransitionsConfigure.Type.ContainerTransform
        )
    )
}
