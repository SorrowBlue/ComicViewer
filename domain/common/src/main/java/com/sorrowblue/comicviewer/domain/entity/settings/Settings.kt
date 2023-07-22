package com.sorrowblue.comicviewer.domain.entity.settings

import kotlinx.serialization.Serializable

@Serializable
data class Settings(
    val doneTutorial: Boolean = false,
    val useAuth: Boolean = false,
    val restoreOnLaunch: Boolean = false
)
