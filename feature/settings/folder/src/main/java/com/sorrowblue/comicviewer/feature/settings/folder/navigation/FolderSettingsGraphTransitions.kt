package com.sorrowblue.comicviewer.feature.settings.folder.navigation

import com.sorrowblue.comicviewer.feature.settings.folder.destinations.FolderSettingsScreenDestination
import com.sorrowblue.comicviewer.feature.settings.folder.destinations.SupportExtensionScreenDestination
import com.sorrowblue.comicviewer.framework.ui.DestinationTransitions
import com.sorrowblue.comicviewer.framework.ui.TransitionsConfigure

internal object FolderSettingsGraphTransitions : DestinationTransitions() {

    override val transitions = listOf(
        TransitionsConfigure(
            FolderSettingsScreenDestination,
            SupportExtensionScreenDestination,
            TransitionsConfigure.Type.SharedAxisX
        )
    )
}
