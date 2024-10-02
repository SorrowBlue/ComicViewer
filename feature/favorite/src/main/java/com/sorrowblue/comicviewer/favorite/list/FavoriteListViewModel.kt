package com.sorrowblue.comicviewer.favorite.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.usecase.favorite.CreateFavoriteUseCase
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@HiltViewModel
internal class FavoriteListViewModel @Inject constructor(
    pagingFavoriteUseCase: PagingFavoriteUseCase,
    private val createFavoriteUseCase: CreateFavoriteUseCase,
) : ViewModel() {

    val pagingDataFlow =
        pagingFavoriteUseCase.execute(
            PagingFavoriteUseCase.Request(
                PagingConfig(20),
                BookshelfId(),
                ""
            )
        )
            .cachedIn(viewModelScope)

    fun create(name: String, done: () -> Unit) {
        viewModelScope.launch {
            createFavoriteUseCase(CreateFavoriteUseCase.Request(name)).first()
            done()
        }
    }
}
