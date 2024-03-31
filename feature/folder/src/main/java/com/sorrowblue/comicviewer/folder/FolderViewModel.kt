package com.sorrowblue.comicviewer.folder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.settings.FolderDisplaySettings
import com.sorrowblue.comicviewer.domain.model.settings.SortType
import com.sorrowblue.comicviewer.domain.usecase.file.AddReadLaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.DeleteReadLaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.ExistsReadlaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.GetFileAttributeUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.GetFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageFolderDisplaySettingsUseCase
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
    private val pagingFileUseCase: PagingFileUseCase,
    val displaySettingsUseCase: ManageFolderDisplaySettingsUseCase,
    val addReadLaterUseCase: AddReadLaterUseCase,
    val deleteReadLaterUseCase: DeleteReadLaterUseCase,
    val getFileAttributeUseCase: GetFileAttributeUseCase,
    val existsReadlaterUseCase: ExistsReadlaterUseCase,
) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    fun pagingDataFlow(bookshelfId: BookshelfId, path: String) =
        pagingFileUseCase.execute(
            PagingFileUseCase.Request(PagingConfig(30), bookshelfId, path)
        ).filterSuccess().flattenConcat().cachedIn(viewModelScope)

    val displaySettings = displaySettingsUseCase.settings

    val sort: StateFlow<SortType> =
        displaySettingsUseCase.settings.map { it.sortType }
            .stateIn(
                viewModelScope,
                SharingStarted.Eagerly,
                runBlocking { displaySettingsUseCase.settings.first().sortType }
            )

    val showHidden: StateFlow<Boolean> =
        displaySettingsUseCase.settings.map { it.showHiddenFile }
            .stateIn(
                viewModelScope,
                SharingStarted.Eagerly,
                runBlocking { displaySettingsUseCase.settings.first().showHiddenFile }
            )

    fun readLater(file: File, isAdd: Boolean) {
        viewModelScope.launch {
            if (isAdd) {
                addReadLaterUseCase(
                    AddReadLaterUseCase.Request(
                        file.bookshelfId,
                        file.path
                    )
                ).first()
            } else {
                deleteReadLaterUseCase(
                    DeleteReadLaterUseCase.Request(
                        file.bookshelfId,
                        file.path
                    )
                ).first()
            }
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
                        FolderDisplaySettings.ColumnSize.Medium -> FolderDisplaySettings.ColumnSize.Large
                        FolderDisplaySettings.ColumnSize.Large -> FolderDisplaySettings.ColumnSize.Medium
                    }
                )
            }
        }
    }

    fun updateShowHide(value: Boolean) {
        viewModelScope.launch {
            displaySettingsUseCase.edit {
                it.copy(showHiddenFile = value)
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
