/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.service.datasource

import com.sorrowblue.comicviewer.domain.model.settings.WindowSettings
import kotlinx.coroutines.flow.Flow

/**
 * JVM Datastore data source
 */
interface JvmDatastoreDataSource {

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
