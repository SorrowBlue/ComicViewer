/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.settings

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.common.InternalDataApi
import com.sorrowblue.comicviewer.domain.model.settings.BookSettings
import com.sorrowblue.comicviewer.domain.model.settings.CollectionSettings
import com.sorrowblue.comicviewer.domain.model.settings.DisplaySettings
import com.sorrowblue.comicviewer.domain.model.settings.FolderSettings
import com.sorrowblue.comicviewer.domain.model.settings.SecuritySettings
import com.sorrowblue.comicviewer.domain.model.settings.Settings
import com.sorrowblue.comicviewer.domain.model.settings.ViewerSettings
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderDisplaySettings
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
import com.sorrowblue.comicviewer.domain.repository.SettingsRepository
import com.sorrowblue.comicviewer.domain.service.settings.FolderSortSettingsServiceImpl
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest

@OptIn(InternalDataApi::class)
class ManageFolderDisplaySettingsUseCaseTest {

    private val bookshelfId = BookshelfId(1)
    private val path = "/manga"

    @Test
    fun testUpdateSortType() = runTest {
        val fakeRepo = FakeSettingsRepository(FolderDisplaySettings(sortType = SortType.Name(true)))
        val service = FolderSortSettingsServiceImpl()
        val useCase = ManageFolderDisplaySettingsUseCase(fakeRepo, service)

        // 異なるソート種別への更新 -> true が返り、設定が更新される
        val changed = useCase.updateSortType(bookshelfId, path, SortType.Date(false))
        assertTrue(changed)
        assertEquals(SortType.Date(false), fakeRepo.folderDisplaySettings.first().sortType)

        // 同じソート種別への更新 -> false が返る
        val notChanged = useCase.updateSortType(bookshelfId, path, SortType.Date(false))
        assertFalse(notChanged)
    }

    @Test
    fun testToggleFolderScopeOnly() = runTest {
        val fakeRepo = FakeSettingsRepository(FolderDisplaySettings(sortType = SortType.Name(true)))
        val service = FolderSortSettingsServiceImpl()
        val useCase = ManageFolderDisplaySettingsUseCase(fakeRepo, service)

        // 最初は空 -> トグルで個別設定が追加される
        useCase.toggleFolderScopeOnly(bookshelfId, path)
        val afterAdd = fakeRepo.folderDisplaySettings.first()
        assertEquals(1, afterAdd.folderScopeOnlyList.size)
        assertEquals(bookshelfId, afterAdd.folderScopeOnlyList[0].bookshelfId)
        assertEquals(path, afterAdd.folderScopeOnlyList[0].path)

        // 再度トグル -> 個別設定が削除される
        useCase.toggleFolderScopeOnly(bookshelfId, path)
        val afterRemove = fakeRepo.folderDisplaySettings.first()
        assertTrue(afterRemove.folderScopeOnlyList.isEmpty())
    }

    private class FakeSettingsRepository(
        initialFolderDisplaySettings: FolderDisplaySettings = FolderDisplaySettings(),
    ) : SettingsRepository {

        private val _folderDisplaySettings = MutableStateFlow(initialFolderDisplaySettings)
        override val folderDisplaySettings: Flow<FolderDisplaySettings> = _folderDisplaySettings.asStateFlow()

        override suspend fun updateFolderDisplaySettings(
            transform: suspend (FolderDisplaySettings) -> FolderDisplaySettings,
        ): FolderDisplaySettings {
            val newSettings = transform(_folderDisplaySettings.value)
            _folderDisplaySettings.value = newSettings
            return newSettings
        }

        override val settings: Flow<Settings> get() = TODO()
        override suspend fun updateSettings(transform: suspend (Settings) -> Settings): Settings =
            TODO()
        override val displaySettings: Flow<DisplaySettings> get() = TODO()
        override suspend fun updateDisplaySettings(
            transform: suspend (DisplaySettings) -> DisplaySettings
        ): DisplaySettings = TODO()
        override val viewerSettings: Flow<ViewerSettings> get() = TODO()
        override suspend fun updateViewerSettings(
            transform: suspend (ViewerSettings) -> ViewerSettings
        ): ViewerSettings = TODO()
        override val bookSettings: Flow<BookSettings> get() = TODO()
        override suspend fun updateBookSettings(
            transform: suspend (BookSettings) -> BookSettings
        ): BookSettings = TODO()
        override val folderSettings: Flow<FolderSettings> get() = TODO()
        override suspend fun updateFolderSettings(
            transform: suspend (FolderSettings) -> FolderSettings
        ): FolderSettings = TODO()
        override val securitySettings: Flow<SecuritySettings> get() = TODO()
        override suspend fun updateSecuritySettings(
            transform: suspend (SecuritySettings) -> SecuritySettings
        ): SecuritySettings = TODO()
        override val collectionSettings: Flow<CollectionSettings> get() = TODO()
        override suspend fun updateCollectionSettings(
            transform: suspend (CollectionSettings) -> CollectionSettings
        ): CollectionSettings = TODO()
    }
}
