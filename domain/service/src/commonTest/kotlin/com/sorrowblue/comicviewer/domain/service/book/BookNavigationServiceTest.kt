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
    fun testResolveFolderSortType_useGlobalAndNormalizedPaths() {
        val settings = FolderDisplaySettings(
            sortType = SortType.Name(false),
            folderScopeOnlyList = listOf(
                FolderScopeOnly(
                    bookshelfId = bookshelfId,
                    path = "/manga/",
                    sortType = SortType.Size(false),
                    includeSubfolders = true,
                ),
                FolderScopeOnly(
                    bookshelfId = bookshelfId,
                    path = "/manga/global_sub",
                    sortType = SortType.Date(true),
                    includeSubfolders = false,
                    useGlobal = true,
                ),
            ),
        )
        assertEquals(
            SortType.Size(false),
            service.resolveFolderSortType(settings, bookshelfId, "/manga"),
        )
        assertEquals(
            SortType.Size(false),
            service.resolveFolderSortType(settings, bookshelfId, "/manga/other_sub/"),
        )
        assertEquals(
            SortType.Name(false),
            service.resolveFolderSortType(settings, bookshelfId, "/manga/global_sub"),
        )
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
}
