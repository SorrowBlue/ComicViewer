package com.sorrowblue.comicviewer.favorite.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.sorrowblue.comicviewer.domain.entity.FavoriteBook
import com.sorrowblue.comicviewer.domain.entity.FavoriteId
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.domain.usecase.GetFavoriteUseCase
import com.sorrowblue.comicviewer.domain.usecase.RemoveFavoriteBookUseCase
import com.sorrowblue.comicviewer.domain.usecase.UpdateFavoriteUseCase
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingFavoriteBookUseCase
import com.sorrowblue.comicviewer.framework.ui.navigation.SupportSafeArgs
import com.sorrowblue.comicviewer.framework.ui.navigation.navArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch

@HiltViewModel
internal class FavoriteEditViewModel @Inject constructor(
    pagingFavoriteBookUseCase: PagingFavoriteBookUseCase,
    private val getFavoriteUseCase: GetFavoriteUseCase,
    private val removeFavoriteBookUseCase: RemoveFavoriteBookUseCase,
    private val updateFavoriteUseCase: UpdateFavoriteUseCase,
    override val savedStateHandle: SavedStateHandle
) : ViewModel(), SupportSafeArgs {

    fun remove(it: File) {
        viewModelScope.launch {
            removeFavoriteBookUseCase.execute(
                RemoveFavoriteBookUseCase.Request(FavoriteBook(favoriteId, it.serverId, it.path))
            ).collect()
        }
    }

    private val args: FavoriteEditFragmentArgs by navArgs()

    private val favoriteId = FavoriteId(args.favoriteId)

    val favoriteFlow = getFavoriteUseCase.source

    val title = MutableStateFlow("")

    fun save(done: () -> Unit) {
        viewModelScope.launch {
            val favorite = favoriteFlow.replayCache.firstOrNull()?.dataOrNull ?: return@launch
            updateFavoriteUseCase.execute(UpdateFavoriteUseCase.Request(favorite.copy(name = title.value))).collect()
            done()
        }
    }

    init {
        viewModelScope.launch {
            getFavoriteUseCase.execute(GetFavoriteUseCase.Request(FavoriteId(args.favoriteId)))
        }
        viewModelScope.launch {
            favoriteFlow.mapNotNull { it.dataOrNull }.collectLatest {
                title.value = it.name
            }
        }
    }

    val pagingDataFlow = pagingFavoriteBookUseCase.execute(
        PagingFavoriteBookUseCase.Request(PagingConfig(20), FavoriteId(args.favoriteId))
    ).cachedIn(viewModelScope)
}
