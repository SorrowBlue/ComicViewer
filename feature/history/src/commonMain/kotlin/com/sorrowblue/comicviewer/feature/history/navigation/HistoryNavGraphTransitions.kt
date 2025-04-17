package com.sorrowblue.comicviewer.feature.history.navigation

import com.sorrowblue.comicviewer.feature.history.History
import com.sorrowblue.comicviewer.feature.history.HistoryFolder
import com.sorrowblue.comicviewer.framework.ui.navigation.DestinationTransitions
import com.sorrowblue.comicviewer.framework.ui.navigation.TransitionsConfigure

internal object HistoryNavGraphTransitions : DestinationTransitions() {
    override val transitions = listOf(
        TransitionsConfigure(
            HistoryNavGraph::class,
            null,
            TransitionsConfigure.Type.ContainerTransform
        ),
        TransitionsConfigure(
            History::class,
            HistoryFolder::class,
            TransitionsConfigure.Type.SharedAxisX
        ),
        TransitionsConfigure(
            HistoryFolder::class,
            HistoryFolder::class,
            TransitionsConfigure.Type.SharedAxisX
        ),
    )
}
