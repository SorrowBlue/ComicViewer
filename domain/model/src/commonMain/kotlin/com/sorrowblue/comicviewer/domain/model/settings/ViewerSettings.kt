package com.sorrowblue.comicviewer.domain.model.settings

import com.sorrowblue.comicviewer.domain.model.settings.folder.ImageFormat
import kotlinx.serialization.Serializable

@Serializable
data class ViewerSettings(
    val showStatusBar: Boolean = true,
    val showNavigationBar: Boolean = true,
    val keepOnScreen: Boolean = false,
    val cutWhitespace: Boolean = false,
    val enableBrightnessControl: Boolean = false,
    val screenBrightness: Float = 0.3f,
    val imageQuality: Int = 75,
    val readAheadPageCount: Int = 3,
    val bindingDirection: BindingDirection = BindingDirection.RTL,
    val alwaysOpenFromFirstPage: Boolean = false,
    val imageFormat: ImageFormat = ImageFormat.ORIGINAL,
)
