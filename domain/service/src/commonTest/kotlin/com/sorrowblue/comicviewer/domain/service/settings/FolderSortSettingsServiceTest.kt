/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.service.settings

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.common.InternalDataApi
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderDisplaySettings
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderScopeOnly
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderSortScope
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(InternalDataApi::class)
class FolderSortSettingsServiceTest {

    private val service = FolderSortSettingsServiceImpl()
    private val bookshelfId = BookshelfId(1)
    private val otherBookshelfId = BookshelfId(2)
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
        assertFalse(service.isFolderScopeOnly(scopedSettings, otherBookshelfId, path))
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
            service.resolveSortType(scopedSettings, otherBookshelfId, path),
        )
    }

    // =========================================================================
    // 1. Exhaustive 3x3 Scope Transitions (Standalone Folder)
    // =========================================================================

    @Test
    fun testUpdateSortScope_all3x3Transitions_standaloneFolder() {
        val baseGlobalSort = SortType.Name(true)
        val customSort = SortType.Date(false)

        for (fromScope in FolderSortScope.entries) {
            for (toScope in FolderSortScope.entries) {
                // Set up initial state for fromScope
                val initialSettings = when (fromScope) {
                    FolderSortScope.Global -> FolderDisplaySettings(
                        sortType = baseGlobalSort,
                        folderScopeOnlyList = emptyList(),
                    )

                    FolderSortScope.FolderOnly -> FolderDisplaySettings(
                        sortType = baseGlobalSort,
                        folderScopeOnlyList = listOf(
                            FolderScopeOnly(
                                bookshelfId = bookshelfId,
                                path = "/manga",
                                sortType = customSort,
                                includeSubfolders = false,
                            ),
                        ),
                    )

                    FolderSortScope.Subfolders -> FolderDisplaySettings(
                        sortType = baseGlobalSort,
                        folderScopeOnlyList = listOf(
                            FolderScopeOnly(
                                bookshelfId = bookshelfId,
                                path = "/manga",
                                sortType = customSort,
                                includeSubfolders = true,
                            ),
                        ),
                    )
                }

                assertEquals(
                    fromScope,
                    service.resolveSortScope(initialSettings, bookshelfId, "/manga"),
                )

                val result = service.updateSortScope(
                    settings = initialSettings,
                    bookshelfId = bookshelfId,
                    path = "/manga",
                    scope = toScope,
                )

                // Resulting scope must match toScope
                assertEquals(
                    toScope,
                    service.resolveSortScope(result.settings, bookshelfId, "/manga"),
                    "Transition $fromScope -> $toScope failed to set scope",
                )

                if (fromScope == toScope) {
                    assertFalse(
                        result.isChanged,
                        "Same scope transition $fromScope -> $toScope should not mark isChanged",
                    )
                    assertFalse(
                        result.isSortTypeChanged,
                        "Same scope transition $fromScope -> $toScope should not mark isSortTypeChanged",
                    )
                } else {
                    assertTrue(
                        result.isChanged,
                        "Different scope transition $fromScope -> $toScope should mark isChanged",
                    )
                }

                // Verify effective sort type and subfolder inheritance after transition
                val expectedSort = if (toScope == FolderSortScope.Global ||
                    fromScope == FolderSortScope.Global
                ) {
                    baseGlobalSort
                } else {
                    customSort
                }
                assertEquals(
                    expectedSort,
                    service.resolveSortType(result.settings, bookshelfId, "/manga"),
                )

                val expectedSortChanged =
                    (fromScope != FolderSortScope.Global && toScope == FolderSortScope.Global)
                assertEquals(
                    expectedSortChanged,
                    result.isSortTypeChanged,
                    "isSortTypeChanged mismatch for $fromScope -> $toScope",
                )

                if (toScope == FolderSortScope.Subfolders) {
                    assertEquals(
                        FolderSortScope.Subfolders,
                        service.resolveSortScope(result.settings, bookshelfId, "/manga/sub"),
                    )
                    assertEquals(
                        expectedSort,
                        service.resolveSortType(result.settings, bookshelfId, "/manga/sub"),
                    )
                } else {
                    assertEquals(
                        FolderSortScope.Global,
                        service.resolveSortScope(result.settings, bookshelfId, "/manga/sub"),
                    )
                    assertEquals(
                        baseGlobalSort,
                        service.resolveSortType(result.settings, bookshelfId, "/manga/sub"),
                    )
                }
            }
        }
    }

    // =========================================================================
    // 2. Exhaustive 3x3 Scope Transitions (Subfolder under Subfolders Parent)
    // =========================================================================

    @Test
    fun testUpdateSortScope_all3x3Transitions_underSubfoldersParent() {
        val globalSort = SortType.Name(true)
        val parentSort = SortType.Date(false)
        val childSort = SortType.Size(true)

        val parentBase = FolderDisplaySettings(
            sortType = globalSort,
            folderScopeOnlyList = listOf(
                FolderScopeOnly(
                    bookshelfId = bookshelfId,
                    path = "/manga",
                    sortType = parentSort,
                    includeSubfolders = true,
                ),
            ),
        )

        for (fromScope in FolderSortScope.entries) {
            for (toScope in FolderSortScope.entries) {
                // Prepare subfolder /manga/sub in fromScope
                val initial = when (fromScope) {
                    FolderSortScope.Global -> service.updateSortScope(
                        parentBase,
                        bookshelfId,
                        "/manga/sub",
                        FolderSortScope.Global,
                    ).settings

                    FolderSortScope.FolderOnly -> {
                        val s1 = service.updateSortScope(
                            parentBase,
                            bookshelfId,
                            "/manga/sub",
                            FolderSortScope.FolderOnly,
                        ).settings
                        service.updateSortType(s1, bookshelfId, "/manga/sub", childSort).settings
                    }

                    FolderSortScope.Subfolders -> {
                        val s1 = service.updateSortScope(
                            parentBase,
                            bookshelfId,
                            "/manga/sub",
                            FolderSortScope.Subfolders,
                        ).settings
                        service.updateSortType(s1, bookshelfId, "/manga/sub", childSort).settings
                    }
                }

                assertEquals(
                    fromScope,
                    service.resolveSortScope(initial, bookshelfId, "/manga/sub"),
                )

                val result = service.updateSortScope(
                    settings = initial,
                    bookshelfId = bookshelfId,
                    path = "/manga/sub",
                    scope = toScope,
                )

                assertEquals(
                    toScope,
                    service.resolveSortScope(result.settings, bookshelfId, "/manga/sub"),
                    "Subfolder transition $fromScope -> $toScope failed",
                )

                // Parent /manga must remain untouched in Subfolders(parentSort)
                assertEquals(
                    FolderSortScope.Subfolders,
                    service.resolveSortScope(result.settings, bookshelfId, "/manga"),
                )
                assertEquals(
                    parentSort,
                    service.resolveSortType(result.settings, bookshelfId, "/manga"),
                )
            }
        }
    }

    // =========================================================================
    // 3. Exhaustive updateSortType across all 6 SortTypes & 3 Scopes
    // =========================================================================

    @Test
    fun testUpdateSortType_allSortTypesAndScopes() {
        for (scope in FolderSortScope.entries) {
            for (initialSort in SortType.entries) {
                for (targetSort in SortType.entries) {
                    val base = FolderDisplaySettings(sortType = initialSort)
                    val scoped = service.updateSortScope(base, bookshelfId, path, scope).settings

                    val result = service.updateSortType(scoped, bookshelfId, path, targetSort)
                    assertEquals(
                        targetSort,
                        service.resolveSortType(result.settings, bookshelfId, path),
                    )
                    assertEquals(
                        scope,
                        service.resolveSortScope(result.settings, bookshelfId, path),
                    )
                    assertEquals(initialSort != targetSort, result.isChanged)
                    assertEquals(initialSort != targetSort, result.isSortTypeChanged)
                }
            }
        }
    }

    @Test
    fun testUpdateSortType_inInheritedSubfolder_doesNotOverwriteGlobalSort() {
        // Regression test for Bug 1:
        // Parent /manga has Subfolders(Date(false)), global is Name(true).
        // Subfolder /manga/vol1 has no direct entry in folderScopeOnlyList, inheriting Subfolders(Date(false)).
        // Changing sort in /manga/vol1 to Size(true) must update /manga/vol1 (and its subfolders)
        // WITHOUT overwriting global sortType.
        val initialSettings = FolderDisplaySettings(
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

        val result = service.updateSortType(
            settings = initialSettings,
            bookshelfId = bookshelfId,
            path = "/manga/vol1",
            sortType = SortType.Size(true),
        )

        assertTrue(result.isChanged)
        assertTrue(result.isSortTypeChanged)
        // Global sort is preserved
        assertEquals(SortType.Name(true), result.settings.sortType)
        // Parent /manga is preserved
        assertEquals(
            SortType.Date(false),
            service.resolveSortType(result.settings, bookshelfId, "/manga"),
        )
        // Subfolder /manga/vol1 and its child /manga/vol1/ch1 now use Size(true)
        assertEquals(
            FolderSortScope.Subfolders,
            service.resolveSortScope(result.settings, bookshelfId, "/manga/vol1"),
        )
        assertEquals(
            SortType.Size(true),
            service.resolveSortType(result.settings, bookshelfId, "/manga/vol1"),
        )
        assertEquals(
            SortType.Size(true),
            service.resolveSortType(result.settings, bookshelfId, "/manga/vol1/ch1"),
        )
    }

    @Test
    fun testUpdateSortType_inGlobalScope_preservesExistingFolderAndSubfolderOverrides() {
        // Specification A2: Updating Global sort updates settings.sortType while keeping
        // existing FolderOnly and Subfolders settings intact.
        val initialSettings = FolderDisplaySettings(
            sortType = SortType.Name(true),
            folderScopeOnlyList = listOf(
                FolderScopeOnly(
                    bookshelfId = bookshelfId,
                    path = "/folderOnly",
                    sortType = SortType.Date(false),
                    includeSubfolders = false,
                ),
                FolderScopeOnly(
                    bookshelfId = bookshelfId,
                    path = "/subfolders",
                    sortType = SortType.Size(true),
                    includeSubfolders = true,
                ),
            ),
        )

        val result = service.updateSortType(
            settings = initialSettings,
            bookshelfId = bookshelfId,
            path = "/globalFolder",
            sortType = SortType.Name(false),
        )

        assertTrue(result.isChanged)
        assertTrue(result.isSortTypeChanged)
        assertEquals(SortType.Name(false), result.settings.sortType)
        assertEquals(
            SortType.Name(false),
            service.resolveSortType(result.settings, bookshelfId, "/globalFolder"),
        )
        // Scoped folders are preserved
        assertEquals(
            SortType.Date(false),
            service.resolveSortType(result.settings, bookshelfId, "/folderOnly"),
        )
        assertEquals(
            SortType.Size(true),
            service.resolveSortType(result.settings, bookshelfId, "/subfolders"),
        )
        assertEquals(
            SortType.Size(true),
            service.resolveSortType(result.settings, bookshelfId, "/subfolders/child"),
        )
    }

    // =========================================================================
    // 4. Last-Action-Wins ("後から操作した方を優先") Parent-Child Sequences
    // =========================================================================

    @Test
    fun lastActionWins_parentSelectingSubfoldersClearsAllDescendantOverrides() {
        // Step 1: Child (/manga/sub) and Grandchild (/manga/sub/deep) have individual settings,
        // and another folder (/other) also has a setting.
        val initialSettings = FolderDisplaySettings(
            sortType = SortType.Name(true),
            folderScopeOnlyList = listOf(
                FolderScopeOnly(
                    bookshelfId = bookshelfId,
                    path = "/manga/sub",
                    sortType = SortType.Date(false),
                    includeSubfolders = false,
                ),
                FolderScopeOnly(
                    bookshelfId = bookshelfId,
                    path = "/manga/sub/deep",
                    sortType = SortType.Size(true),
                    includeSubfolders = true,
                ),
                FolderScopeOnly(
                    bookshelfId = bookshelfId,
                    path = "/other",
                    sortType = SortType.Name(false),
                    includeSubfolders = false,
                ),
                FolderScopeOnly(
                    bookshelfId = otherBookshelfId,
                    path = "/manga/sub",
                    sortType = SortType.Date(true),
                    includeSubfolders = false,
                ),
            ),
        )

        // Step 2: Parent (/manga) selects Subfolders and sets Date(true)
        val afterScope = service.updateSortScope(
            settings = initialSettings,
            bookshelfId = bookshelfId,
            path = "/manga",
            scope = FolderSortScope.Subfolders,
        )
        val afterSort = service.updateSortType(
            settings = afterScope.settings,
            bookshelfId = bookshelfId,
            path = "/manga",
            sortType = SortType.Date(true),
        )

        val finalSettings = afterSort.settings

        // Parent, child, and grandchild on bookshelfId all resolve to Date(true) and Subfolders
        for (p in listOf("/manga", "/manga/sub", "/manga/sub/deep")) {
            assertEquals(
                FolderSortScope.Subfolders,
                service.resolveSortScope(finalSettings, bookshelfId, p),
            )
            assertEquals(
                SortType.Date(true),
                service.resolveSortType(finalSettings, bookshelfId, p),
            )
        }

        // Unrelated folder (/other) and other bookshelf (otherBookshelfId) are untouched
        assertEquals(
            SortType.Name(false),
            service.resolveSortType(finalSettings, bookshelfId, "/other"),
        )
        assertEquals(
            SortType.Date(true),
            service.resolveSortType(finalSettings, otherBookshelfId, "/manga/sub"),
        )
    }

    @Test
    fun lastActionWins_childOverrideThenParentUpdateSortTypeInSubfoldersClearsChild() {
        // Step 1: Parent (/manga) sets Subfolders(Date(true))
        var settings = FolderDisplaySettings(sortType = SortType.Name(true))
        settings = service.updateSortScope(
            settings,
            bookshelfId,
            "/manga",
            FolderSortScope.Subfolders,
        ).settings
        settings = service.updateSortType(
            settings,
            bookshelfId,
            "/manga",
            SortType.Date(true),
        ).settings

        // Step 2: Child (/manga/sub) overrides with FolderOnly(Size(false))
        settings = service.updateSortScope(
            settings,
            bookshelfId,
            "/manga/sub",
            FolderSortScope.FolderOnly,
        ).settings
        settings = service.updateSortType(
            settings,
            bookshelfId,
            "/manga/sub",
            SortType.Size(false),
        ).settings
        assertEquals(
            FolderSortScope.FolderOnly,
            service.resolveSortScope(settings, bookshelfId, "/manga/sub"),
        )
        assertEquals(
            SortType.Size(false),
            service.resolveSortType(settings, bookshelfId, "/manga/sub"),
        )

        // Step 3: Parent (/manga) changes sort type to Name(false) (still in Subfolders)
        settings = service.updateSortType(
            settings,
            bookshelfId,
            "/manga",
            SortType.Name(false),
        ).settings

        // Child (/manga/sub) is reset and now inherits Name(false) from Parent
        assertEquals(
            FolderSortScope.Subfolders,
            service.resolveSortScope(settings, bookshelfId, "/manga/sub"),
        )
        assertEquals(
            SortType.Name(false),
            service.resolveSortType(settings, bookshelfId, "/manga/sub"),
        )
    }

    @Test
    fun orphanUseGlobalCleanup_whenParentLeavesSubfoldersScope() {
        // Step 1: Parent (/manga) is Subfolders(Date(false))
        var settings = FolderDisplaySettings(sortType = SortType.Name(true))
        settings = service.updateSortScope(
            settings,
            bookshelfId,
            "/manga",
            FolderSortScope.Subfolders,
        ).settings
        settings = service.updateSortType(
            settings,
            bookshelfId,
            "/manga",
            SortType.Date(false),
        ).settings

        // Step 2: Child (/manga/sub) selects Global -> creates a useGlobal=true entry
        settings = service.updateSortScope(
            settings,
            bookshelfId,
            "/manga/sub",
            FolderSortScope.Global,
        ).settings
        assertTrue(settings.folderScopeOnlyList.any { it.path == "/manga/sub" && it.useGlobal })

        // Step 3: Parent (/manga) switches to FolderOnly -> child's useGlobal entry is now orphan and cleaned up
        settings = service.updateSortScope(
            settings,
            bookshelfId,
            "/manga",
            FolderSortScope.FolderOnly,
        ).settings
        assertFalse(settings.folderScopeOnlyList.any { it.useGlobal })
        assertEquals(1, settings.folderScopeOnlyList.size)
        assertEquals("/manga", settings.folderScopeOnlyList[0].path)
    }

    // =========================================================================
    // 5. Legacy Toggle Methods & Path Normalization
    // =========================================================================

    @Test
    fun testToggleFolderScopeOnly_and_toggleIncludeSubfolders_withNormalizedPaths() {
        val initialSettings = FolderDisplaySettings(sortType = SortType.Date(true))

        // Add with "/manga/chapter1"
        val added = service.toggleFolderScopeOnly(
            initialSettings,
            bookshelfId,
            "/manga/chapter1"
        ).settings
        assertEquals(1, added.folderScopeOnlyList.size)
        assertTrue(service.isFolderScopeOnly(added, bookshelfId, "/manga/chapter1/"))

        // Toggle includeSubfolders using trailing slash "/manga/chapter1/" -> updates existing entry, no duplicate
        val withSubfolders = service.toggleIncludeSubfolders(
            added,
            bookshelfId,
            "/manga/chapter1/",
        ).settings
        assertEquals(1, withSubfolders.folderScopeOnlyList.size)
        assertTrue(service.isIncludeSubfolders(withSubfolders, bookshelfId, "/manga/chapter1"))

        // Toggle folderScopeOnly off using "/manga/chapter1/" -> removes entry
        val removed = service.toggleFolderScopeOnly(
            withSubfolders,
            bookshelfId,
            "/manga/chapter1/",
        ).settings
        assertTrue(removed.folderScopeOnlyList.isEmpty())
    }

    @Test
    fun testToggleFolderScopeOnly_onInheritedSubfolder_switchesToGlobalThenFolderOnly() {
        val initialSettings = FolderDisplaySettings(
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

        // Subfolder /manga/sub is currently scoped (inheriting Subfolders)
        assertTrue(service.isFolderScopeOnly(initialSettings, bookshelfId, "/manga/sub"))

        // Toggling folderScopeOnly on /manga/sub turns scope OFF -> Global
        val toggledOff = service.toggleFolderScopeOnly(
            initialSettings,
            bookshelfId,
            "/manga/sub"
        ).settings
        assertFalse(service.isFolderScopeOnly(toggledOff, bookshelfId, "/manga/sub"))
        assertEquals(
            FolderSortScope.Global,
            service.resolveSortScope(toggledOff, bookshelfId, "/manga/sub"),
        )
        assertEquals(
            SortType.Name(true),
            service.resolveSortType(toggledOff, bookshelfId, "/manga/sub"),
        )

        // Toggling folderScopeOnly again turns scope ON -> FolderOnly
        val toggledOn = service.toggleFolderScopeOnly(
            toggledOff,
            bookshelfId,
            "/manga/sub"
        ).settings
        assertTrue(service.isFolderScopeOnly(toggledOn, bookshelfId, "/manga/sub"))
        assertEquals(
            FolderSortScope.FolderOnly,
            service.resolveSortScope(toggledOn, bookshelfId, "/manga/sub"),
        )
    }
}
