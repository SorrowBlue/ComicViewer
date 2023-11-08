package com.sorrowblue.comicviewer.folder

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.onSuccess
import com.sorrowblue.comicviewer.domain.model.settings.FolderDisplaySettings
import com.sorrowblue.comicviewer.domain.model.settings.SortType
import com.sorrowblue.comicviewer.domain.usecase.AddReadLaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.GetFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageFolderDisplaySettingsUseCase
import com.sorrowblue.comicviewer.file.component.toFileContentLayout
import com.sorrowblue.comicviewer.folder.navigation.FolderArgs
import com.sorrowblue.comicviewer.folder.section.FolderAppBarUiState
import com.sorrowblue.comicviewer.folder.section.Sort
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flattenConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@HiltViewModel
internal class FolderViewModel @Inject constructor(
    getFileUseCase: GetFileUseCase,
    pagingFileUseCase: PagingFileUseCase,
    private val displaySettingsUseCase: ManageFolderDisplaySettingsUseCase,
    private val addReadLaterUseCase: AddReadLaterUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val args = FolderArgs(savedStateHandle)
    val bookshelfId = args.bookshelfId
    val path = args.path
    var position = args.position

    @OptIn(ExperimentalCoroutinesApi::class)
    val pagingDataFlow: Flow<PagingData<File>> = pagingFileUseCase.execute(
        PagingFileUseCase.Request(PagingConfig(30), bookshelfId, path)
    ).filterSuccess().flattenConcat().cachedIn(viewModelScope)

    private val _uiState = MutableStateFlow(
        FolderScreenUiState(
            folderAppBarUiState = FolderAppBarUiState(
                "",
                runBlocking { displaySettingsUseCase.settings.first().toFileContentLayout() }
            ),
            fileContentType = runBlocking {
                displaySettingsUseCase.settings.first().toFileContentLayout()
            }
        )
    )

    init {
        viewModelScope.launch {
            displaySettingsUseCase.settings.map(FolderDisplaySettings::toFileContentLayout)
                .distinctUntilChanged().collectLatest {
                    _uiState.value = _uiState.value.copy(
                        folderAppBarUiState = _uiState.value.folderAppBarUiState.copy(
                            fileContentType = it
                        ),
                        fileContentType = it
                    )
                }
        }
        viewModelScope.launch {
            getFileUseCase.execute(GetFileUseCase.Request(bookshelfId, path)).first().onSuccess {
                _uiState.value = _uiState.value.copy(
                    folderAppBarUiState = _uiState.value.folderAppBarUiState.copy(title = it.name)
                )
            }
        }
    }

    val uiState = _uiState.asStateFlow()

    var isSkipFirstRefresh = true
    var isScrollableTop = false

    private val displaySettings = displaySettingsUseCase.settings

    fun openSort() {
        val uiState = _uiState.value
        viewModelScope.launch {
            _uiState.value = uiState.copy(
                openSortSheet = true,
                currentSort = displaySettings.first().sortType.toSort()
            )
        }
    }

    val sort =
        displaySettingsUseCase.settings.distinctUntilChangedBy(FolderDisplaySettings::sortType)
            .map { it.sortType.toSort() }
            .stateIn(
                viewModelScope,
                SharingStarted.Eagerly,
                runBlocking { displaySettingsUseCase.settings.first().sortType.toSort() }
            )

    fun onSortChange(sort: Sort) {
        isScrollableTop = true
        isSkipFirstRefresh = false
        onSortSheetDismissRequest()
        viewModelScope.launch {
            val sortType = when (sort) {
                Sort.NAME_ASC -> SortType.NAME(true)
                Sort.NAME_DESC -> SortType.NAME(false)
                Sort.SIZE_DESC -> SortType.SIZE(true)
                Sort.SIZE_ASC -> SortType.SIZE(false)
                Sort.DATE_ASC -> SortType.DATE(true)
                Sort.DATE_DESC -> SortType.DATE(false)
            }
            displaySettingsUseCase.edit { it.copy(sortType = sortType) }
        }
    }

    fun onSortSheetDismissRequest() {
        val uiState = _uiState.value
        _uiState.value = uiState.copy(openSortSheet = false)
    }

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

    fun onGridSizeChange() {
        viewModelScope.launch {
            displaySettingsUseCase.edit {
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

    fun onReadLaterClick(file: File) {
        viewModelScope.launch {
            addReadLaterUseCase.execute(AddReadLaterUseCase.Request(file.bookshelfId, file.path))
                .first()
        }
    }
}

private fun SortType.toSort(): Sort {
    return when (this) {
        is SortType.DATE -> if (isAsc) Sort.DATE_ASC else Sort.DATE_DESC
        is SortType.NAME -> if (isAsc) Sort.NAME_ASC else Sort.NAME_DESC
        is SortType.SIZE -> if (isAsc) Sort.SIZE_ASC else Sort.SIZE_DESC
    }
}

private fun <T, E : Resource.AppError> Flow<Resource<T, E>>.filterSuccess(): Flow<T> {
    return filter {
        it is Resource.Success<T>
    }.map {
        (it as Resource.Success<T>).data
    }
}
