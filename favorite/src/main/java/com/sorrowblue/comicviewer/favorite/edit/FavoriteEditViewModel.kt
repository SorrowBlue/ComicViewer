package com.sorrowblue.comicviewer.favorite.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.sorrowblue.comicviewer.domain.entity.favorite.FavoriteFile
import com.sorrowblue.comicviewer.domain.entity.favorite.FavoriteId
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.domain.usecase.favorite.GetFavoriteUseCase
import com.sorrowblue.comicviewer.domain.usecase.favorite.RemoveFavoriteFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.favorite.UpdateFavoriteUseCase
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingFavoriteFileUseCase
import com.sorrowblue.comicviewer.framework.ui.flow.mutableStateIn
import com.sorrowblue.comicviewer.framework.ui.fragment.PagingViewModel
import com.sorrowblue.comicviewer.framework.ui.navigation.SupportSafeArgs
import com.sorrowblue.comicviewer.framework.ui.navigation.navArgs
import com.sorrowblue.comicviewer.framework.ui.navigation.stateIn
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch

@HiltViewModel
internal class FavoriteEditViewModel @Inject constructor(
    pagingFavoriteFileUseCase: PagingFavoriteFileUseCase,
    private val getFavoriteUseCase: GetFavoriteUseCase,
    private val removeFavoriteFileUseCase: RemoveFavoriteFileUseCase,
    private val updateFavoriteUseCase: UpdateFavoriteUseCase,
    override val savedStateHandle: SavedStateHandle
) : PagingViewModel<File>(), SupportSafeArgs {

    private val args: FavoriteEditFragmentArgs by navArgs()
    private val favoriteId = FavoriteId(args.favoriteId)
    private val favoriteFlow =
        getFavoriteUseCase.execute(GetFavoriteUseCase.Request(favoriteId)).stateIn { null }

    override val transitionName: String? = null
    override val pagingDataFlow = pagingFavoriteFileUseCase.execute(
        PagingFavoriteFileUseCase.Request(PagingConfig(20), favoriteId)
    ).cachedIn(viewModelScope)

    val titleFlow = favoriteFlow.mapNotNull { it?.dataOrNull?.name }.mutableStateIn("")

    init {
        viewModelScope.launch {
            getFavoriteUseCase.execute(GetFavoriteUseCase.Request(favoriteId))
        }
    }

    fun removeFile(file: File) {
        val request =
            RemoveFavoriteFileUseCase.Request(FavoriteFile(favoriteId, file.bookshelfId, file.path))

        viewModelScope.launch {
            removeFavoriteFileUseCase.execute(request).collect()
        }
    }

    fun save(done: () -> Unit) {
        viewModelScope.launch {
            val favorite = favoriteFlow.replayCache.firstOrNull()?.dataOrNull ?: return@launch
            updateFavoriteUseCase.execute(UpdateFavoriteUseCase.Request(favorite.copy(name = titleFlow.value)))
                .collect()
            done()
        }
    }
}
