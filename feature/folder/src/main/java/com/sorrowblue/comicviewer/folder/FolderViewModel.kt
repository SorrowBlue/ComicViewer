package com.sorrowblue.comicviewer.folder

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.settings.FolderDisplaySettings
import com.sorrowblue.comicviewer.domain.model.settings.SortType
import com.sorrowblue.comicviewer.domain.usecase.AddReadLaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.GetFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageFolderDisplaySettingsUseCase
import com.sorrowblue.comicviewer.folder.navigation.FolderArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flattenConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@HiltViewModel
internal class FolderViewModel @Inject constructor(
    val getFileUseCase: GetFileUseCase,
    pagingFileUseCase: PagingFileUseCase,
    val displaySettingsUseCase: ManageFolderDisplaySettingsUseCase,
    private val addReadLaterUseCase: AddReadLaterUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val args = FolderArgs(savedStateHandle)
    val bookshelfId = args.bookshelfId
    val path = args.path
    var restorePath = args.restorePath

    @OptIn(ExperimentalCoroutinesApi::class)
    val pagingDataFlow: Flow<PagingData<File>> = pagingFileUseCase.execute(
        PagingFileUseCase.Request(PagingConfig(30), bookshelfId, path)
    ).filterSuccess().flattenConcat().cachedIn(viewModelScope)

    val displaySettings = displaySettingsUseCase.settings

    fun updateDisplaySettings(folderDisplaySettings: (FolderDisplaySettings) -> FolderDisplaySettings) {
        viewModelScope.launch {
            displaySettingsUseCase.edit(folderDisplaySettings)
        }
    }

    val sort: StateFlow<SortType> =
        displaySettingsUseCase.settings.map { it.sortType }
            .stateIn(
                viewModelScope,
                SharingStarted.Eagerly,
                runBlocking { displaySettingsUseCase.settings.first().sortType }
            )

    fun toggleFileListType() {
        viewModelScope.launch {
            displaySettingsUseCase.edit {
                it.copy(
                    display = when (it.display) {
                        FolderDisplaySettings.Display.GRID -> FolderDisplaySettings.Display.LIST
                        FolderDisplaySettings.Display.LIST -> FolderDisplaySettings.Display.GRID
                    }
                )
            }
        }
    }

    fun addToReadLater(file: File) {
        viewModelScope.launch {
            addReadLaterUseCase.execute(AddReadLaterUseCase.Request(file.bookshelfId, file.path))
                .first()
        }
    }

    fun updateDisplay(display: FolderDisplaySettings.Display) {
        viewModelScope.launch {
            displaySettingsUseCase.edit {
                it.copy(display = display)
            }
        }
    }

    fun updateGridSize() {
        viewModelScope.launch {
            displaySettingsUseCase.edit {
                it.copy(
                    columnSize = when (it.columnSize) {
                        FolderDisplaySettings.Size.MEDIUM -> FolderDisplaySettings.Size.LARGE
                        FolderDisplaySettings.Size.LARGE -> FolderDisplaySettings.Size.MEDIUM
                    }
                )
            }
        }
    }
}

private fun <T, E : Resource.AppError> Flow<Resource<T, E>>.filterSuccess(): Flow<T> {
    return filter {
        it is Resource.Success<T>
    }.map {
        (it as Resource.Success<T>).data
    }
}
