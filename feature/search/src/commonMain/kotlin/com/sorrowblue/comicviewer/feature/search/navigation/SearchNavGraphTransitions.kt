package com.sorrowblue.comicviewer.feature.search.navigation

import com.sorrowblue.comicviewer.feature.search.Search
import com.sorrowblue.comicviewer.feature.search.SearchFolder
import com.sorrowblue.comicviewer.framework.ui.navigation.BetweenScreen
import com.sorrowblue.comicviewer.framework.ui.navigation.DestinationTransitions
import com.sorrowblue.comicviewer.framework.ui.navigation.GraphFrom
import com.sorrowblue.comicviewer.framework.ui.navigation.TransitionsConfigure

object SearchNavGraphTransitions : DestinationTransitions() {

    override val transitions = listOf(
        BetweenScreen(
            Search::class,
            SearchFolder::class,
            TransitionsConfigure.Type.SharedAxisX
        ),
        BetweenScreen(
            SearchFolder::class,
            SearchFolder::class,
            TransitionsConfigure.Type.SharedAxisX
        ),
        GraphFrom(
            null,
            SearchNavGraph::class,
            TransitionsConfigure.Type.SharedAxisZ
        ),
    )
}
