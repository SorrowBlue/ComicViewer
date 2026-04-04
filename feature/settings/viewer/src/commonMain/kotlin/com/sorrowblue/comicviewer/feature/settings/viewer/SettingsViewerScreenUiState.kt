package com.sorrowblue.comicviewer.feature.settings.viewer

internal data class SettingsViewerScreenUiState(
    val isStatusBarShow: Boolean = false,
    val isNavigationBarShow: Boolean = false,
    val isTurnOnScreen: Boolean = false,
    val isCutWhitespace: Boolean = false,
    val isCacheImage: Boolean = false,
    val isDisplayFirstPage: Boolean = false,
    val preloadPages: Float = 1f,
    val imageQuality: Float = 75f,
    val isFixScreenBrightness: Boolean = false,
    val screenBrightness: Float = 0.5f,
)
