package com.sorrowblue.comicviewer.feature.settings.navigation

import com.sorrowblue.comicviewer.feature.authentication.Authentication
import com.sorrowblue.comicviewer.feature.settings.Settings
import com.sorrowblue.comicviewer.feature.settings.security.SecuritySettings
import com.sorrowblue.comicviewer.feature.tutorial.Tutorial
import com.sorrowblue.comicviewer.framework.ui.navigation.BetweenScreen
import com.sorrowblue.comicviewer.framework.ui.navigation.DestinationTransitions
import com.sorrowblue.comicviewer.framework.ui.navigation.GraphFrom
import com.sorrowblue.comicviewer.framework.ui.navigation.TransitionsConfigure

object SettingsNavGraphTransitions : DestinationTransitions() {
    override val transitions = listOf(
        BetweenScreen(
            SecuritySettings::class,
            Authentication::class,
            TransitionsConfigure.Type.SharedAxisZ
        ),
        BetweenScreen(
            Settings::class,
            Tutorial::class,
            TransitionsConfigure.Type.SharedAxisZ
        ),
        GraphFrom(
            null,
            SettingsNavGraph::class,
            TransitionsConfigure.Type.SharedAxisZ
        ),
    )
}
