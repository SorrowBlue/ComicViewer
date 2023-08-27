package com.sorrowblue.comicviewer.favorite

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.domain.entity.settings.FolderDisplaySettings
import com.sorrowblue.comicviewer.domain.usecase.favorite.DeleteFavoriteUseCase
import com.sorrowblue.comicviewer.domain.usecase.favorite.GetFavoriteUseCase
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingFavoriteFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageFolderDisplaySettingsUseCase
import com.sorrowblue.comicviewer.favorite.navigation.FavoriteArgs
import com.sorrowblue.comicviewer.folder.toFileListType
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@HiltViewModel
internal class FavoriteViewModel @Inject constructor(
    getFavoriteUseCase: GetFavoriteUseCase,
    pagingFavoriteFileUseCase: PagingFavoriteFileUseCase,
    private val displaySettingsUseCase: ManageFolderDisplaySettingsUseCase,
    private val deleteFavoriteUseCase: DeleteFavoriteUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val args = FavoriteArgs(savedStateHandle)

    val favoriteId = args.favoriteId

    private val favoriteFlow = getFavoriteUseCase.execute(GetFavoriteUseCase.Request(favoriteId))
        .mapNotNull { it.dataOrNull }

    private val _uiState = MutableStateFlow(FavoriteScreenUiState())
    val uiState = _uiState.asStateFlow()

    val pagingDataFlow: Flow<PagingData<File>> = pagingFavoriteFileUseCase
        .execute(PagingFavoriteFileUseCase.Request(PagingConfig(20), favoriteId))
        .cachedIn(viewModelScope)

    init {
        viewModelScope.launch {
            getFavoriteUseCase.execute(GetFavoriteUseCase.Request(favoriteId)).collectLatest {
                if (it.dataOrNull != null) {
                    val uiState = _uiState.value
                    _uiState.value =
                        uiState.copy(
                            favoriteAppBarUiState = uiState.favoriteAppBarUiState.copy(title = it.dataOrNull!!.name)
                        )

                }
            }
        }
        viewModelScope.launch {
            displaySettingsUseCase.settings.map(FolderDisplaySettings::toFileListType)
                .distinctUntilChanged().collectLatest {
                    _uiState.value = _uiState.value.copy(
                        favoriteAppBarUiState = _uiState.value.favoriteAppBarUiState.copy(
                            fileListType = runBlocking {
                                displaySettingsUseCase.settings.first().toFileListType()
                            }),
                        fileListType = it
                    )
                }
        }
    }

    fun delete(done: () -> Unit) {
        viewModelScope.launch {
            deleteFavoriteUseCase.execute(DeleteFavoriteUseCase.Request(favoriteId))
                .collect()
            done()
        }
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

    fun toggleGridSize() {
        viewModelScope.launch {
            displaySettingsUseCase.edit {
                it.copy(
                    spanCount = when (it.spanCount) {
                        2 -> 3
                        3 -> 4
                        else -> 2
                    }
                )
            }
        }
    }

    fun showFileInfoSheet(file: File) {

    }

}
