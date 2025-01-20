package com.sorrowblue.comicviewer.feature.readlater.navigation

import com.ramcosta.composedestinations.spec.DestinationSpec
import com.sorrowblue.comicviewer.feature.readlater.NavGraphs
import com.sorrowblue.comicviewer.feature.readlater.destinations.ReadLaterFolderScreenDestination
import com.sorrowblue.comicviewer.feature.readlater.destinations.ReadLaterScreenDestination
import com.sorrowblue.comicviewer.framework.ui.DestinationTransitions
import com.sorrowblue.comicviewer.framework.ui.TransitionsConfigure

internal object ReadLaterGraphTransitions : DestinationTransitions() {
    override val directionToDisplayNavigation: List<DestinationSpec> = listOf(
        ReadLaterScreenDestination,
        ReadLaterFolderScreenDestination
    )

    override val transitions = listOf(
        TransitionsConfigure(
            ReadLaterScreenDestination,
            ReadLaterFolderScreenDestination,
            TransitionsConfigure.Type.SharedAxisX
        ),
        TransitionsConfigure(
            ReadLaterFolderScreenDestination,
            ReadLaterFolderScreenDestination,
            TransitionsConfigure.Type.SharedAxisX
        ),
        TransitionsConfigure(
            NavGraphs.readLater,
            null,
            TransitionsConfigure.Type.ContainerTransform
        )
    )
}
