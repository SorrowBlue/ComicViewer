package com.sorrowblue.comicviewer.feature.favorite.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteFile
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteId
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.usecase.favorite.GetFavoriteUseCase
import com.sorrowblue.comicviewer.domain.usecase.favorite.RemoveFavoriteFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.favorite.UpdateFavoriteUseCase
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingFavoriteFileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@HiltViewModel
internal class FavoriteEditViewModel @Inject constructor(
    private val pagingFavoriteFileUseCase: PagingFavoriteFileUseCase,
    val getFavoriteUseCase: GetFavoriteUseCase,
    private val removeFavoriteFileUseCase: RemoveFavoriteFileUseCase,
    private val updateFavoriteUseCase: UpdateFavoriteUseCase,
) : ViewModel() {

    fun pagingDataFlow(favoriteId: FavoriteId) = pagingFavoriteFileUseCase.execute(
        PagingFavoriteFileUseCase.Request(PagingConfig(20), favoriteId)
    ).cachedIn(viewModelScope)

    fun removeFile(favoriteId: FavoriteId, file: File) {
        val request =
            RemoveFavoriteFileUseCase.Request(FavoriteFile(favoriteId, file.bookshelfId, file.path))

        viewModelScope.launch {
            removeFavoriteFileUseCase.execute(request).collect()
        }
    }

    fun save(favoriteId: FavoriteId, name: String, done: () -> Unit) {
        viewModelScope.launch {
            val favorite = getFavoriteUseCase.execute(GetFavoriteUseCase.Request(favoriteId))
                .first().dataOrNull ?: return@launch
            updateFavoriteUseCase.execute(UpdateFavoriteUseCase.Request(favorite.copy(name = name)))
                .collect()
            done()
        }
    }
}
