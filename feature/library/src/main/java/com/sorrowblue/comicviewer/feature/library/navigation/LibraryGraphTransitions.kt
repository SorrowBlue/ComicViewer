package com.sorrowblue.comicviewer.feature.library.navigation

import com.ramcosta.composedestinations.spec.DestinationSpec
import com.sorrowblue.comicviewer.feature.history.destinations.HistoryScreenDestination
import com.sorrowblue.comicviewer.feature.library.destinations.LibraryScreenDestination
import com.sorrowblue.comicviewer.feature.library.serviceloader.BoxNavGraph
import com.sorrowblue.comicviewer.feature.library.serviceloader.DropBoxNavGraph
import com.sorrowblue.comicviewer.feature.library.serviceloader.GoogleDriveNavGraph
import com.sorrowblue.comicviewer.feature.library.serviceloader.OneDriveNavGraph
import com.sorrowblue.comicviewer.framework.ui.DestinationTransitions
import com.sorrowblue.comicviewer.framework.ui.TransitionsConfigure

internal object LibraryGraphTransitions : DestinationTransitions() {
    override val directionToDisplayNavigation = listOf<DestinationSpec>(
        LibraryScreenDestination,
        HistoryScreenDestination
    )
    override val transitions
        get() = listOf(
            TransitionsConfigure(
                LibraryScreenDestination,
                HistoryScreenDestination,
                TransitionsConfigure.Type.SharedAxisX
            ),
            TransitionsConfigure(
                LibraryScreenDestination,
                BoxNavGraph()?.navGraph,
                TransitionsConfigure.Type.SharedAxisX
            ),
            TransitionsConfigure(
                LibraryScreenDestination,
                DropBoxNavGraph()?.navGraph,
                TransitionsConfigure.Type.SharedAxisX
            ),
            TransitionsConfigure(
                LibraryScreenDestination,
                OneDriveNavGraph()?.navGraph,
                TransitionsConfigure.Type.SharedAxisX
            ),
            TransitionsConfigure(
                LibraryScreenDestination,
                GoogleDriveNavGraph()?.navGraph,
                TransitionsConfigure.Type.SharedAxisX
            ),
            TransitionsConfigure(
                LibraryNavGraph,
                null,
                TransitionsConfigure.Type.ContainerTransform
            )
        )
}
