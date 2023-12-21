package com.sorrowblue.comicviewer.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteId
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.settings.FolderDisplaySettings
import com.sorrowblue.comicviewer.domain.usecase.AddReadLaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.favorite.DeleteFavoriteUseCase
import com.sorrowblue.comicviewer.domain.usecase.favorite.GetFavoriteUseCase
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingFavoriteFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageFolderDisplaySettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@HiltViewModel
internal class FavoriteViewModel @Inject constructor(
    val getFavoriteUseCase: GetFavoriteUseCase,
    private val pagingFavoriteFileUseCase: PagingFavoriteFileUseCase,
    private val displaySettingsUseCase: ManageFolderDisplaySettingsUseCase,
    private val deleteFavoriteUseCase: DeleteFavoriteUseCase,
    private val addReadLaterUseCase: AddReadLaterUseCase,
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
                    columnSize = when (it.columnSize) {
                        FolderDisplaySettings.Size.MEDIUM -> FolderDisplaySettings.Size.LARGE
                        FolderDisplaySettings.Size.LARGE -> FolderDisplaySettings.Size.MEDIUM
                    }
                )
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
}
