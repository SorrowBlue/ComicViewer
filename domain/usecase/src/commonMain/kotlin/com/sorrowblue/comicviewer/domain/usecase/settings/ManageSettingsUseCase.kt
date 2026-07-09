/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.settings

import kotlinx.coroutines.flow.Flow

interface ManageSettingsUseCase<T> {
    val settings: Flow<T>

    suspend fun edit(action: (T) -> T)
}
