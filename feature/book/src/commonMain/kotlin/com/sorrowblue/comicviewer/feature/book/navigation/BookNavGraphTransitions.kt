package com.sorrowblue.comicviewer.feature.book.navigation

import com.sorrowblue.comicviewer.framework.ui.navigation.DestinationTransitions
import com.sorrowblue.comicviewer.framework.ui.navigation.GraphFrom
import com.sorrowblue.comicviewer.framework.ui.navigation.TransitionsConfigure

object BookNavGraphTransitions : DestinationTransitions() {

    override val transitions = listOf(
        GraphFrom(
            null,
            BookNavGraph::class,
            TransitionsConfigure.Type.SharedAxisZ
        )
    )
}
