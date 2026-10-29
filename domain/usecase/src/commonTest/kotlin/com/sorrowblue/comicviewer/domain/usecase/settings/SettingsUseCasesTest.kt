/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.settings

import com.sorrowblue.comicviewer.domain.model.settings.BookSettings
import com.sorrowblue.comicviewer.domain.model.settings.CollectionSettings
import com.sorrowblue.comicviewer.domain.model.settings.DisplaySettings
import com.sorrowblue.comicviewer.domain.model.settings.FolderSettings
import com.sorrowblue.comicviewer.domain.model.settings.SecuritySettings
import com.sorrowblue.comicviewer.domain.model.settings.Settings
import com.sorrowblue.comicviewer.domain.model.settings.ViewerSettings
import com.sorrowblue.comicviewer.domain.usecase.StubSettingsRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest

class SettingsUseCasesTest {

    @Test
    fun loadSettingsUseCase_observesAndUpdatesSettings() = runTest {
        val state = MutableStateFlow(Settings(doneTutorial = false))
        val repository = object : StubSettingsRepository() {
            override val settings: Flow<Settings> = state
            override suspend fun updateSettings(
                transform: suspend (Settings) -> Settings,
            ): Settings {
                val updated = transform(state.value)
                state.value = updated
                return updated
            }
        }
        val useCase = LoadSettingsUseCaseImpl(repository)

        assertEquals(false, useCase.settings.first().doneTutorial)

        useCase.edit { it.copy(doneTutorial = true) }

        assertEquals(true, useCase.settings.first().doneTutorial)
    }

    @Test
    fun manageBookSettingsUseCase_observesAndUpdatesBookSettings() = runTest {
        val state = MutableStateFlow(BookSettings())
        val repository = object : StubSettingsRepository() {
            override val bookSettings: Flow<BookSettings> = state
            override suspend fun updateBookSettings(
                transform: suspend (BookSettings) -> BookSettings,
            ): BookSettings {
                val updated = transform(state.value)
                state.value = updated
                return updated
            }
        }
        val useCase = ManageBookSettingsUseCaseImpl(repository)

        assertEquals(BookSettings.PageFormat.Default, useCase.settings.first().pageFormat)

        useCase.edit { it.copy(pageFormat = BookSettings.PageFormat.Spread) }

        assertEquals(BookSettings.PageFormat.Spread, useCase.settings.first().pageFormat)
    }

    @Test
    fun manageCollectionSettingsUseCase_observesAndUpdatesCollectionSettings() = runTest {
        val state = MutableStateFlow(CollectionSettings(recent = false))
        val repository = object : StubSettingsRepository() {
            override val collectionSettings: Flow<CollectionSettings> = state
            override suspend fun updateCollectionSettings(
                transform: suspend (CollectionSettings) -> CollectionSettings,
            ): CollectionSettings {
                val updated = transform(state.value)
                state.value = updated
                return updated
            }
        }
        val useCase = ManageCollectionSettingsUseCaseImpl(repository)

        assertEquals(false, useCase.settings.first().recent)

        useCase.edit { it.copy(recent = true) }

        assertEquals(true, useCase.settings.first().recent)
    }

    @Test
    fun manageDisplaySettingsUseCase_observesAndUpdatesDisplaySettings() = runTest {
        val state = MutableStateFlow(DisplaySettings(restoreOnLaunch = false))
        val repository = object : StubSettingsRepository() {
            override val displaySettings: Flow<DisplaySettings> = state
            override suspend fun updateDisplaySettings(
                transform: suspend (DisplaySettings) -> DisplaySettings,
            ): DisplaySettings {
                val updated = transform(state.value)
                state.value = updated
                return updated
            }
        }
        val useCase = ManageDisplaySettingsUseCaseImpl(repository)

        assertEquals(false, useCase.settings.first().restoreOnLaunch)

        useCase.edit { it.copy(restoreOnLaunch = true) }

        assertEquals(true, useCase.settings.first().restoreOnLaunch)
    }

    @Test
    fun manageFolderSettingsUseCase_observesAndUpdatesFolderSettings() = runTest {
        val state = MutableStateFlow(FolderSettings(resolveImageFolder = false))
        val repository = object : StubSettingsRepository() {
            override val folderSettings: Flow<FolderSettings> = state
            override suspend fun updateFolderSettings(
                transform: suspend (FolderSettings) -> FolderSettings,
            ): FolderSettings {
                val updated = transform(state.value)
                state.value = updated
                return updated
            }
        }
        val useCase = ManageFolderSettingsUseCaseImpl(repository)

        assertEquals(false, useCase.settings.first().resolveImageFolder)

        useCase.edit { it.copy(resolveImageFolder = true) }

        assertEquals(true, useCase.settings.first().resolveImageFolder)
    }

    @Test
    fun manageSecuritySettingsUseCase_observesAndUpdatesSecuritySettings() = runTest {
        val state = MutableStateFlow(SecuritySettings(useBiometrics = false))
        val repository = object : StubSettingsRepository() {
            override val securitySettings: Flow<SecuritySettings> = state
            override suspend fun updateSecuritySettings(
                transform: suspend (SecuritySettings) -> SecuritySettings,
            ): SecuritySettings {
                val updated = transform(state.value)
                state.value = updated
                return updated
            }
        }
        val useCase = ManageSecuritySettingsUseCaseImpl(repository)

        assertEquals(false, useCase.settings.first().useBiometrics)

        useCase.edit { it.copy(useBiometrics = true) }

        assertEquals(true, useCase.settings.first().useBiometrics)
    }

    @Test
    fun manageViewerSettingsUseCase_observesAndUpdatesViewerSettings() = runTest {
        val state = MutableStateFlow(ViewerSettings(keepOnScreen = false))
        val repository = object : StubSettingsRepository() {
            override val viewerSettings: Flow<ViewerSettings> = state
            override suspend fun updateViewerSettings(
                transform: suspend (ViewerSettings) -> ViewerSettings,
            ): ViewerSettings {
                val updated = transform(state.value)
                state.value = updated
                return updated
            }
        }
        val useCase = ManageViewerSettingsUseCaseImpl(repository)

        assertEquals(false, useCase.settings.first().keepOnScreen)

        useCase.edit { it.copy(keepOnScreen = true) }

        assertEquals(true, useCase.settings.first().keepOnScreen)
    }
}
