package com.sorrowblue.comicviewer.feature.readlater.navigation

import com.sorrowblue.comicviewer.feature.readlater.ReadLater
import com.sorrowblue.comicviewer.feature.readlater.ReadLaterFolder
import com.sorrowblue.comicviewer.framework.ui.navigation.DestinationTransitions
import com.sorrowblue.comicviewer.framework.ui.navigation.TransitionsConfigure

internal object ReadLaterNavGraphTransitions : DestinationTransitions() {
    override val transitions = listOf(
        TransitionsConfigure(
            ReadLaterNavGraph::class,
            null,
            TransitionsConfigure.Type.ContainerTransform
        ),
        TransitionsConfigure(
            ReadLater::class,
            ReadLaterFolder::class,
            TransitionsConfigure.Type.SharedAxisX
        ),
        TransitionsConfigure(
            ReadLaterFolder::class,
            ReadLaterFolder::class,
            TransitionsConfigure.Type.SharedAxisX
        )
    )
}
