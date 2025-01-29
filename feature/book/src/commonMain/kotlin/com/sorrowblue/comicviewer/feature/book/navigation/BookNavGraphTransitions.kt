package com.sorrowblue.comicviewer.feature.book.navigation

import com.sorrowblue.comicviewer.framework.ui.navigation.DestinationTransitions
import com.sorrowblue.comicviewer.framework.ui.navigation.TransitionsConfigure

internal object BookNavGraphTransitions : DestinationTransitions() {

    override val transitions = listOf(
        TransitionsConfigure(
            BookNavGraph::class,
            null,
            TransitionsConfigure.Type.ContainerTransform
        )
    )
}
