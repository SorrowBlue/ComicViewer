package com.sorrowblue.comicviewer.feature.history.navigation

import com.sorrowblue.comicviewer.feature.history.History
import com.sorrowblue.comicviewer.feature.history.HistoryFolder
import com.sorrowblue.comicviewer.framework.ui.navigation.BetweenScreen
import com.sorrowblue.comicviewer.framework.ui.navigation.DestinationTransitions
import com.sorrowblue.comicviewer.framework.ui.navigation.GraphFrom
import com.sorrowblue.comicviewer.framework.ui.navigation.TransitionsConfigure

object HistoryNavGraphTransitions : DestinationTransitions() {
    override val transitions = listOf(
        BetweenScreen(
            History::class,
            HistoryFolder::class,
            TransitionsConfigure.Type.SharedAxisX
        ),
        BetweenScreen(
            HistoryFolder::class,
            HistoryFolder::class,
            TransitionsConfigure.Type.SharedAxisX
        ),
        GraphFrom(
            null,
            HistoryNavGraph::class,
            TransitionsConfigure.Type.ContainerTransform
        ),
    )
}
