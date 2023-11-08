package com.sorrowblue.comicviewer.domain.model.settings

import kotlinx.serialization.Serializable

@Serializable
data class ViewerSettings(
    val showStatusBar: Boolean = true,
    val showNavigationBar: Boolean = true,
    val keepOnScreen: Boolean = false,
    val enableBrightnessControl: Boolean = false,
    val screenBrightness: Float = 0.5f,
    val imageQuality: Int = 75,
    val readAheadPageCount: Int = 4,
    val bindingDirection: BindingDirection = BindingDirection.RIGHT,
) {

    enum class BindingDirection {
        RIGHT,
        LEFT,
    }
}
