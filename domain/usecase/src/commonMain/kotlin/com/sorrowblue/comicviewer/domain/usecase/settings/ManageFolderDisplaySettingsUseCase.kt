/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.settings

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderDisplaySettings
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderSortScope
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
import com.sorrowblue.comicviewer.domain.repository.SettingsRepository
import com.sorrowblue.comicviewer.domain.service.settings.FolderSortSettingsService
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow

/**
 * Use case for retrieving and editing folder display settings and folder-specific sort settings.
 */
interface ManageFolderDisplaySettingsUseCase : ManageSettingsUseCase<FolderDisplaySettings> {

    /**
     * Updates the sort type for the specified folder according to its active scope.
     *
     * @param bookshelfId The ID of the bookshelf.
     * @param path The folder path.
     * @param sortType The new sort type.
     * @return True if the effective sort type for the folder changed, false otherwise.
     */
    suspend fun updateSortType(bookshelfId: BookshelfId, path: String, sortType: SortType): Boolean

    /**
     * Updates the sort scope for the specified folder.
     *
     * @param bookshelfId The ID of the bookshelf.
     * @param path The folder path.
     * @param scope The new [FolderSortScope] to apply.
     * @return True if the effective sort type for the folder changed, false otherwise.
     */
    suspend fun updateSortScope(
        bookshelfId: BookshelfId,
        path: String,
        scope: FolderSortScope,
    ): Boolean
}

@Inject
@ContributesBinding(AppScope::class)
internal class ManageFolderDisplaySettingsUseCaseImpl(
    private val settingsRepository: SettingsRepository,
    private val folderSortSettingsService: FolderSortSettingsService,
) : ManageFolderDisplaySettingsUseCase {

    override val settings: Flow<FolderDisplaySettings> = settingsRepository.folderDisplaySettings

    override suspend fun edit(action: (FolderDisplaySettings) -> FolderDisplaySettings) {
        settingsRepository.updateFolderDisplaySettings(action)
    }

    override suspend fun updateSortType(
        bookshelfId: BookshelfId,
        path: String,
        sortType: SortType,
    ): Boolean = updateWithResult { current ->
        folderSortSettingsService.updateSortType(
            settings = current,
            bookshelfId = bookshelfId,
            path = path,
            sortType = sortType,
        )
    }

    override suspend fun updateSortScope(
        bookshelfId: BookshelfId,
        path: String,
        scope: FolderSortScope,
    ): Boolean = updateWithResult { current ->
        folderSortSettingsService.updateSortScope(
            settings = current,
            bookshelfId = bookshelfId,
            path = path,
            scope = scope,
        )
    }

    private suspend fun updateWithResult(
        transform: (FolderDisplaySettings) -> FolderSortSettingsService.UpdateResult,
    ): Boolean {
        var result: FolderSortSettingsService.UpdateResult? = null
        settingsRepository.updateFolderDisplaySettings { current ->
            transform(current).also { result = it }.settings
        }
        return result?.isSortTypeChanged ?: false
    }
}
