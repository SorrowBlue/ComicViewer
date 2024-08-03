package com.sorrowblue.comicviewer.feature.favorite.add

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.favorite.Favorite
import com.sorrowblue.comicviewer.framework.ui.SaveableScreenState
import com.sorrowblue.comicviewer.framework.ui.rememberSaveableScreenState
import kotlinx.coroutines.flow.Flow

internal interface FavoriteAddScreenState : SaveableScreenState {
    val pagingDataFlow: Flow<PagingData<Favorite>>
    fun onFavoriteClick(favorite: Favorite)
}

@Composable
internal fun rememberFavoriteAddScreenState(viewModel: FavoriteAddViewModel = hiltViewModel()): FavoriteAddScreenState =
    rememberSaveableScreenState {
        FavoriteAddScreenStateImpl(savedStateHandle = it, viewModel = viewModel)
    }

private class FavoriteAddScreenStateImpl(
    override val savedStateHandle: SavedStateHandle,
    private val viewModel: FavoriteAddViewModel,
) : FavoriteAddScreenState {

    override val pagingDataFlow = viewModel.pagingDataFlow

    override fun onFavoriteClick(favorite: Favorite) {
        viewModel.update(favorite)
    }
}
