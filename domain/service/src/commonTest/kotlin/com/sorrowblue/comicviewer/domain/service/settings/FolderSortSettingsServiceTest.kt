/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.service.settings

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.common.InternalDataApi
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderDisplaySettings
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderScopeOnly
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(InternalDataApi::class)
class FolderSortSettingsServiceTest {

    private val service = FolderSortSettingsServiceImpl()
    private val bookshelfId = BookshelfId(1)
    private val path = "/manga/chapter1"

    @Test
    fun testIsFolderScopeOnly() {
        val defaultSettings = FolderDisplaySettings()
        assertFalse(service.isFolderScopeOnly(defaultSettings, bookshelfId, path))

        val scopedSettings = defaultSettings.copy(
            folderScopeOnlyList = listOf(
                FolderScopeOnly(bookshelfId, path, SortType.Date(true)),
            ),
        )
        assertTrue(service.isFolderScopeOnly(scopedSettings, bookshelfId, path))
        assertFalse(service.isFolderScopeOnly(scopedSettings, BookshelfId(2), path))
        assertFalse(service.isFolderScopeOnly(scopedSettings, bookshelfId, "/other"))
    }

    @Test
    fun testResolveSortType() {
        val defaultSettings = FolderDisplaySettings(sortType = SortType.Name(true))
        assertEquals(
            SortType.Name(true),
            service.resolveSortType(defaultSettings, bookshelfId, path),
        )

        val scopedSettings = defaultSettings.copy(
            folderScopeOnlyList = listOf(
                FolderScopeOnly(bookshelfId, path, SortType.Date(false)),
            ),
        )
        assertEquals(
            SortType.Date(false),
            service.resolveSortType(scopedSettings, bookshelfId, path),
        )
        assertEquals(
            SortType.Name(true),
            service.resolveSortType(scopedSettings, BookshelfId(2), path),
        )
    }

    @Test
    fun testUpdateSortType_whenFolderScopeOnly_changesExisting() {
        val initialSettings = FolderDisplaySettings(
            sortType = SortType.Name(true),
            folderScopeOnlyList = listOf(
                FolderScopeOnly(bookshelfId, path, SortType.Name(true)),
            ),
        )

        val result = service.updateSortType(
            initialSettings,
            bookshelfId,
            path,
            SortType.Date(false),
        )
        assertTrue(result.isChanged)
        assertEquals(SortType.Name(true), result.settings.sortType)
        assertEquals(1, result.settings.folderScopeOnlyList.size)
        assertEquals(SortType.Date(false), result.settings.folderScopeOnlyList[0].sortType)
    }

    @Test
    fun testUpdateSortType_whenFolderScopeOnly_sameValue() {
        val initialSettings = FolderDisplaySettings(
            sortType = SortType.Name(true),
            folderScopeOnlyList = listOf(
                FolderScopeOnly(bookshelfId, path, SortType.Date(false)),
            ),
        )

        val result = service.updateSortType(
            initialSettings,
            bookshelfId,
            path,
            SortType.Date(false),
        )
        assertFalse(result.isChanged)
        assertEquals(initialSettings, result.settings)
    }

    @Test
    fun testUpdateSortType_whenGlobal_changesGlobal() {
        val initialSettings = FolderDisplaySettings(sortType = SortType.Name(true))

        val result = service.updateSortType(
            initialSettings,
            bookshelfId,
            path,
            SortType.Size(false),
        )
        assertTrue(result.isChanged)
        assertEquals(SortType.Size(false), result.settings.sortType)
        assertTrue(result.settings.folderScopeOnlyList.isEmpty())
    }

    @Test
    fun testUpdateSortType_whenGlobal_sameValue() {
        val initialSettings = FolderDisplaySettings(sortType = SortType.Name(true))

        val result = service.updateSortType(initialSettings, bookshelfId, path, SortType.Name(true))
        assertFalse(result.isChanged)
        assertEquals(initialSettings, result.settings)
    }

    @Test
    fun testToggleFolderScopeOnly_add() {
        val initialSettings = FolderDisplaySettings(sortType = SortType.Date(true))

        val updated = service.toggleFolderScopeOnly(initialSettings, bookshelfId, path)
        assertEquals(1, updated.folderScopeOnlyList.size)
        assertEquals(bookshelfId, updated.folderScopeOnlyList[0].bookshelfId)
        assertEquals(path, updated.folderScopeOnlyList[0].path)
        assertEquals(SortType.Date(true), updated.folderScopeOnlyList[0].sortType)
    }

    @Test
    fun testToggleFolderScopeOnly_remove() {
        val initialSettings = FolderDisplaySettings(
            sortType = SortType.Name(true),
            folderScopeOnlyList = listOf(
                FolderScopeOnly(bookshelfId, path, SortType.Size(true)),
            ),
        )

        val updated = service.toggleFolderScopeOnly(initialSettings, bookshelfId, path)
        assertTrue(updated.folderScopeOnlyList.isEmpty())
    }

    @Test
    fun testResolveSortType_inheritance_subfolder() {
        val scopedSettings = FolderDisplaySettings(
            sortType = SortType.Name(true),
            folderScopeOnlyList = listOf(
                FolderScopeOnly(
                    bookshelfId = bookshelfId,
                    path = "/manga",
                    sortType = SortType.Date(false),
                    includeSubfolders = true,
                ),
            ),
        )
        // 子フォルダに親の設定が継承される
        assertEquals(
            SortType.Date(false),
            service.resolveSortType(scopedSettings, bookshelfId, "/manga/vol1"),
        )
        // 孫フォルダにも継承される
        assertEquals(
            SortType.Date(false),
            service.resolveSortType(scopedSettings, bookshelfId, "/manga/vol1/ch1"),
        )
        // 異なるパスには継承されない
        assertEquals(
            SortType.Name(true),
            service.resolveSortType(scopedSettings, bookshelfId, "/novel/vol1"),
        )
        // 前方一致だがディレクトリ境界が異なる（/manga2）には継承されない
        assertEquals(
            SortType.Name(true),
            service.resolveSortType(scopedSettings, bookshelfId, "/manga2"),
        )
    }

    @Test
    fun testResolveSortType_nearestAncestorWins() {
        val scopedSettings = FolderDisplaySettings(
            sortType = SortType.Name(true),
            folderScopeOnlyList = listOf(
                FolderScopeOnly(
                    bookshelfId = bookshelfId,
                    path = "/manga",
                    sortType = SortType.Date(false),
                    includeSubfolders = true,
                ),
                FolderScopeOnly(
                    bookshelfId = bookshelfId,
                    path = "/manga/action",
                    sortType = SortType.Size(true),
                    includeSubfolders = true,
                ),
            ),
        )
        // 最も近い祖先（/manga/action）の設定が優先される
        assertEquals(
            SortType.Size(true),
            service.resolveSortType(scopedSettings, bookshelfId, "/manga/action/hero"),
        )
        // 自分自身に個別設定があれば最優先
        val withSelfSettings = scopedSettings.copy(
            folderScopeOnlyList = scopedSettings.folderScopeOnlyList + FolderScopeOnly(
                bookshelfId = bookshelfId,
                path = "/manga/action/hero",
                sortType = SortType.Name(false),
                includeSubfolders = false,
            ),
        )
        assertEquals(
            SortType.Name(false),
            service.resolveSortType(withSelfSettings, bookshelfId, "/manga/action/hero"),
        )
    }

    @Test
    fun testResolveSortType_subfolderNotInheritedWhenFalse() {
        val scopedSettings = FolderDisplaySettings(
            sortType = SortType.Name(true),
            folderScopeOnlyList = listOf(
                FolderScopeOnly(
                    bookshelfId = bookshelfId,
                    path = "/manga",
                    sortType = SortType.Date(false),
                    includeSubfolders = false,
                ),
            ),
        )
        // includeSubfolders = false のため子フォルダには継承されない
        assertEquals(
            SortType.Name(true),
            service.resolveSortType(scopedSettings, bookshelfId, "/manga/vol1"),
        )
    }

    @Test
    fun testToggleIncludeSubfolders_whenExists_togglesFlag() {
        val initialSettings = FolderDisplaySettings(
            sortType = SortType.Name(true),
            folderScopeOnlyList = listOf(
                FolderScopeOnly(bookshelfId, path, SortType.Date(true), includeSubfolders = false),
            ),
        )

        val updated = service.toggleIncludeSubfolders(initialSettings, bookshelfId, path)
        assertTrue(service.isIncludeSubfolders(updated, bookshelfId, path))
        assertEquals(1, updated.folderScopeOnlyList.size)

        val toggledBack = service.toggleIncludeSubfolders(updated, bookshelfId, path)
        assertFalse(service.isIncludeSubfolders(toggledBack, bookshelfId, path))
    }

    @Test
    fun testToggleIncludeSubfolders_whenNotExists_addsWithTrue() {
        val initialSettings = FolderDisplaySettings(sortType = SortType.Name(true))

        val updated = service.toggleIncludeSubfolders(initialSettings, bookshelfId, path)
        assertTrue(service.isIncludeSubfolders(updated, bookshelfId, path))
        assertTrue(service.isFolderScopeOnly(updated, bookshelfId, path))
        assertEquals(1, updated.folderScopeOnlyList.size)
        assertEquals(SortType.Name(true), updated.folderScopeOnlyList[0].sortType)
    }
}
