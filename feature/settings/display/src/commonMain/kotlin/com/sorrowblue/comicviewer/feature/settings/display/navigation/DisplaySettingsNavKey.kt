package com.sorrowblue.comicviewer.feature.settings.display.navigation

import com.sorrowblue.comicviewer.framework.ui.navigation.ScreenKey
import kotlinx.serialization.Serializable

@Serializable
data object DisplaySettingsNavKey : ScreenKey

@Serializable
internal data object DarkModeNavKey : ScreenKey
