package com.sorrowblue.comicviewer.feature.book.navigation

import com.sorrowblue.comicviewer.feature.book.NavGraphs
import com.sorrowblue.comicviewer.framework.ui.DestinationTransitions
import com.sorrowblue.comicviewer.framework.ui.TransitionsConfigure

internal object BookGraphTransitions : DestinationTransitions() {

    override val transitions = listOf(
        TransitionsConfigure(
            NavGraphs.book,
            null,
            TransitionsConfigure.Type.ContainerTransform
        )
    )
}
