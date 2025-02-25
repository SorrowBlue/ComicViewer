package com.sorrowblue.comicviewer.feature.settings.navigation

import com.sorrowblue.comicviewer.feature.authentication.Authentication
import com.sorrowblue.comicviewer.feature.settings.Settings
import com.sorrowblue.comicviewer.feature.tutorial.Tutorial
import com.sorrowblue.cmpdestinations.annotation.DestinationInGraph
import com.sorrowblue.cmpdestinations.annotation.NavGraph
import kotlinx.serialization.Serializable

@Serializable
@NavGraph(startDestination = Settings::class, transitions = SettingsNavGraphTransitions::class)
data object SettingsNavGraph {

    @DestinationInGraph<Authentication>
    @DestinationInGraph<Settings>
    @DestinationInGraph<Tutorial>
    object Include
}
