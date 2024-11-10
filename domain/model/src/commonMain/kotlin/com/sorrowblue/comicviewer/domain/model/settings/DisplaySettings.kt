package com.sorrowblue.comicviewer.domain.model.settings

import kotlinx.serialization.Serializable

@Serializable
data class DisplaySettings(
    val darkMode: DarkMode = DarkMode.DEVICE,
    val restoreOnLaunch: Boolean = false,
)
