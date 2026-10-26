/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.settings

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.common.InternalDataApi
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderDisplaySettings
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderSortScope
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
import com.sorrowblue.comicviewer.domain.repository.SettingsRepository
import com.sorrowblue.comicviewer.domain.usecase.StubSettingsRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
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
    private val subPath = "/manga/sub"

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
    fun testUpdateSortScope_and_reloadReturnValues() = runTest {
        manageFolderDisplaySettingsUseCase.edit {
            it.copy(sortType = SortType.Name(true))
        }

        // Global(Name(true)) -> FolderOnly(Name(true)): スコープは変わるが実効ソート順は同じなので false
        val sortChangedToFolderOnly = manageFolderDisplaySettingsUseCase.updateSortScope(
            bookshelfId,
            path,
            FolderSortScope.FolderOnly,
        )
        assertFalse(sortChangedToFolderOnly)
        assertEquals(
            FolderSortScope.FolderOnly,
            manageFolderDisplaySettingsUseCase.settings.first().currentSortScope(bookshelfId, path),
        )

        // FolderOnly で Date(false) に変更 -> 実効ソート順が変わるので true
        val sortChangedInFolderOnly = manageFolderDisplaySettingsUseCase.updateSortType(
            bookshelfId,
            path,
            SortType.Date(false),
        )
        assertTrue(sortChangedInFolderOnly)

        // FolderOnly(Date(false)) -> Subfolders(Date(false)): 実効ソート順は同じなので false（子フォルダには波及）
        val sortChangedToSubfolders = manageFolderDisplaySettingsUseCase.updateSortScope(
            bookshelfId,
            path,
            FolderSortScope.Subfolders,
        )
        assertFalse(sortChangedToSubfolders)
        assertEquals(
            SortType.Date(false),
            manageFolderDisplaySettingsUseCase.settings.first().currentSortType(
                bookshelfId,
                subPath,
            ),
        )

        // 子フォルダで Global(Name(true)) に切り替え -> 子フォルダの実効ソート順が Date(false) -> Name(true) に変わるので true
        val subChangedToGlobal = manageFolderDisplaySettingsUseCase.updateSortScope(
            bookshelfId,
            subPath,
            FolderSortScope.Global,
        )
        assertTrue(subChangedToGlobal)
        assertEquals(
            SortType.Name(true),
            manageFolderDisplaySettingsUseCase.settings.first().currentSortType(
                bookshelfId,
                subPath,
            ),
        )

        // 親フォルダで Global(Name(true)) に切り替え -> 親の実効ソート順が Date(false) -> Name(true) に変わるので true
        val parentChangedToGlobal = manageFolderDisplaySettingsUseCase.updateSortScope(
            bookshelfId,
            path,
            FolderSortScope.Global,
        )
        assertTrue(parentChangedToGlobal)
        assertTrue(
            manageFolderDisplaySettingsUseCase.settings.first().folderScopeOnlyList.isEmpty(),
        )
    }
}

@DependencyGraph(scope = AppScope::class)
internal interface TestGraph {

    val manageFolderDisplaySettingsUseCase: ManageFolderDisplaySettingsUseCase
}

@Inject
@ContributesBinding(AppScope::class, binding = binding<SettingsRepository>())
internal class FakeSettingsRepository : StubSettingsRepository() {

    override val folderDisplaySettings: Flow<FolderDisplaySettings>
        field = MutableStateFlow(FolderDisplaySettings())

    override suspend fun updateFolderDisplaySettings(
        transform: suspend (FolderDisplaySettings) -> FolderDisplaySettings,
    ): FolderDisplaySettings {
        val newSettings = transform(folderDisplaySettings.value)
        folderDisplaySettings.value = newSettings
        return newSettings
    }
}
