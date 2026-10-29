/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.settings

import kotlinx.coroutines.flow.Flow

/**
 * Base interface for use cases that observe and update settings of type [T].
 *
 * @param T The settings model type.
 */
interface ManageSettingsUseCase<T> {
    /**
     * A [Flow] emitting the current settings value whenever it changes.
     */
    val settings: Flow<T>

    /**
     * Updates the current settings by applying the given transformation [action].
     *
     * @param action Transformation function receiving the current settings and returning the
     * updated settings.
     */
    suspend fun edit(action: (T) -> T)
}
