package com.sorrowblue.comicviewer.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteId
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.settings.folder.FileListDisplay
import com.sorrowblue.comicviewer.domain.model.settings.folder.GridColumnSize
import com.sorrowblue.comicviewer.domain.usecase.favorite.DeleteFavoriteUseCase
import com.sorrowblue.comicviewer.domain.usecase.favorite.GetFavoriteUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.AddReadLaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.ExistsReadlaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.GetFileAttributeUseCase
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingFavoriteFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageFolderDisplaySettingsUseCase
import com.sorrowblue.comicviewer.file.FileInfoUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@HiltViewModel
internal class FavoriteViewModel @Inject constructor(
    val getFavoriteUseCase: GetFavoriteUseCase,
    private val pagingFavoriteFileUseCase: PagingFavoriteFileUseCase,
    private val displaySettingsUseCase: ManageFolderDisplaySettingsUseCase,
    private val deleteFavoriteUseCase: DeleteFavoriteUseCase,
    private val addReadLaterUseCase: AddReadLaterUseCase,
    private val getFileAttributeUseCase: GetFileAttributeUseCase,
    private val existsReadlaterUseCase: ExistsReadlaterUseCase,
) : ViewModel() {

    val displaySettings = displaySettingsUseCase.settings

    fun pagingDataFlow(favoriteId: FavoriteId): Flow<PagingData<File>> = pagingFavoriteFileUseCase
        .execute(PagingFavoriteFileUseCase.Request(PagingConfig(20), favoriteId))
        .cachedIn(viewModelScope)

    fun delete(favoriteId: FavoriteId, done: () -> Unit) {
        viewModelScope.launch {
            deleteFavoriteUseCase.execute(DeleteFavoriteUseCase.Request(favoriteId))
                .collect()
            done()
        }
    }

    fun addToReadLater(file: File) {
        viewModelScope.launch {
            addReadLaterUseCase.execute(AddReadLaterUseCase.Request(file.bookshelfId, file.path))
                .first()
        }
    }

    fun updateGridSize() {
        viewModelScope.launch {
            displaySettingsUseCase.edit {
                it.copy(
                    gridColumnSize = when (it.gridColumnSize) {
                        GridColumnSize.Medium -> GridColumnSize.Large
                        GridColumnSize.Large -> GridColumnSize.Medium
                    }
                )
            }
        }
    }

    fun fileInfo(file: File): Flow<Resource<FileInfoUiState, Resource.AppError>> {
        val getRequest = GetFileAttributeUseCase.Request(file.bookshelfId, file.path)
        val isRequest = ExistsReadlaterUseCase.Request(file.bookshelfId, file.path)

        return getFileAttributeUseCase(getRequest)
            .combine(existsReadlaterUseCase(isRequest)) { a, b ->
                if (a is Resource.Success && b is Resource.Success) {
                    Resource.Success(FileInfoUiState(file, a.data, b.data))
                } else {
                    Resource.Error(GetFileAttributeUseCase.Error.NotFound)
                }
            }
    }

    fun updateDisplay(display: FileListDisplay) {
        viewModelScope.launch {
            displaySettingsUseCase.edit {
                it.copy(fileListDisplay = display)
            }
        }
    }
}
