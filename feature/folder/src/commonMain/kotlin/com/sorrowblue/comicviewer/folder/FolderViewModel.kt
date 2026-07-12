/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.folder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfType
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderScopeOnly
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.GetBookshelfInfoUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.GetFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.PagingFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageFolderDisplaySettingsUseCase
import com.sorrowblue.comicviewer.folder.section.FolderAppBarUiState
import com.sorrowblue.comicviewer.folder.section.FolderListUiState
import com.sorrowblue.comicviewer.folder.sorttype.SortTypeSelectScreenResult
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactory
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactoryKey
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class)
@AssistedInject
internal class FolderViewModel(
    @Assisted private val bookshelfId: BookshelfId,
    @Assisted private val path: String,
    @Assisted private val restorePath: String?,
    @Assisted private val showSearch: Boolean,
    private val getBookshelfInfoUseCase: GetBookshelfInfoUseCase,
    private val pagingFileUseCase: PagingFileUseCase,
    private val getFileUseCase: GetFileUseCase,
    private val folderDisplaySettingsUseCase: ManageFolderDisplaySettingsUseCase,
) : ViewModel() {

    private val isPermissionGranted = MutableStateFlow(false)

    fun updatePermission(isGranted: Boolean) {
        isPermissionGranted.value = isGranted
    }

    val pagingFlow: Flow<PagingData<File>> = combine(
        getBookshelfInfoUseCase(GetBookshelfInfoUseCase.Request(bookshelfId))
            .map { resource ->
                if (resource is Resource.Success) resource.data.bookshelf else null
            }.distinctUntilChanged(),
        isPermissionGranted,
    ) { bookshelf, permissionState ->
        bookshelf to permissionState
    }.flatMapLatest { (bookshelf, permissionState) ->
        if (bookshelf == null) {
            emptyFlow()
        } else if (bookshelf.type == BookshelfType.SMB) {
            if (permissionState) {
                pagingFileUseCase(PagingFileUseCase.Request(PagingConfig(20), bookshelfId, path))
            } else {
                emptyFlow()
            }
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
                folderScopeOnly = folderDisplaySettings.folderScopeOnlyList.any { scope ->
                    scope.bookshelfId == bookshelfId && scope.path == path
                },
                sortType = folderDisplaySettings.folderScopeOnlyList
                    .find { scopeOnly ->
                        scopeOnly.bookshelfId == bookshelfId && scopeOnly.path == path
                    }?.sortType
                    ?: folderDisplaySettings.sortType,
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

    suspend fun onSortClick(sortType: SortType): Boolean {
        var refresh = false
        folderDisplaySettingsUseCase.edit { settings ->
            val beforeFolderScopeOnly =
                settings.folderScopeOnlyList.find {
                    it.bookshelfId == bookshelfId && it.path == path
                }
            when {
                uiState.value.folderAppBarUiState.folderScopeOnly -> {
                    if (beforeFolderScopeOnly == null) {
                        refresh = true
                        settings.copy(
                            folderScopeOnlyList =
                                settings.folderScopeOnlyList + FolderScopeOnly(
                                    bookshelfId,
                                    path,
                                    sortType,
                                ),
                        )
                    } else if (beforeFolderScopeOnly.sortType != sortType) {
                        refresh = true
                        val new = FolderScopeOnly(
                            bookshelfId,
                            path,
                            sortType,
                        )
                        settings.copy(
                            folderScopeOnlyList =
                                settings.folderScopeOnlyList - beforeFolderScopeOnly + new,
                        )
                    } else {
                        settings
                    }
                }

                !uiState.value.folderAppBarUiState.folderScopeOnly && beforeFolderScopeOnly != null -> {
                    refresh = true
                    settings.copy(
                        folderScopeOnlyList =
                            settings.folderScopeOnlyList - beforeFolderScopeOnly,
                    )
                }

                settings.sortType != sortType -> {
                    refresh = true
                    settings.copy(sortType = sortType)
                }

                else -> {
                    settings
                }
            }
        }
        return refresh
    }

    suspend fun onFolderScopeOnlyClick() {
        folderDisplaySettingsUseCase.edit { settings ->
            val beforeFolderScopeOnly =
                settings.folderScopeOnlyList.find {
                    it.bookshelfId == bookshelfId && it.path == path
                }
            val folderScopeOnlyList = if (beforeFolderScopeOnly == null) {
                settings.folderScopeOnlyList + FolderScopeOnly(
                    bookshelfId,
                    path,
                    settings.sortType,
                )
            } else {
                settings.folderScopeOnlyList - beforeFolderScopeOnly
            }
            settings.copy(folderScopeOnlyList = folderScopeOnlyList)
        }
    }

    suspend fun onSortTypeSelectScreenResult(result: SortTypeSelectScreenResult): Boolean {
        var refresh = false
        folderDisplaySettingsUseCase.edit { settings ->
            val beforeFolderScopeOnly =
                settings.folderScopeOnlyList.find {
                    it.bookshelfId == bookshelfId && it.path == path
                }
            when {
                result.folderScopeOnly -> {
                    if (beforeFolderScopeOnly == null) {
                        refresh = true
                        settings.copy(
                            folderScopeOnlyList =
                                settings.folderScopeOnlyList + FolderScopeOnly(
                                    bookshelfId,
                                    path,
                                    result.sortType,
                                ),
                        )
                    } else if (beforeFolderScopeOnly.sortType != result.sortType) {
                        refresh = true
                        val new = FolderScopeOnly(
                            bookshelfId,
                            path,
                            result.sortType,
                        )
                        settings.copy(
                            folderScopeOnlyList =
                                settings.folderScopeOnlyList - beforeFolderScopeOnly + new,
                        )
                    } else {
                        settings
                    }
                }

                !result.folderScopeOnly && beforeFolderScopeOnly != null -> {
                    refresh = true
                    settings.copy(
                        folderScopeOnlyList =
                            settings.folderScopeOnlyList - beforeFolderScopeOnly,
                    )
                }

                settings.sortType != result.sortType -> {
                    refresh = true
                    settings.copy(sortType = result.sortType)
                }

                else -> {
                    settings
                }
            }
        }
        return refresh
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
