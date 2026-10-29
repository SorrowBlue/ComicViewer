/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.repository

import com.sorrowblue.comicviewer.domain.model.settings.BookSettings
import com.sorrowblue.comicviewer.domain.model.settings.CollectionSettings
import com.sorrowblue.comicviewer.domain.model.settings.DisplaySettings
import com.sorrowblue.comicviewer.domain.model.settings.FolderSettings
import com.sorrowblue.comicviewer.domain.model.settings.SecuritySettings
import com.sorrowblue.comicviewer.domain.model.settings.Settings
import com.sorrowblue.comicviewer.domain.model.settings.ViewerSettings
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderDisplaySettings
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for observing and updating application settings.
 */
interface SettingsRepository {
    /**
     * Observes general application [Settings].
     */
    val settings: Flow<Settings>

    /**
     * Updates general application [Settings] using [transform].
     */
    suspend fun updateSettings(transform: suspend (Settings) -> Settings): Settings

    /**
     * Observes [DisplaySettings].
     */
    val displaySettings: Flow<DisplaySettings>

    /**
     * Updates [DisplaySettings] using [transform].
     */
    suspend fun updateDisplaySettings(
        transform: suspend (DisplaySettings) -> DisplaySettings,
    ): DisplaySettings

    /**
     * Observes [ViewerSettings].
     */
    val viewerSettings: Flow<ViewerSettings>

    /**
     * Updates [ViewerSettings] using [transform].
     */
    suspend fun updateViewerSettings(
        transform: suspend (ViewerSettings) -> ViewerSettings,
    ): ViewerSettings

    /**
     * Observes [BookSettings].
     */
    val bookSettings: Flow<BookSettings>

    /**
     * Updates [BookSettings] using [transform].
     */
    suspend fun updateBookSettings(transform: suspend (BookSettings) -> BookSettings): BookSettings

    /**
     * Observes [FolderDisplaySettings].
     */
    val folderDisplaySettings: Flow<FolderDisplaySettings>

    /**
     * Updates [FolderDisplaySettings] using [transform].
     */
    suspend fun updateFolderDisplaySettings(
        transform: suspend (FolderDisplaySettings) -> FolderDisplaySettings,
    ): FolderDisplaySettings

    /**
     * Observes [FolderSettings].
     */
    val folderSettings: Flow<FolderSettings>

    /**
     * Updates [FolderSettings] using [transform].
     */
    suspend fun updateFolderSettings(
        transform: suspend (FolderSettings) -> FolderSettings,
    ): FolderSettings

    /**
     * Observes [SecuritySettings].
     */
    val securitySettings: Flow<SecuritySettings>

    /**
     * Updates [SecuritySettings] using [transform].
     */
    suspend fun updateSecuritySettings(
        transform: suspend (SecuritySettings) -> SecuritySettings,
    ): SecuritySettings

    /**
     * Observes [CollectionSettings].
     */
    val collectionSettings: Flow<CollectionSettings>

    /**
     * Updates [CollectionSettings] using [transform].
     */
    suspend fun updateCollectionSettings(
        transform: suspend (CollectionSettings) -> CollectionSettings,
    ): CollectionSettings
}
