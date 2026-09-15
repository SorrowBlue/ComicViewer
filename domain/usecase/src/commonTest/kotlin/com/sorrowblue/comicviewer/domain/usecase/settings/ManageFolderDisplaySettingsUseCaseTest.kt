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
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.createGraph
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest

@OptIn(InternalDataApi::class)
class ManageFolderDisplaySettingsUseCaseTest {

    private lateinit var manageFolderDisplaySettingsUseCase: ManageFolderDisplaySettingsUseCase
    private val bookshelfId = BookshelfId(1)
    private val path = "/manga"

    @BeforeTest
    fun setup() {
        val graph = createGraph<TestGraph>()
        manageFolderDisplaySettingsUseCase = graph.manageFolderDisplaySettingsUseCase
    }

    @Test
    fun testUpdateSortType() = runTest {
        manageFolderDisplaySettingsUseCase.edit {
            it.copy(sortType = SortType.Name(true))
        }
        // 異なるソート種別への更新 -> true が返り、設定が更新される
        val changed = manageFolderDisplaySettingsUseCase.updateSortType(
            bookshelfId,
            path,
            SortType.Date(false),
        )
        assertTrue(changed)
        assertEquals(
            SortType.Date(false),
            manageFolderDisplaySettingsUseCase.settings.first().sortType,
        )

        // 同じソート種別への更新 -> false が返る
        val notChanged = manageFolderDisplaySettingsUseCase.updateSortType(
            bookshelfId,
            path,
            SortType.Date(false),
        )
        assertFalse(notChanged)
    }

    @Test
    fun testToggleFolderScopeOnly() = runTest {
        manageFolderDisplaySettingsUseCase.edit {
            it.copy(sortType = SortType.Name(true))
        }

        // 最初は空 -> トグルで個別設定が追加される
        manageFolderDisplaySettingsUseCase.toggleFolderScopeOnly(bookshelfId, path)
        val afterAdd = manageFolderDisplaySettingsUseCase.settings.first()
        assertEquals(1, afterAdd.folderScopeOnlyList.size)
        assertEquals(bookshelfId, afterAdd.folderScopeOnlyList[0].bookshelfId)
        assertEquals(path, afterAdd.folderScopeOnlyList[0].path)

        // 再度トグル -> 個別設定が削除される
        manageFolderDisplaySettingsUseCase.toggleFolderScopeOnly(bookshelfId, path)
        val afterRemove = manageFolderDisplaySettingsUseCase.settings.first()
        assertTrue(afterRemove.folderScopeOnlyList.isEmpty())
    }

    @Test
    fun testToggleIncludeSubfolders() = runTest {
        manageFolderDisplaySettingsUseCase.edit {
            it.copy(sortType = SortType.Name(true))
        }
        manageFolderDisplaySettingsUseCase.toggleIncludeSubfolders(bookshelfId, path)
        val afterAdd = manageFolderDisplaySettingsUseCase.settings.first()
        assertEquals(1, afterAdd.folderScopeOnlyList.size)
        assertTrue(afterAdd.folderScopeOnlyList[0].includeSubfolders)

        manageFolderDisplaySettingsUseCase.toggleIncludeSubfolders(bookshelfId, path)
        val afterToggle = manageFolderDisplaySettingsUseCase.settings.first()
        assertEquals(1, afterToggle.folderScopeOnlyList.size)
        assertFalse(afterToggle.folderScopeOnlyList[0].includeSubfolders)
    }
}

@DependencyGraph(scope = AppScope::class)
internal interface TestGraph {

    val manageFolderDisplaySettingsUseCase: ManageFolderDisplaySettingsUseCase
}

@Inject
@ContributesBinding(AppScope::class)
internal class FakeSettingsRepository : SettingsRepository {

    override val folderDisplaySettings: Flow<FolderDisplaySettings>
        field = MutableStateFlow(FolderDisplaySettings())

    override suspend fun updateFolderDisplaySettings(
        transform: suspend (FolderDisplaySettings) -> FolderDisplaySettings,
    ): FolderDisplaySettings {
        val newSettings = transform(folderDisplaySettings.value)
        folderDisplaySettings.value = newSettings
        return newSettings
    }

    override val settings: Flow<Settings>
        field = MutableStateFlow(Settings())

    override suspend fun updateSettings(transform: suspend (Settings) -> Settings): Settings {
        val newSettings = transform(settings.value)
        settings.value = newSettings
        return newSettings
    }

    override val displaySettings: Flow<DisplaySettings>
        field = MutableStateFlow(DisplaySettings())

    override suspend fun updateDisplaySettings(
        transform: suspend (DisplaySettings) -> DisplaySettings,
    ): DisplaySettings {
        val newSettings = transform(displaySettings.value)
        displaySettings.value = newSettings
        return newSettings
    }

    override val viewerSettings: Flow<ViewerSettings>
        field = MutableStateFlow(ViewerSettings())

    override suspend fun updateViewerSettings(
        transform: suspend (ViewerSettings) -> ViewerSettings,
    ): ViewerSettings {
        val newSettings = transform(viewerSettings.value)
        viewerSettings.value = newSettings
        return newSettings
    }

    override val bookSettings: Flow<BookSettings>
        field = MutableStateFlow(BookSettings())

    override suspend fun updateBookSettings(
        transform: suspend (BookSettings) -> BookSettings,
    ): BookSettings {
        val newSettings = transform(bookSettings.value)
        bookSettings.value = newSettings
        return newSettings
    }

    override val folderSettings: Flow<FolderSettings>
        field = MutableStateFlow(FolderSettings())

    override suspend fun updateFolderSettings(
        transform: suspend (FolderSettings) -> FolderSettings,
    ): FolderSettings {
        val newSettings = transform(folderSettings.value)
        folderSettings.value = newSettings
        return newSettings
    }

    override val securitySettings: Flow<SecuritySettings>
        field = MutableStateFlow(SecuritySettings())

    override suspend fun updateSecuritySettings(
        transform: suspend (SecuritySettings) -> SecuritySettings,
    ): SecuritySettings {
        val newSettings = transform(securitySettings.value)
        securitySettings.value = newSettings
        return newSettings
    }

    override val collectionSettings: Flow<CollectionSettings>
        field = MutableStateFlow(CollectionSettings())

    override suspend fun updateCollectionSettings(
        transform: suspend (CollectionSettings) -> CollectionSettings,
    ): CollectionSettings {
        val newSettings = transform(collectionSettings.value)
        collectionSettings.value = newSettings
        return newSettings
    }
}
