package com.sorrowblue.comicviewer.feature.tutorial.navigation

import com.sorrowblue.comicviewer.feature.tutorial.NavGraphs
import com.sorrowblue.comicviewer.framework.ui.DestinationTransitions
import com.sorrowblue.comicviewer.framework.ui.TransitionsConfigure

internal object TutorialGraphTransitions : DestinationTransitions() {

    override val transitions: List<TransitionsConfigure> = listOf(
        TransitionsConfigure(
            NavGraphs.tutorial,
            null,
            TransitionsConfigure.Type.SharedAxisY
        )
    )
}
