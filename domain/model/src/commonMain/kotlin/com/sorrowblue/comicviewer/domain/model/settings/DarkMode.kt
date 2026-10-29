/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.model.settings

import kotlinx.serialization.Serializable

/**
 * Represents the application theme appearance mode.
 */
@Serializable
enum class DarkMode {
    /** Follow the system device theme setting. */
    DEVICE,

    /** Always use dark theme. */
    DARK,

    /** Always use light theme. */
    LIGHT,
}
