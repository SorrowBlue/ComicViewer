/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.service.settings

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderDisplaySettings
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderScopeOnly
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderSortScope
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
import com.sorrowblue.comicviewer.domain.model.settings.folder.isSameFolderPath
import com.sorrowblue.comicviewer.domain.model.settings.folder.isSubfolderPath
import com.sorrowblue.comicviewer.domain.model.settings.folder.normalizeFolderPath
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

/**
 * Domain service responsible for updating folder sort settings and scopes.
 */
interface FolderSortSettingsService {

    /**
     * Updates the sort type for the specified folder according to its active scope.
     *
     * @param settings The current folder display settings.
     * @param bookshelfId The ID of the bookshelf.
     * @param path The folder path.
     * @param sortType The new sort type.
     * @return The [UpdateResult] containing the updated settings and change flags.
     */
    fun updateSortType(
        settings: FolderDisplaySettings,
        bookshelfId: BookshelfId,
        path: String,
        sortType: SortType,
    ): UpdateResult

    /**
     * Updates the sort scope for the specified folder.
     * When [FolderSortScope.Subfolders] is selected, any existing overrides on descendant
     * subfolders are cleared so the parent setting is applied uniformly.
     *
     * @param settings The current folder display settings.
     * @param bookshelfId The ID of the bookshelf.
     * @param path The folder path.
     * @param scope The new [FolderSortScope] to apply.
     * @return The [UpdateResult] containing the updated settings and change flags.
     */
    fun updateSortScope(
        settings: FolderDisplaySettings,
        bookshelfId: BookshelfId,
        path: String,
        scope: FolderSortScope,
    ): UpdateResult

    /**
     * Represents the result of a sort setting or scope update.
     *
     * @property settings The updated folder display settings.
     * @property isSortTypeChanged True if the effective sort type for the target folder changed.
     */
    data class UpdateResult(val settings: FolderDisplaySettings, val isSortTypeChanged: Boolean)

    companion object {
        /**
         * Creates an instance of [FolderSortSettingsService].
         */
        operator fun invoke(): FolderSortSettingsService = FolderSortSettingsServiceImpl()
    }
}

@ContributesBinding(AppScope::class)
@Inject
class FolderSortSettingsServiceImpl : FolderSortSettingsService {

    override fun updateSortType(
        settings: FolderDisplaySettings,
        bookshelfId: BookshelfId,
        path: String,
        sortType: SortType,
    ): FolderSortSettingsService.UpdateResult {
        val previousSortType = settings.currentSortType(bookshelfId, path)
        val currentScope = settings.currentSortScope(bookshelfId, path)
        val newSettings = when (currentScope) {
            FolderSortScope.Global -> {
                val updatedList = settings.folderScopeOnlyList.map {
                    if (it.useGlobal) {
                        it.copy(sortType = sortType)
                    } else {
                        it
                    }
                }
                settings.copy(
                    sortType = sortType,
                    folderScopeOnlyList = updatedList,
                )
            }

            FolderSortScope.FolderOnly -> {
                val updatedList = upsertScopeEntry(
                    list = settings.folderScopeOnlyList,
                    entry = FolderScopeOnly(
                        bookshelfId = bookshelfId,
                        path = path,
                        sortType = sortType,
                        includeSubfolders = false,
                        useGlobal = false,
                    ),
                )
                settings.copy(folderScopeOnlyList = updatedList)
            }

            FolderSortScope.Subfolders -> {
                val existingSelf = settings.folderScopeOnlyList.find {
                    it.bookshelfId == bookshelfId && isSameFolderPath(
                        it.path,
                        path,
                    ) && !it.useGlobal
                }
                val hasDescendants = settings.folderScopeOnlyList.any {
                    it.bookshelfId == bookshelfId && isSubfolderPath(parent = path, child = it.path)
                }
                if (existingSelf == null && !hasDescendants && previousSortType == sortType) {
                    settings
                } else {
                    val withoutDescendants = settings.folderScopeOnlyList.filterNot {
                        it.bookshelfId == bookshelfId &&
                            isSubfolderPath(parent = path, child = it.path)
                    }
                    val updatedList = upsertScopeEntry(
                        list = withoutDescendants,
                        entry = FolderScopeOnly(
                            bookshelfId = bookshelfId,
                            path = path,
                            sortType = sortType,
                            includeSubfolders = true,
                            useGlobal = false,
                        ),
                    )
                    settings.copy(folderScopeOnlyList = updatedList)
                }
            }
        }
        val newSortType = newSettings.currentSortType(bookshelfId, path)
        return FolderSortSettingsService.UpdateResult(
            settings = newSettings,
            isSortTypeChanged = newSortType != previousSortType,
        )
    }

    override fun updateSortScope(
        settings: FolderDisplaySettings,
        bookshelfId: BookshelfId,
        path: String,
        scope: FolderSortScope,
    ): FolderSortSettingsService.UpdateResult {
        val previousSortType = settings.currentSortType(bookshelfId, path)
        val newList = when (scope) {
            FolderSortScope.Global -> {
                val hasSubfoldersAncestor = settings.folderScopeOnlyList.any {
                    it.bookshelfId == bookshelfId &&
                        !it.useGlobal &&
                        it.includeSubfolders &&
                        isSubfolderPath(parent = it.path, child = path)
                }
                val updated = if (hasSubfoldersAncestor) {
                    upsertScopeEntry(
                        list = settings.folderScopeOnlyList,
                        entry = FolderScopeOnly(
                            bookshelfId = bookshelfId,
                            path = path,
                            sortType = settings.sortType,
                            includeSubfolders = false,
                            useGlobal = true,
                        ),
                    )
                } else {
                    settings.folderScopeOnlyList.filterNot {
                        it.bookshelfId == bookshelfId && isSameFolderPath(it.path, path)
                    }
                }
                cleanupOrphanUseGlobalEntries(updated, bookshelfId)
            }

            FolderSortScope.FolderOnly -> {
                val updated = upsertScopeEntry(
                    list = settings.folderScopeOnlyList,
                    entry = FolderScopeOnly(
                        bookshelfId = bookshelfId,
                        path = path,
                        sortType = previousSortType,
                        includeSubfolders = false,
                        useGlobal = false,
                    ),
                )
                cleanupOrphanUseGlobalEntries(updated, bookshelfId)
            }

            FolderSortScope.Subfolders -> {
                val withoutDescendants = settings.folderScopeOnlyList.filterNot {
                    it.bookshelfId == bookshelfId &&
                        isSubfolderPath(parent = path, child = it.path)
                }
                upsertScopeEntry(
                    list = withoutDescendants,
                    entry = FolderScopeOnly(
                        bookshelfId = bookshelfId,
                        path = path,
                        sortType = previousSortType,
                        includeSubfolders = true,
                        useGlobal = false,
                    ),
                )
            }
        }
        val newSettings = settings.copy(folderScopeOnlyList = newList)
        val newSortType = newSettings.currentSortType(bookshelfId, path)
        return FolderSortSettingsService.UpdateResult(
            settings = newSettings,
            isSortTypeChanged = newSortType != previousSortType,
        )
    }

    private fun upsertScopeEntry(
        list: List<FolderScopeOnly>,
        entry: FolderScopeOnly,
    ): List<FolderScopeOnly> {
        val normalizedEntry = entry.copy(path = normalizeFolderPath(entry.path))
        val exists = list.any {
            it.bookshelfId == normalizedEntry.bookshelfId &&
                isSameFolderPath(it.path, normalizedEntry.path)
        }
        return if (exists) {
            list.map {
                if (
                    it.bookshelfId == normalizedEntry.bookshelfId &&
                    isSameFolderPath(it.path, normalizedEntry.path)
                ) {
                    normalizedEntry
                } else {
                    it
                }
            }
        } else {
            list + normalizedEntry
        }
    }

    private fun cleanupOrphanUseGlobalEntries(
        list: List<FolderScopeOnly>,
        bookshelfId: BookshelfId,
    ): List<FolderScopeOnly> {
        val activeSubfolderAncestors = list.filter {
            it.bookshelfId == bookshelfId && !it.useGlobal && it.includeSubfolders
        }
        return list.filterNot { candidate ->
            candidate.bookshelfId == bookshelfId &&
                candidate.useGlobal &&
                activeSubfolderAncestors.none { ancestor ->
                    isSubfolderPath(parent = ancestor.path, child = candidate.path)
                }
        }
    }
}
