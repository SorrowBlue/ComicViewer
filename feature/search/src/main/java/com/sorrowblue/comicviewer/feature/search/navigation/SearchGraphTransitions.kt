package com.sorrowblue.comicviewer.feature.search.navigation

import com.sorrowblue.comicviewer.feature.search.NavGraphs
import com.sorrowblue.comicviewer.feature.search.destinations.SearchFolderScreenDestination
import com.sorrowblue.comicviewer.feature.search.destinations.SearchScreenDestination
import com.sorrowblue.comicviewer.framework.ui.DestinationTransitions
import com.sorrowblue.comicviewer.framework.ui.TransitionsConfigure

internal object SearchGraphTransitions : DestinationTransitions() {

    override val transitions: List<TransitionsConfigure> = listOf(
        TransitionsConfigure(
            SearchScreenDestination,
            SearchFolderScreenDestination,
            TransitionsConfigure.Type.SharedAxisX
        ),
        TransitionsConfigure(
            SearchFolderScreenDestination,
            SearchFolderScreenDestination,
            TransitionsConfigure.Type.SharedAxisX
        ),
        TransitionsConfigure(
            NavGraphs.search,
            null,
            TransitionsConfigure.Type.ContainerTransform
        )
    )
}
