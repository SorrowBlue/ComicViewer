/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.repository

import com.sorrowblue.comicviewer.domain.model.settings.WindowSettings
import kotlinx.coroutines.flow.Flow

/**
 * JVM Settings repository
 */
interface JvmSettingsRepository {

    /**
     * Window settings flow
     */
    val windowSettings: Flow<WindowSettings>

    /**
     * Update window settings
     *
     * @param transform Transform function
     * @return Updated window settings
     */
    suspend fun updateWindowSettings(
        transform: suspend (WindowSettings) -> WindowSettings,
    ): WindowSettings
}
