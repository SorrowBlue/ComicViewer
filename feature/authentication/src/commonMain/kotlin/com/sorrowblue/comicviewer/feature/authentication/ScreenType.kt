package com.sorrowblue.comicviewer.feature.authentication

import kotlinx.serialization.Serializable

@Serializable
enum class ScreenType {
    Register,
    Change,
    Erase,
    Authenticate,
}
