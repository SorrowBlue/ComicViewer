package com.sorrowblue.comicviewer.feature.settings.navigation

import com.sorrowblue.comicviewer.feature.settings.NavGraphs
import com.sorrowblue.comicviewer.framework.ui.DestinationTransitions
import com.sorrowblue.comicviewer.framework.ui.TransitionsConfigure

internal object SettingsGraphTransitions : DestinationTransitions() {

    override val transitions = listOf(
        TransitionsConfigure(
            NavGraphs.settings,
            null,
            TransitionsConfigure.Type.ContainerTransform
        )
    )
}
