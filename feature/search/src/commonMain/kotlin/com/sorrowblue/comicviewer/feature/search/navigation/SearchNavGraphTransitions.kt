package com.sorrowblue.comicviewer.feature.search.navigation

import com.sorrowblue.comicviewer.feature.search.Search
import com.sorrowblue.comicviewer.feature.search.SearchFolder
import com.sorrowblue.comicviewer.framework.ui.navigation.DestinationTransitions
import com.sorrowblue.comicviewer.framework.ui.navigation.TransitionsConfigure

internal object SearchNavGraphTransitions : DestinationTransitions() {

    override val transitions = listOf(
        TransitionsConfigure(
            SearchNavGraph::class,
            null,
            TransitionsConfigure.Type.ContainerTransform
        ),
        TransitionsConfigure(
            Search::class,
            SearchFolder::class,
            TransitionsConfigure.Type.SharedAxisX
        ),
        TransitionsConfigure(
            SearchFolder::class,
            SearchFolder::class,
            TransitionsConfigure.Type.SharedAxisX
        )
    )
}
