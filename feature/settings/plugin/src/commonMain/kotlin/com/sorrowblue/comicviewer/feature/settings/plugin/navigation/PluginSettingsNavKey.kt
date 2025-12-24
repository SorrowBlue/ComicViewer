package com.sorrowblue.comicviewer.feature.settings.plugin.navigation

import com.sorrowblue.comicviewer.framework.ui.navigation.ScreenKey
import kotlinx.serialization.Serializable

@Serializable
data object PluginSettingsNavKey : ScreenKey

@Serializable
internal data object PdfPluginNavKey : ScreenKey
