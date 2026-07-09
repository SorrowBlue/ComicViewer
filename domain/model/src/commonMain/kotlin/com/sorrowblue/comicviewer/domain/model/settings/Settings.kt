/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.model.settings

import kotlinx.serialization.Serializable

@Serializable
data class Settings(
    val doneTutorial: Boolean = false,
    val useAuth: Boolean = false,
    val restoreOnLaunch: Boolean = false,
)
