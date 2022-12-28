package com.sorrowblue.comicviewer.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ViewerSettings(
    val showStatusBar: Boolean = true,
    val showNavigationBar: Boolean = true,
    val keepOnScreen: Boolean = false,
    val enableBrightnessControl: Boolean = false,
    val screenBrightness: Float = 0.5f,
    val imageQuality: Int = 75,
    val readAheadPageCount: Int = 2
)

@Serializable
data class DisplaySettings(
    val darkMode: DarkMode = DarkMode.DEVICE,
    val folderThumbnailOrder: FolderThumbnailOrder = FolderThumbnailOrder.NAME
)

enum class FolderThumbnailOrder {
    NAME,
    MODIFIED,
    LAST_READ
}

@Serializable
data class ViewerOperationSettings(
    // 閉じ方向
    val bindingDirection: BindingDirection = BindingDirection.RTL,

    )

enum class BindingDirection {
    LTR, RTL
}

enum class DarkMode {
    DEVICE,
    DARK,
    LIGHT
}
