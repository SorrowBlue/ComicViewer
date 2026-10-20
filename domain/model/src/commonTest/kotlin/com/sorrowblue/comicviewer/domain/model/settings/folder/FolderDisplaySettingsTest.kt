/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.model.settings.folder

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.common.InternalDataApi
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(InternalDataApi::class)
class FolderDisplaySettingsTest {

    private val bookshelf1 = BookshelfId(1)
    private val bookshelf2 = BookshelfId(2)
    private val bookshelf3 = BookshelfId(3)

    // =========================================================================
    // 1. Path Normalization & Comparison (POSIX, SMB, Windows, Android SAF)
    // =========================================================================

    @Test
    fun normalizeFolderPath_handlesPosixAndTrailingSlashes() {
        assertEquals("", normalizeFolderPath(""))
        assertEquals("/", normalizeFolderPath("/"))
        assertEquals("/", normalizeFolderPath("///"))
        assertEquals("/manga", normalizeFolderPath("/manga"))
        assertEquals("/manga", normalizeFolderPath("/manga/"))
        assertEquals("/manga", normalizeFolderPath("/manga///"))
        assertEquals("/manga/sub", normalizeFolderPath("/manga/sub/"))
    }

    @Test
    fun normalizeFolderPath_handlesWindowsPaths() {
        assertEquals("C:/", normalizeFolderPath("C:\\"))
        assertEquals("C:/", normalizeFolderPath("C:/"))
        assertEquals("C:/Manga", normalizeFolderPath("C:\\Manga"))
        assertEquals("C:/Manga", normalizeFolderPath("C:\\Manga\\"))
        assertEquals("C:/Manga/Sub", normalizeFolderPath("C:\\Manga\\Sub\\"))
    }

    @Test
    fun normalizeFolderPath_handlesAndroidSafEncodedPaths() {
        val safParent =
            "content://com.android.externalstorage.documents/tree/primary%3AManga/document/primary%3AManga"
        val safParentTrailingUpper =
            "content://com.android.externalstorage.documents/tree/primary%3AManga/document/primary%3AManga%2F"
        val safParentTrailingLower =
            "content://com.android.externalstorage.documents/tree/primary%3AManga/document/primary%3AManga%2f"
        val safChild =
            "content://com.android.externalstorage.documents/tree/primary%3AManga/document/primary%3AManga%2FSub"

        assertEquals(normalizeFolderPath(safParent), normalizeFolderPath(safParentTrailingUpper))
        assertEquals(normalizeFolderPath(safParent), normalizeFolderPath(safParentTrailingLower))
        assertTrue(isSubfolderPath(parent = safParent, child = safChild))
        assertTrue(isSubfolderPath(parent = safParentTrailingUpper, child = safChild))
    }

    @Test
    fun isSameFolderPath_matchesEquivalentPathsOnly() {
        assertTrue(isSameFolderPath("/manga", "/manga"))
        assertTrue(isSameFolderPath("/manga", "/manga/"))
        assertTrue(isSameFolderPath("/manga/", "/manga"))
        assertTrue(isSameFolderPath("/", "///"))
        assertTrue(isSameFolderPath("C:\\Manga\\Sub", "C:/Manga/Sub/"))
        assertTrue(isSameFolderPath("primary%3AManga%2FSub", "primary%3AManga%2fSub/"))

        assertFalse(isSameFolderPath("/manga", "/manga2"))
        assertFalse(isSameFolderPath("/manga", "/manga/sub"))
        assertFalse(isSameFolderPath("", "/"))
    }

    @Test
    fun isSubfolderPath_enforcesPathSegmentBoundaries() {
        // Direct and deep subfolders
        assertTrue(isSubfolderPath(parent = "/manga", child = "/manga/sub"))
        assertTrue(isSubfolderPath(parent = "/manga/", child = "/manga/sub/"))
        assertTrue(isSubfolderPath(parent = "/manga", child = "/manga/sub/deep/leaf"))

        // Same folder is NOT a subfolder
        assertFalse(isSubfolderPath(parent = "/manga", child = "/manga"))
        assertFalse(isSubfolderPath(parent = "/manga", child = "/manga/"))
        assertFalse(isSubfolderPath(parent = "/manga/", child = "/manga"))

        // Prefix collision with sibling folder names
        assertFalse(isSubfolderPath(parent = "/manga", child = "/manga2"))
        assertFalse(isSubfolderPath(parent = "/manga", child = "/manga_extra/sub"))
        assertFalse(isSubfolderPath(parent = "/manga", child = "/mangasub"))

        // Reverse direction
        assertFalse(isSubfolderPath(parent = "/manga/sub", child = "/manga"))

        // Root directory handling
        assertTrue(isSubfolderPath(parent = "/", child = "/manga"))
        assertTrue(isSubfolderPath(parent = "/", child = "/manga/sub"))
        assertFalse(isSubfolderPath(parent = "/", child = "/"))
        assertTrue(isSubfolderPath(parent = "C:\\", child = "C:\\Manga"))
        assertFalse(isSubfolderPath(parent = "C:\\", child = "C:/"))

        // Empty paths
        assertFalse(isSubfolderPath(parent = "", child = "/manga"))
        assertFalse(isSubfolderPath(parent = "/manga", child = ""))
        assertFalse(isSubfolderPath(parent = "", child = ""))
    }

    // =========================================================================
    // 2. Exhaustive SortType x FolderSortScope Resolution
    // =========================================================================

    @Test
    fun currentSortType_and_currentSortScope_allSortTypesInGlobalScope() {
        for (globalSort in SortType.entries) {
            val settings = FolderDisplaySettings(
                sortType = globalSort,
                folderScopeOnlyList = emptyList(),
            )
            assertEquals(FolderSortScope.Global, settings.currentSortScope(bookshelf1, "/manga"))
            assertFalse(settings.isFolderScopeOnly(bookshelf1, "/manga"))
            assertFalse(settings.isIncludeSubfolders(bookshelf1, "/manga"))
            assertEquals(globalSort, settings.currentSortType(bookshelf1, "/manga"))
            assertEquals(globalSort, settings.currentSortType(bookshelf1, "/manga/sub"))
        }
    }

    @Test
    fun currentSortType_and_currentSortScope_allSortTypesInFolderOnlyScope() {
        for (globalSort in SortType.entries) {
            for (folderSort in SortType.entries) {
                val settings = FolderDisplaySettings(
                    sortType = globalSort,
                    folderScopeOnlyList = listOf(
                        FolderScopeOnly(
                            bookshelfId = bookshelf1,
                            path = "/manga",
                            sortType = folderSort,
                            includeSubfolders = false,
                        ),
                    ),
                )
                // Target folder itself (including with trailing slash)
                assertEquals(
                    FolderSortScope.FolderOnly,
                    settings.currentSortScope(bookshelf1, "/manga"),
                )
                assertEquals(
                    FolderSortScope.FolderOnly,
                    settings.currentSortScope(bookshelf1, "/manga/"),
                )
                assertTrue(settings.isFolderScopeOnly(bookshelf1, "/manga"))
                assertFalse(settings.isIncludeSubfolders(bookshelf1, "/manga"))
                assertEquals(folderSort, settings.currentSortType(bookshelf1, "/manga"))
                assertEquals(folderSort, settings.currentSortType(bookshelf1, "/manga/"))

                // Subfolder does NOT inherit when includeSubfolders = false
                assertEquals(
                    FolderSortScope.Global,
                    settings.currentSortScope(bookshelf1, "/manga/sub"),
                )
                assertFalse(settings.isFolderScopeOnly(bookshelf1, "/manga/sub"))
                assertFalse(settings.isIncludeSubfolders(bookshelf1, "/manga/sub"))
                assertEquals(globalSort, settings.currentSortType(bookshelf1, "/manga/sub"))

                // Sibling and prefix-similar folders use global
                assertEquals(globalSort, settings.currentSortType(bookshelf1, "/manga2"))
                assertEquals(globalSort, settings.currentSortType(bookshelf1, "/other"))
            }
        }
    }

    @Test
    fun currentSortType_and_currentSortScope_allSortTypesInSubfoldersScope() {
        for (globalSort in SortType.entries) {
            for (subfolderSort in SortType.entries) {
                val settings = FolderDisplaySettings(
                    sortType = globalSort,
                    folderScopeOnlyList = listOf(
                        FolderScopeOnly(
                            bookshelfId = bookshelf1,
                            path = "/manga",
                            sortType = subfolderSort,
                            includeSubfolders = true,
                        ),
                    ),
                )
                // Target folder, direct child, and deep child all resolve to Subfolders + subfolderSort
                for (targetPath in listOf("/manga", "/manga/", "/manga/sub", "/manga/sub/deep")) {
                    assertEquals(
                        FolderSortScope.Subfolders,
                        settings.currentSortScope(bookshelf1, targetPath),
                        "Expected Subfolders scope for $targetPath",
                    )
                    assertTrue(settings.isFolderScopeOnly(bookshelf1, targetPath))
                    assertTrue(settings.isIncludeSubfolders(bookshelf1, targetPath))
                    assertEquals(
                        subfolderSort,
                        settings.currentSortType(bookshelf1, targetPath),
                        "Expected $subfolderSort for $targetPath",
                    )
                }

                // Outside hierarchy uses Global
                for (outsidePath in listOf("/", "/manga2", "/manga_other/sub", "/other")) {
                    assertEquals(
                        FolderSortScope.Global,
                        settings.currentSortScope(bookshelf1, outsidePath),
                    )
                    assertFalse(settings.isFolderScopeOnly(bookshelf1, outsidePath))
                    assertFalse(settings.isIncludeSubfolders(bookshelf1, outsidePath))
                    assertEquals(globalSort, settings.currentSortType(bookshelf1, outsidePath))
                }
            }
        }
    }

    // =========================================================================
    // 3. Multi-Level Hierarchy & Override Resolution
    // =========================================================================

    @Test
    fun hierarchy_nearestSubfolderAncestorWins_regardlessOfListOrder() {
        val rootEntry = FolderScopeOnly(
            bookshelfId = bookshelf1,
            path = "/a",
            sortType = SortType.Date(true),
            includeSubfolders = true,
        )
        val midEntry = FolderScopeOnly(
            bookshelfId = bookshelf1,
            path = "/a/b",
            sortType = SortType.Size(false),
            includeSubfolders = true,
        )

        // Test both list orders: [root, mid] and [mid, root]
        for (list in listOf(listOf(rootEntry, midEntry), listOf(midEntry, rootEntry))) {
            val settings = FolderDisplaySettings(
                sortType = SortType.Name(true),
                folderScopeOnlyList = list,
            )
            assertEquals(SortType.Date(true), settings.currentSortType(bookshelf1, "/a"))
            assertEquals(SortType.Date(true), settings.currentSortType(bookshelf1, "/a/other"))
            assertEquals(SortType.Size(false), settings.currentSortType(bookshelf1, "/a/b"))
            assertEquals(SortType.Size(false), settings.currentSortType(bookshelf1, "/a/b/c"))
            assertEquals(SortType.Size(false), settings.currentSortType(bookshelf1, "/a/b/c/d"))
        }
    }

    @Test
    fun hierarchy_childFolderOnlyOverridesSubfolderParentForChildOnly() {
        val settings = FolderDisplaySettings(
            sortType = SortType.Name(true),
            folderScopeOnlyList = listOf(
                FolderScopeOnly(
                    bookshelfId = bookshelf1,
                    path = "/a",
                    sortType = SortType.Date(false),
                    includeSubfolders = true,
                ),
                FolderScopeOnly(
                    bookshelfId = bookshelf1,
                    path = "/a/b",
                    sortType = SortType.Size(true),
                    includeSubfolders = false,
                ),
            ),
        )

        // Parent /a has Subfolders
        assertEquals(FolderSortScope.Subfolders, settings.currentSortScope(bookshelf1, "/a"))
        assertEquals(SortType.Date(false), settings.currentSortType(bookshelf1, "/a"))

        // Child /a/b has FolderOnly
        assertEquals(FolderSortScope.FolderOnly, settings.currentSortScope(bookshelf1, "/a/b"))
        assertTrue(settings.isFolderScopeOnly(bookshelf1, "/a/b"))
        assertFalse(settings.isIncludeSubfolders(bookshelf1, "/a/b"))
        assertEquals(SortType.Size(true), settings.currentSortType(bookshelf1, "/a/b"))

        // Grandchild /a/b/c inherits from /a because /a/b has includeSubfolders = false
        assertEquals(FolderSortScope.Subfolders, settings.currentSortScope(bookshelf1, "/a/b/c"))
        assertEquals(SortType.Date(false), settings.currentSortType(bookshelf1, "/a/b/c"))
    }

    @Test
    fun hierarchy_childUseGlobalOverridesSubfolderParentForChildOnly() {
        val settings = FolderDisplaySettings(
            sortType = SortType.Name(false),
            folderScopeOnlyList = listOf(
                FolderScopeOnly(
                    bookshelfId = bookshelf1,
                    path = "/a",
                    sortType = SortType.Date(true),
                    includeSubfolders = true,
                ),
                FolderScopeOnly(
                    bookshelfId = bookshelf1,
                    path = "/a/b",
                    sortType = SortType.Name(false),
                    includeSubfolders = false,
                    useGlobal = true,
                ),
            ),
        )

        // Parent /a has Subfolders
        assertEquals(FolderSortScope.Subfolders, settings.currentSortScope(bookshelf1, "/a"))
        assertEquals(SortType.Date(true), settings.currentSortType(bookshelf1, "/a"))

        // Child /a/b opted back into Global
        assertEquals(FolderSortScope.Global, settings.currentSortScope(bookshelf1, "/a/b"))
        assertFalse(settings.isFolderScopeOnly(bookshelf1, "/a/b"))
        assertFalse(settings.isIncludeSubfolders(bookshelf1, "/a/b"))
        assertEquals(SortType.Name(false), settings.currentSortType(bookshelf1, "/a/b"))

        // Grandchild /a/b/c still inherits from /a
        assertEquals(FolderSortScope.Subfolders, settings.currentSortScope(bookshelf1, "/a/b/c"))
        assertEquals(SortType.Date(true), settings.currentSortType(bookshelf1, "/a/b/c"))
    }

    // =========================================================================
    // 4. Multi-Bookshelf Isolation
    // =========================================================================

    @Test
    fun bookshelfIsolation_samePathDifferentBookshelvesResolveIndependently() {
        val settings = FolderDisplaySettings(
            sortType = SortType.Name(true),
            folderScopeOnlyList = listOf(
                FolderScopeOnly(
                    bookshelfId = bookshelf1,
                    path = "/manga",
                    sortType = SortType.Date(false),
                    includeSubfolders = true,
                ),
                FolderScopeOnly(
                    bookshelfId = bookshelf2,
                    path = "/manga",
                    sortType = SortType.Size(true),
                    includeSubfolders = false,
                ),
            ),
        )

        // Bookshelf 1: Subfolders
        assertEquals(FolderSortScope.Subfolders, settings.currentSortScope(bookshelf1, "/manga"))
        assertEquals(SortType.Date(false), settings.currentSortType(bookshelf1, "/manga"))
        assertEquals(
            FolderSortScope.Subfolders,
            settings.currentSortScope(bookshelf1, "/manga/sub"),
        )
        assertEquals(SortType.Date(false), settings.currentSortType(bookshelf1, "/manga/sub"))

        // Bookshelf 2: FolderOnly
        assertEquals(FolderSortScope.FolderOnly, settings.currentSortScope(bookshelf2, "/manga"))
        assertEquals(SortType.Size(true), settings.currentSortType(bookshelf2, "/manga"))
        assertEquals(FolderSortScope.Global, settings.currentSortScope(bookshelf2, "/manga/sub"))
        assertEquals(SortType.Name(true), settings.currentSortType(bookshelf2, "/manga/sub"))

        // Bookshelf 3: Global
        assertEquals(FolderSortScope.Global, settings.currentSortScope(bookshelf3, "/manga"))
        assertEquals(SortType.Name(true), settings.currentSortType(bookshelf3, "/manga"))
        assertEquals(FolderSortScope.Global, settings.currentSortScope(bookshelf3, "/manga/sub"))
        assertEquals(SortType.Name(true), settings.currentSortType(bookshelf3, "/manga/sub"))
    }
}
