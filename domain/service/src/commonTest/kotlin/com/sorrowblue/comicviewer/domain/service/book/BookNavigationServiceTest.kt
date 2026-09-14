/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.service.book

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.collection.BasicCollection
import com.sorrowblue.comicviewer.domain.model.collection.SmartCollection
import com.sorrowblue.comicviewer.domain.model.common.InternalDataApi
import com.sorrowblue.comicviewer.domain.model.file.BookFile
import com.sorrowblue.comicviewer.domain.model.file.BookFolder
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.domain.model.search.SearchCondition
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderDisplaySettings
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderScopeOnly
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

@OptIn(InternalDataApi::class)
class BookNavigationServiceTest {

    private val service = BookNavigationService()
    private val bookshelfId = BookshelfId(1)

    @Test
    fun testResolveFolderSortType_defaultSettings() {
        val settings = FolderDisplaySettings(sortType = SortType.Name(true))
        val sortType = service.resolveFolderSortType(
            settings = settings,
            bookshelfId = bookshelfId,
            parentPath = "/manga",
        )
        assertEquals(SortType.Name(true), sortType)
    }

    @Test
    fun testResolveFolderSortType_scopeOnlySettings() {
        val settings = FolderDisplaySettings(
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
        val sortType = service.resolveFolderSortType(
            settings = settings,
            bookshelfId = bookshelfId,
            parentPath = "/manga",
        )
        assertEquals(SortType.Date(false), sortType)
    }

    @Test
    fun testResolveFolderSortType_includeSubfoldersInherited() {
        val settings = FolderDisplaySettings(
            sortType = SortType.Name(true),
            folderScopeOnlyList = listOf(
                FolderScopeOnly(
                    bookshelfId = bookshelfId,
                    path = "/manga",
                    sortType = SortType.Size(false),
                    includeSubfolders = true,
                ),
            ),
        )
        val subSortType = service.resolveFolderSortType(
            settings = settings,
            bookshelfId = bookshelfId,
            parentPath = "/manga/action/series1",
        )
        assertEquals(SortType.Size(false), subSortType)
    }

    @Test
    fun testResolveCollectionSortType_smartCollection() {
        val smartCollection = SmartCollection(
            name = "Recent",
            bookshelfId = bookshelfId,
            searchCondition = SearchCondition(
                sortType = SortType.Date(false),
            ),
        )
        val settings = FolderDisplaySettings(sortType = SortType.Name(true))
        val resolved = service.resolveCollectionSortType(smartCollection, settings)
        assertEquals(SortType.Date(false), resolved)
    }

    @Test
    fun testResolveCollectionSortType_basicCollection() {
        val basicCollection = BasicCollection(
            name = "Favorites",
        )
        val settings = FolderDisplaySettings(sortType = SortType.Name(false))
        val resolved = service.resolveCollectionSortType(basicCollection, settings)
        assertEquals(SortType.Name(false), resolved)
    }

    @Test
    fun testExtractParentPath() {
        assertEquals("/manga", service.extractParentPath("/manga/vol1.cbz"))
        assertEquals("/manga", service.extractParentPath("/manga/vol1.cbz/"))
        assertEquals("/manga/sub", service.extractParentPath("/manga/sub/vol1.cbz"))
        assertEquals("", service.extractParentPath("/vol1.cbz"))
        assertEquals("manga", service.extractParentPath("manga/vol1.cbz"))
        assertEquals("", service.extractParentPath("vol1.cbz"))
        assertEquals("", service.extractParentPath("/"))
        assertEquals("", service.extractParentPath(""))
    }

    @Test
    fun testValidateBook() {
        val bookFile = BookFile(
            bookshelfId = bookshelfId,
            name = "vol1.cbz",
            parent = "/manga",
            path = "/manga/vol1.cbz",
            size = 1000,
            lastModifier = 100,
            isHidden = false,
        )
        val bookFolder = BookFolder(
            bookshelfId = bookshelfId,
            name = "chapter1",
            parent = "/manga",
            path = "/manga/chapter1",
            size = 2000,
            lastModifier = 200,
            isHidden = false,
        )
        val folder = Folder(
            bookshelfId = bookshelfId,
            name = "manga",
            parent = "",
            path = "/manga",
            size = 0,
            lastModifier = 0,
            isHidden = false,
        )

        assertEquals(bookFile, service.validateBook(bookFile))
        assertEquals(bookFolder, service.validateBook(bookFolder))
        assertNull(service.validateBook(folder))
        assertNull(service.validateBook(null))
    }

    @Test
    fun testFindNextBook_sortByNameAsc() {
        val b1 = BookFile(bookshelfId, "01.cbz", "/m", "/m/01.cbz", 100, 10, false, sortIndex = 0)
        val b2 = BookFile(bookshelfId, "02.cbz", "/m", "/m/02.cbz", 200, 20, false, sortIndex = 1)
        val b3 = BookFile(bookshelfId, "03.cbz", "/m", "/m/03.cbz", 300, 30, false, sortIndex = 2)
        val folder = Folder(bookshelfId, "sub", "/m", "/m/sub", 0, 0, false)

        val files = listOf(b2, folder, b3, b1)
        val sortType = SortType.Name(true)

        // 次の本
        assertEquals(b2, service.findNextBook(files, b1.path, isNext = true, sortType = sortType))
        assertEquals(b3, service.findNextBook(files, b2.path, isNext = true, sortType = sortType))
        assertNull(service.findNextBook(files, b3.path, isNext = true, sortType = sortType))

        // 前の本
        assertNull(service.findNextBook(files, b1.path, isNext = false, sortType = sortType))
        assertEquals(b1, service.findNextBook(files, b2.path, isNext = false, sortType = sortType))
        assertEquals(b2, service.findNextBook(files, b3.path, isNext = false, sortType = sortType))

        // 存在しないパス
        assertNull(service.findNextBook(files, "/not_found", isNext = true, sortType = sortType))
    }

    @Test
    fun testFindNextBook_sortByNameDesc() {
        val b1 = BookFile(bookshelfId, "01.cbz", "/m", "/m/01.cbz", 100, 10, false, sortIndex = 0)
        val b2 = BookFile(bookshelfId, "02.cbz", "/m", "/m/02.cbz", 200, 20, false, sortIndex = 1)
        val b3 = BookFile(bookshelfId, "03.cbz", "/m", "/m/03.cbz", 300, 30, false, sortIndex = 2)

        val files = listOf(b1, b2, b3)
        val sortType = SortType.Name(false) // 降順: b3, b2, b1

        assertEquals(b2, service.findNextBook(files, b3.path, isNext = true, sortType = sortType))
        assertEquals(b1, service.findNextBook(files, b2.path, isNext = true, sortType = sortType))
        assertNull(service.findNextBook(files, b1.path, isNext = true, sortType = sortType))

        assertNull(service.findNextBook(files, b3.path, isNext = false, sortType = sortType))
        assertEquals(b3, service.findNextBook(files, b2.path, isNext = false, sortType = sortType))
        assertEquals(b2, service.findNextBook(files, b1.path, isNext = false, sortType = sortType))
    }

    @Test
    fun testFindNextBook_sortByDateAndSize() {
        val b1 = BookFile(bookshelfId, "b1.cbz", "/m", "/m/b1.cbz", 300, 10, false)
        val b2 = BookFile(bookshelfId, "b2.cbz", "/m", "/m/b2.cbz", 100, 30, false)
        val b3 = BookFile(bookshelfId, "b3.cbz", "/m", "/m/b3.cbz", 200, 20, false)

        val files = listOf(b1, b2, b3)

        // Date 昇順: b1(10), b3(20), b2(30)
        val dateAsc = SortType.Date(true)
        assertEquals(b3, service.findNextBook(files, b1.path, isNext = true, sortType = dateAsc))
        assertEquals(b2, service.findNextBook(files, b3.path, isNext = true, sortType = dateAsc))

        // Size 昇順: b2(100), b3(200), b1(300)
        val sizeAsc = SortType.Size(true)
        assertEquals(b3, service.findNextBook(files, b2.path, isNext = true, sortType = sizeAsc))
        assertEquals(b1, service.findNextBook(files, b3.path, isNext = true, sortType = sizeAsc))
    }
}
