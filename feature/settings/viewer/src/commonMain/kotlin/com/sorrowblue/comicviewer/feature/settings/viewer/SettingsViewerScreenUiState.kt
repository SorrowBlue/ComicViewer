package com.sorrowblue.comicviewer.feature.settings.viewer

import com.sorrowblue.comicviewer.domain.model.settings.BindingDirection
import com.sorrowblue.comicviewer.domain.model.settings.folder.ImageFormat

internal data class SettingsViewerScreenUiState(
    val isStatusBarShow: Boolean = false,
    val isNavigationBarShow: Boolean = false,
    val isTurnOnScreen: Boolean = false,
    val isCutWhitespace: Boolean = false,
    val isDisplayFirstPage: Boolean = false,
    val preloadPages: Float = 1f,
    val imageQuality: Float = 75f,
    val isFixScreenBrightness: Boolean = false,
    val screenBrightness: Float = 0.5f,
    val screenBrightnessPreview: Boolean = false,
    val screenBrightnessPreviewTime: Int = 0,
    val bindingDirection: BindingDirection = BindingDirection.RTL,
    val imageFormat: ImageFormat = ImageFormat.ORIGINAL,
)
