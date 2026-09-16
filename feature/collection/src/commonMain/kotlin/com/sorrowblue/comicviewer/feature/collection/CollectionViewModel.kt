/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.collection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.domain.model.common.dataOrNull
import com.sorrowblue.comicviewer.domain.model.settings.folder.FileListDisplay
import com.sorrowblue.comicviewer.domain.model.settings.folder.GridColumnSize
import com.sorrowblue.comicviewer.domain.usecase.collection.GetCollectionUseCase
import com.sorrowblue.comicviewer.domain.usecase.collection.PagingCollectionFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageFolderDisplaySettingsUseCase
import com.sorrowblue.comicviewer.feature.collection.section.CollectionAppBarUiState
import com.sorrowblue.comicviewer.framework.ui.component.file.FileLazyVerticalGridUiState
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactory
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactoryKey
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@AssistedInject
internal class CollectionViewModel(
    @Assisted val id: CollectionId,
    getCollectionUseCase: GetCollectionUseCase,
    pagingCollectionFileUseCase: PagingCollectionFileUseCase,
    private val folderDisplaySettingsUseCase: ManageFolderDisplaySettingsUseCase,
) : ViewModel() {

    val uiState =
        combine(
            getCollectionUseCase(id).mapNotNull { it.dataOrNull() },
            folderDisplaySettingsUseCase.settings.distinctUntilChanged(),
        ) { collection, settings ->
            CollectionScreenUiState(
                collection = collection,
                appBarUiState = CollectionAppBarUiState(
                    title = collection.name,
                    fileListDisplay = settings.fileListDisplay,
                ),
                fileLazyVerticalGridUiState = FileLazyVerticalGridUiState(
                    fileListDisplay = settings.fileListDisplay,
                    columnSize = settings.gridColumnSize,
                    imageScale = settings.imageScale,
                    imageFilterQuality = settings.imageFilterQuality,
                    fontSize = settings.fontSize,
                ),
            )
        }.stateIn(viewModelScope, SharingStarted.Eagerly, CollectionScreenUiState())

    val pagingDataFlow = pagingCollectionFileUseCase(
        PagingCollectionFileUseCase.Request(id, PagingConfig(20)),
    ).cachedIn(viewModelScope)

    fun onFileListDisplayClick() {
        viewModelScope.launch {
            folderDisplaySettingsUseCase.edit {
                it.copy(
                    fileListDisplay = when (it.fileListDisplay) {
                        FileListDisplay.Grid -> FileListDisplay.List
                        FileListDisplay.List -> FileListDisplay.Grid
                    },
                )
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

    @AssistedFactory
    @ManualViewModelAssistedFactoryKey
    @ContributesIntoMap(AppScope::class)
    interface Factory : ManualViewModelAssistedFactory {
        fun create(id: CollectionId): CollectionViewModel
    }
}
