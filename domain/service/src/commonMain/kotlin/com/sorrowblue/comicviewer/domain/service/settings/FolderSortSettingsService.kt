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
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

/**
 * Domain service responsible for resolving and updating folder sort settings and scopes.
 */
interface FolderSortSettingsService {

    /**
     * Checks whether the specified folder has a folder-specific sort setting.
     *
     * @param settings The current folder display settings.
     * @param bookshelfId The ID of the bookshelf.
     * @param path The folder path.
     * @return True if a folder-specific setting exists, false otherwise.
     */
    fun isFolderScopeOnly(
        settings: FolderDisplaySettings,
        bookshelfId: BookshelfId,
        path: String,
    ): Boolean

    /**
     * Resolves the effective [FolderSortScope] for the specified folder.
     *
     * @param settings The current folder display settings.
     * @param bookshelfId The ID of the bookshelf.
     * @param path The folder path.
     * @return The effective [FolderSortScope] applied to the folder.
     */
    fun resolveSortScope(
        settings: FolderDisplaySettings,
        bookshelfId: BookshelfId,
        path: String,
    ): FolderSortScope

    /**
     * Resolves the effective [SortType] for the specified folder.
     *
     * @param settings The current folder display settings.
     * @param bookshelfId The ID of the bookshelf.
     * @param path The folder path.
     * @return The effective [SortType] applied to the folder.
     */
    fun resolveSortType(
        settings: FolderDisplaySettings,
        bookshelfId: BookshelfId,
        path: String,
    ): SortType

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
     * Checks whether the specified folder has subfolder sort inheritance enabled.
     *
     * @param settings The current folder display settings.
     * @param bookshelfId The ID of the bookshelf.
     * @param path The folder path.
     * @return True if subfolder sort inheritance is enabled, false otherwise.
     */
    fun isIncludeSubfolders(
        settings: FolderDisplaySettings,
        bookshelfId: BookshelfId,
        path: String,
    ): Boolean

    /**
     * Toggles the folder-specific sort setting for the specified folder.
     *
     * @param settings The current folder display settings.
     * @param bookshelfId The ID of the bookshelf.
     * @param path The folder path.
     * @return The updated [FolderDisplaySettings].
     */
    fun toggleFolderScopeOnly(
        settings: FolderDisplaySettings,
        bookshelfId: BookshelfId,
        path: String,
    ): FolderDisplaySettings

    /**
     * Toggles the subfolder sort inheritance flag for the specified folder.
     *
     * @param settings The current folder display settings.
     * @param bookshelfId The ID of the bookshelf.
     * @param path The folder path.
     * @return The updated [FolderDisplaySettings].
     */
    fun toggleIncludeSubfolders(
        settings: FolderDisplaySettings,
        bookshelfId: BookshelfId,
        path: String,
    ): FolderDisplaySettings

    /**
     * Represents the result of a sort setting or scope update.
     *
     * @property settings The updated folder display settings.
     * @property isChanged True if any setting value was modified.
     * @property isSortTypeChanged True if the effective sort type for the target folder changed.
     */
    data class UpdateResult(
        val settings: FolderDisplaySettings,
        val isChanged: Boolean,
        val isSortTypeChanged: Boolean = isChanged,
    )

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

    override fun isFolderScopeOnly(
        settings: FolderDisplaySettings,
        bookshelfId: BookshelfId,
        path: String,
    ): Boolean = settings.isFolderScopeOnly(bookshelfId, path)

    override fun isIncludeSubfolders(
        settings: FolderDisplaySettings,
        bookshelfId: BookshelfId,
        path: String,
    ): Boolean = settings.isIncludeSubfolders(bookshelfId, path)

    override fun resolveSortScope(
        settings: FolderDisplaySettings,
        bookshelfId: BookshelfId,
        path: String,
    ): FolderSortScope = settings.currentSortScope(bookshelfId, path)

    override fun resolveSortType(
        settings: FolderDisplaySettings,
        bookshelfId: BookshelfId,
        path: String,
    ): SortType = settings.currentSortType(bookshelfId, path)

    override fun updateSortType(
        settings: FolderDisplaySettings,
        bookshelfId: BookshelfId,
        path: String,
        sortType: SortType,
    ): FolderSortSettingsService.UpdateResult {
        val previousSortType = resolveSortType(settings, bookshelfId, path)
        val currentScope = resolveSortScope(settings, bookshelfId, path)
        val newSettings = when (currentScope) {
            FolderSortScope.Global -> {
                val updatedList = settings.folderScopeOnlyList.map {
                    if (
                        it.bookshelfId == bookshelfId &&
                        isSameFolderPath(it.path, path) &&
                        it.useGlobal
                    ) {
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
        val newSortType = resolveSortType(newSettings, bookshelfId, path)
        return FolderSortSettingsService.UpdateResult(
            settings = newSettings,
            isChanged = newSettings != settings,
            isSortTypeChanged = newSortType != previousSortType,
        )
    }

    override fun updateSortScope(
        settings: FolderDisplaySettings,
        bookshelfId: BookshelfId,
        path: String,
        scope: FolderSortScope,
    ): FolderSortSettingsService.UpdateResult {
        val previousSortType = resolveSortType(settings, bookshelfId, path)
        val newList = when (scope) {
            FolderSortScope.Global -> {
                val withoutSelf = settings.folderScopeOnlyList.filterNot {
                    it.bookshelfId == bookshelfId && isSameFolderPath(it.path, path)
                }
                val cleaned = cleanupOrphanUseGlobalEntries(withoutSelf, bookshelfId)
                val hasSubfoldersAncestor = cleaned.any {
                    it.bookshelfId == bookshelfId &&
                        !it.useGlobal &&
                        it.includeSubfolders &&
                        isSubfolderPath(parent = it.path, child = path)
                }
                if (hasSubfoldersAncestor) {
                    upsertScopeEntry(
                        list = settings.folderScopeOnlyList.let {
                            cleanupOrphanUseGlobalEntries(it, bookshelfId)
                        },
                        entry = FolderScopeOnly(
                            bookshelfId = bookshelfId,
                            path = path,
                            sortType = settings.sortType,
                            includeSubfolders = false,
                            useGlobal = true,
                        ),
                    )
                } else {
                    cleaned
                }
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
        val newSortType = resolveSortType(newSettings, bookshelfId, path)
        return FolderSortSettingsService.UpdateResult(
            settings = newSettings,
            isChanged = newSettings != settings,
            isSortTypeChanged = newSortType != previousSortType,
        )
    }

    override fun toggleFolderScopeOnly(
        settings: FolderDisplaySettings,
        bookshelfId: BookshelfId,
        path: String,
    ): FolderDisplaySettings {
        val isCurrentlyScoped = isFolderScopeOnly(settings, bookshelfId, path)
        val targetScope = if (isCurrentlyScoped) {
            FolderSortScope.Global
        } else {
            FolderSortScope.FolderOnly
        }
        return updateSortScope(settings, bookshelfId, path, targetScope).settings
    }

    override fun toggleIncludeSubfolders(
        settings: FolderDisplaySettings,
        bookshelfId: BookshelfId,
        path: String,
    ): FolderDisplaySettings {
        val isCurrentlySubfolders = isIncludeSubfolders(settings, bookshelfId, path)
        val targetScope = if (isCurrentlySubfolders) {
            FolderSortScope.FolderOnly
        } else {
            FolderSortScope.Subfolders
        }
        return updateSortScope(settings, bookshelfId, path, targetScope).settings
    }

    private fun upsertScopeEntry(
        list: List<FolderScopeOnly>,
        entry: FolderScopeOnly,
    ): List<FolderScopeOnly> {
        val exists = list.any {
            it.bookshelfId == entry.bookshelfId && isSameFolderPath(it.path, entry.path)
        }
        return if (exists) {
            list.map {
                if (it.bookshelfId == entry.bookshelfId && isSameFolderPath(it.path, entry.path)) {
                    it.copy(
                        sortType = entry.sortType,
                        includeSubfolders = entry.includeSubfolders,
                        useGlobal = entry.useGlobal,
                    )
                } else {
                    it
                }
            }
        } else {
            list + entry
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
