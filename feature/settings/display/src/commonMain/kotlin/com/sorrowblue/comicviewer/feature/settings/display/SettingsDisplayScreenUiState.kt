package com.sorrowblue.comicviewer.feature.settings.display

import com.sorrowblue.comicviewer.domain.model.settings.DarkMode

internal data class SettingsDisplayScreenUiState(
    val darkMode: DarkMode = DarkMode.DEVICE,
    val restoreOnLaunch: Boolean = false,
)
