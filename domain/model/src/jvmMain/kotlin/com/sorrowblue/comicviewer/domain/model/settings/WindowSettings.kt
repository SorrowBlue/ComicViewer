/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.model.settings

import kotlinx.serialization.Serializable

/**
 * Window settings
 *
 * @property width Window width
 * @property height Window height
 * @property x Window x position
 * @property y Window y position
 * @property isMaximized Whether the window is maximized
 */
@Serializable
data class WindowSettings(
    val width: Int = 1024,
    val height: Int = 768,
    val x: Int = -1,
    val y: Int = -1,
    val isMaximized: Boolean = false,
)
