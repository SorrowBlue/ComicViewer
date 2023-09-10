package com.sorrowblue.comicviewer.feature.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.domain.entity.settings.FolderDisplaySettings
import com.sorrowblue.comicviewer.domain.usecase.AddReadLaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingHistoryBookUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageFolderDisplaySettingsUseCase
import com.sorrowblue.comicviewer.file.component.FileContentUiState
import com.sorrowblue.comicviewer.file.component.toFileContentLayout
import com.sorrowblue.comicviewer.file.component.FileInfoSheetUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@HiltViewModel
internal class HistoryViewModel @Inject constructor(
    pagingHistoryBookUseCase: PagingHistoryBookUseCase,
    private val manageFolderDisplaySettingsUseCase: ManageFolderDisplaySettingsUseCase,
    private val addReadLaterUseCase: AddReadLaterUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ReadLaterScreenUiState(
            FileInfoSheetUiState.Hide,
            FileContentUiState(runBlocking {
                manageFolderDisplaySettingsUseCase.settings.first().toFileContentLayout()
            })
        )
    )
    val uiState = _uiState.asStateFlow()


    init {
        viewModelScope.launch {
            manageFolderDisplaySettingsUseCase.settings.map(FolderDisplaySettings::toFileContentLayout)
                .distinctUntilChanged().collectLatest {
                    _uiState.value = _uiState.value.copy(
                        fileContentUiState = _uiState.value.fileContentUiState.copy(layout = it)
                    )
                }
        }
    }

    val pagingDataFlow = pagingHistoryBookUseCase
        .execute(PagingHistoryBookUseCase.Request(PagingConfig(20)))
        .cachedIn(viewModelScope)


    fun toggleDisplay() {
        viewModelScope.launch {
            manageFolderDisplaySettingsUseCase.edit {
                it.copy(
                    display = when (it.display) {
                        FolderDisplaySettings.Display.GRID -> FolderDisplaySettings.Display.LIST
                        FolderDisplaySettings.Display.LIST -> FolderDisplaySettings.Display.GRID
                    }
                )
            }
        }
    }

    fun onGridSizeChange() {
        viewModelScope.launch {
            manageFolderDisplaySettingsUseCase.edit {
                it.copy(
                    columnSize = when (it.columnSize) {
                        FolderDisplaySettings.Size.SMALL -> FolderDisplaySettings.Size.LARGE
                        FolderDisplaySettings.Size.MEDIUM -> FolderDisplaySettings.Size.SMALL
                        FolderDisplaySettings.Size.LARGE -> FolderDisplaySettings.Size.MEDIUM
                    }
                )
            }
        }
    }

    fun onFileInfoDismissRequest() {
        _uiState.value = _uiState.value.copy(fileInfoSheetUiState = FileInfoSheetUiState.Hide)
    }

    fun onFileLongClick(file: File) {
        _uiState.value = _uiState.value.copy(fileInfoSheetUiState = FileInfoSheetUiState.Show(file))
    }


    fun addsReadLater(file: File) {
        viewModelScope.launch {
            addReadLaterUseCase.execute(AddReadLaterUseCase.Request(file.bookshelfId, file.path))
                .first()
        }
    }
}