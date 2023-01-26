package com.sorrowblue.comicviewer.domain.entity.settings

import kotlinx.serialization.Serializable

@Serializable
data class DisplaySettings(
    val darkMode: DarkMode = DarkMode.DEVICE,
    val folderThumbnailOrder: FolderThumbnailOrder = FolderThumbnailOrder.NAME
)

