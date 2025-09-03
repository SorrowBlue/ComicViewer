package com.sorrowblue.comicviewer.feature.settings.navigation

import com.sorrowblue.cmpdestinations.annotation.NavGraph
import com.sorrowblue.comicviewer.feature.settings.Settings
import com.sorrowblue.comicviewer.feature.tutorial.Tutorial
import kotlinx.serialization.Serializable

@Serializable
@NavGraph(
    startDestination = Settings::class,
    transitions = SettingsNavGraphTransitions::class,
    destinations = [Settings::class, Tutorial::class]
)
data object SettingsNavGraph
