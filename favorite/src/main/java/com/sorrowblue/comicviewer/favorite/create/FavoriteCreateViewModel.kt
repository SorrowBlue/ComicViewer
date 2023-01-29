package com.sorrowblue.comicviewer.favorite.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sorrowblue.comicviewer.domain.usecase.CreateFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@HiltViewModel
internal class FavoriteCreateViewModel @Inject constructor(
    private val createFavoriteUseCase: CreateFavoriteUseCase
) : ViewModel() {

    val title = MutableStateFlow("")

    fun create(done: () -> Unit) {
        viewModelScope.launch {
            createFavoriteUseCase.execute(CreateFavoriteUseCase.Request(title.value)).collect()
            done()
        }
    }
}
