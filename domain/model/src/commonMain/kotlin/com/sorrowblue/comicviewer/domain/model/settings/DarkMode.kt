package com.sorrowblue.comicviewer.domain.model.settings

import kotlinx.serialization.Serializable

@Serializable
enum class DarkMode {
    DEVICE,
    DARK,
    LIGHT,
}
