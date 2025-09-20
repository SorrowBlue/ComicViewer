package com.sorrowblue.comicviewer.feature.readlater.navigation

import com.sorrowblue.comicviewer.feature.readlater.ReadLater
import com.sorrowblue.comicviewer.feature.readlater.ReadLaterFolder
import com.sorrowblue.comicviewer.framework.ui.navigation.BetweenScreen
import com.sorrowblue.comicviewer.framework.ui.navigation.DestinationTransitions
import com.sorrowblue.comicviewer.framework.ui.navigation.GraphFrom
import com.sorrowblue.comicviewer.framework.ui.navigation.TransitionsConfigure

object ReadLaterNavGraphTransitions : DestinationTransitions() {
    override val transitions = listOf(
        BetweenScreen(
            ReadLater::class,
            ReadLaterFolder::class,
            TransitionsConfigure.Type.SharedAxisX
        ),
        BetweenScreen(
            ReadLaterFolder::class,
            ReadLaterFolder::class,
            TransitionsConfigure.Type.SharedAxisX
        ),
        GraphFrom(
            null,
            ReadLaterNavGraph::class,
            TransitionsConfigure.Type.ContainerTransform
        ),
    )
}
