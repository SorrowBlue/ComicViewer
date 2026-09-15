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
            service.resolveSortType(defaultSettings, bookshelfId, path)
        )

        val scopedSettings = defaultSettings.copy(
            folderScopeOnlyList = listOf(
                FolderScopeOnly(bookshelfId, path, SortType.Date(false)),
            ),
        )
        assertEquals(
            SortType.Date(false),
            service.resolveSortType(scopedSettings, bookshelfId, path)
        )
        assertEquals(
            SortType.Name(true),
            service.resolveSortType(scopedSettings, BookshelfId(2), path)
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
            SortType.Date(false)
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
            SortType.Date(false)
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
            SortType.Size(false)
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
}
