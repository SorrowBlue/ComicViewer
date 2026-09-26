/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.folder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfType
import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.settings.folder.FileListDisplay
import com.sorrowblue.comicviewer.domain.model.settings.folder.GridColumnSize
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.GetBookshelfInfoUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.GetFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.PagingFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageFolderDisplaySettingsUseCase
import com.sorrowblue.comicviewer.feature.folder.section.FolderAppBarUiState
import com.sorrowblue.comicviewer.feature.folder.section.FolderListUiState
import com.sorrowblue.comicviewer.framework.ui.EventFlow
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactory
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactoryKey
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal sealed interface FolderScreenUiEvent {
    data object Reload : FolderScreenUiEvent
}

@OptIn(ExperimentalCoroutinesApi::class)
@AssistedInject
internal class FolderViewModel(
    @Assisted private val bookshelfId: BookshelfId,
    @Assisted private val path: String,
    @Assisted private val restorePath: String?,
    @Assisted private val showSearch: Boolean,
    getBookshelfInfoUseCase: GetBookshelfInfoUseCase,
    private val pagingFileUseCase: PagingFileUseCase,
    getFileUseCase: GetFileUseCase,
    private val folderDisplaySettingsUseCase: ManageFolderDisplaySettingsUseCase,
) : ViewModel() {

    val events = EventFlow<FolderScreenUiEvent>()

    val pagingFlow: Flow<PagingData<File>> =
        getBookshelfInfoUseCase(bookshelfId).map { resource ->
            if (resource is Resource.Success) resource.data.bookshelf else null
        }.distinctUntilChanged().flatMapLatest { bookshelf ->
            if (bookshelf == null) {
                emptyFlow()
            } else if (bookshelf.type == BookshelfType.SMB) {
                pagingFileUseCase(PagingFileUseCase.Request(PagingConfig(20), bookshelfId, path))
            } else {
                pagingFileUseCase(PagingFileUseCase.Request(PagingConfig(20), bookshelfId, path))
            }
        }.cachedIn(viewModelScope)

    val uiState: StateFlow<FolderScreenUiState> = combine(
        folderDisplaySettingsUseCase.settings.distinctUntilChanged(),
        getFileUseCase(GetFileUseCase.Request(bookshelfId, path)),
    ) { folderDisplaySettings, fileResource ->
        val title = if (fileResource is Resource.Success) fileResource.data.name else ""
        FolderScreenUiState(
            folderAppBarUiState = FolderAppBarUiState(
                title = title,
                showSearch = showSearch,
                folderScopeOnly = folderDisplaySettings.isFolderScopeOnly(bookshelfId, path),
                includeSubfolders = folderDisplaySettings.isIncludeSubfolders(bookshelfId, path),
                sortType = folderDisplaySettings.currentSortType(bookshelfId, path),
                fileListDisplay = folderDisplaySettings.fileListDisplay,
                showHiddenFiles = folderDisplaySettings.showHiddenFiles,
            ),
            folderListUiState = FolderListUiState(
                emphasisPath = restorePath.orEmpty(),
                fileLazyVerticalGridUiState = FolderListUiState().fileLazyVerticalGridUiState.copy(
                    fileListDisplay = folderDisplaySettings.fileListDisplay,
                    columnSize = folderDisplaySettings.gridColumnSize,
                    imageScale = folderDisplaySettings.imageScale,
                    imageFilterQuality = folderDisplaySettings.imageFilterQuality,
                    fontSize = folderDisplaySettings.fontSize,
                    showThumbnails = folderDisplaySettings.showThumbnails,
                ),
            ),
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = FolderScreenUiState(
            folderAppBarUiState = FolderAppBarUiState(showSearch = showSearch),
            folderListUiState = FolderListUiState(emphasisPath = restorePath.orEmpty()),
        ),
    )

    fun onSortClick(sortType: SortType) {
        viewModelScope.launch {
            if (folderDisplaySettingsUseCase.updateSortType(bookshelfId, path, sortType)) {
                events.tryEmit(FolderScreenUiEvent.Reload)
            }
        }
    }

    fun onFolderScopeOnlyClick() {
        viewModelScope.launch {
            folderDisplaySettingsUseCase.toggleFolderScopeOnly(bookshelfId, path)
        }
    }

    fun onIncludeSubfoldersClick() {
        viewModelScope.launch {
            folderDisplaySettingsUseCase.toggleIncludeSubfolders(bookshelfId, path)
        }
    }

    fun onFileListDisplayChange(fileListDisplay: FileListDisplay) {
        viewModelScope.launch {
            folderDisplaySettingsUseCase.edit {
                it.copy(fileListDisplay = fileListDisplay)
            }
        }
    }

    fun onGridSizeClick() {
        viewModelScope.launch {
            folderDisplaySettingsUseCase.edit {
                it.copy(
                    gridColumnSize = when (it.gridColumnSize) {
                        GridColumnSize.Medium -> GridColumnSize.Large
                        GridColumnSize.Large -> GridColumnSize.Medium
                    },
                )
            }
        }
    }

    fun onHiddenFilesChange(checked: Boolean) {
        viewModelScope.launch {
            folderDisplaySettingsUseCase.edit {
                it.copy(showHiddenFiles = checked)
            }
        }
    }

    @AssistedFactory
    @ManualViewModelAssistedFactoryKey
    @ContributesIntoMap(AppScope::class)
    interface Factory : ManualViewModelAssistedFactory {
        fun create(
            bookshelfId: BookshelfId,
            path: String,
            restorePath: String?,
            showSearch: Boolean,
        ): FolderViewModel
    }
}
