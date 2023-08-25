package com.sorrowblue.comicviewer.favorite.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.sorrowblue.comicviewer.domain.usecase.favorite.CreateFavoriteUseCase
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@HiltViewModel
internal class FavoriteListViewModel @Inject constructor(
    pagingFavoriteUseCase: PagingFavoriteUseCase,
    private val createFavoriteUseCase: CreateFavoriteUseCase
) : ViewModel() {

    val pagingDataFlow =
        pagingFavoriteUseCase.execute(PagingFavoriteUseCase.Request(PagingConfig(20)))
            .cachedIn(viewModelScope)

    private val _text = MutableStateFlow("")
    val text = _text.asStateFlow()

    private val _isShowCreateDialog = MutableStateFlow(false)
    val isShowCreateDialog = _isShowCreateDialog.asStateFlow()

    fun onChangeDialog(value: Boolean) {
        _isShowCreateDialog.value = value
    }

    fun onChangeText(text: String) {
        _text.value =text
    }

    fun create() {
        viewModelScope.launch {
            createFavoriteUseCase.execute(CreateFavoriteUseCase.Request(text.value)).collect()
        }
    }
}
