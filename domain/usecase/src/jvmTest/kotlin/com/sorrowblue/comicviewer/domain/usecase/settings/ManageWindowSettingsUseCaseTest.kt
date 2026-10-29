/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.settings

import com.sorrowblue.comicviewer.domain.model.settings.WindowSettings
import com.sorrowblue.comicviewer.domain.repository.JvmSettingsRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest

class ManageWindowSettingsUseCaseTest {

    @Test
    fun manageWindowSettingsUseCase_observesAndUpdatesWindowSettings() = runTest {
        val state = MutableStateFlow(WindowSettings(width = 1024, height = 768))
        val repository = object : JvmSettingsRepository {
            override val windowSettings: Flow<WindowSettings> = state

            override suspend fun updateWindowSettings(
                transform: suspend (WindowSettings) -> WindowSettings,
            ): WindowSettings {
                val updated = transform(state.value)
                state.value = updated
                return updated
            }
        }
        val useCase = ManageWindowSettingsUseCaseImpl(repository)

        assertEquals(1024, useCase.settings.first().width)

        useCase.edit { it.copy(width = 1920, height = 1080, isMaximized = true) }

        val updated = useCase.settings.first()
        assertEquals(1920, updated.width)
        assertEquals(1080, updated.height)
        assertEquals(true, updated.isMaximized)
    }
}
