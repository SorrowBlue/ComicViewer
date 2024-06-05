package com.sorrowblue.comicviewer.feature.settings.info.navigation

import com.sorrowblue.comicviewer.feature.settings.info.destinations.AppInfoSettingsScreenDestination
import com.sorrowblue.comicviewer.feature.settings.info.destinations.LicenseScreenDestination
import com.sorrowblue.comicviewer.framework.ui.DestinationTransitions
import com.sorrowblue.comicviewer.framework.ui.TransitionsConfigure

internal object AppInfoSettingsGraphTransitions : DestinationTransitions() {

    override val transitions = listOf(
        TransitionsConfigure(
            AppInfoSettingsScreenDestination,
            LicenseScreenDestination,
            TransitionsConfigure.Type.SharedAxisX
        )
    )
}
