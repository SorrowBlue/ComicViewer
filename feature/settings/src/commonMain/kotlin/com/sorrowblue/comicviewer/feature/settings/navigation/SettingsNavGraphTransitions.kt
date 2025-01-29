package com.sorrowblue.comicviewer.feature.settings.navigation

import com.sorrowblue.comicviewer.feature.authentication.Authentication
import com.sorrowblue.comicviewer.feature.settings.Settings
import com.sorrowblue.comicviewer.feature.tutorial.Tutorial
import com.sorrowblue.comicviewer.framework.ui.navigation.DestinationTransitions
import com.sorrowblue.comicviewer.framework.ui.navigation.TransitionsConfigure

internal object SettingsNavGraphTransitions : DestinationTransitions() {
    override val transitions: List<TransitionsConfigure> = listOf(
        TransitionsConfigure(
            Settings::class,
            Authentication::class,
            TransitionsConfigure.Type.SharedAxisY
        ),
        TransitionsConfigure(
            Settings::class,
            Tutorial::class,
            TransitionsConfigure.Type.SharedAxisY
        ),
        TransitionsConfigure(
            SettingsNavGraph::class,
            null,
            TransitionsConfigure.Type.SharedAxisX
        ),
    )
}
