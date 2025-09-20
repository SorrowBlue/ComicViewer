package com.sorrowblue.comicviewer.feature.settings.info.navigation

import com.sorrowblue.comicviewer.feature.settings.info.AppInfoSettings
import com.sorrowblue.comicviewer.feature.settings.info.license.License
import com.sorrowblue.comicviewer.framework.ui.navigation.BetweenScreen
import com.sorrowblue.comicviewer.framework.ui.navigation.DestinationTransitions
import com.sorrowblue.comicviewer.framework.ui.navigation.TransitionsConfigure

internal object AppInfoSettingsGraphTransitions : DestinationTransitions() {

    override val transitions = listOf(
        BetweenScreen(
            AppInfoSettings::class,
            License::class,
            TransitionsConfigure.Type.SharedAxisX
        )
    )
}
