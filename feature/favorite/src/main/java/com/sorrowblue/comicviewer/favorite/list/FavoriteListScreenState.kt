package com.sorrowblue.comicviewer.favorite.list

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.favorite.Favorite
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteId
import com.sorrowblue.comicviewer.domain.usecase.favorite.DeleteFavoriteUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch

internal interface FavoriteListScreenState {
    val pagingDataFlow: Flow<PagingData<Favorite>>
    val lazyListState: LazyListState
    fun onNavClick()
    fun delete(favoriteId: FavoriteId)
}

@Composable
internal fun rememberFavoriteListScreenState(
    scope: CoroutineScope = rememberCoroutineScope(),
    lazyListState: LazyListState = rememberLazyListState(),
    viewModel: FavoriteListViewModel = hiltViewModel(),
): FavoriteListScreenState = remember {
    FavoriteListScreenStateImpl(
        scope = scope,
        deleteFavoriteUseCase = viewModel.deleteFavoriteUseCase,
        lazyListState = lazyListState,
        pagingDataFlow = viewModel.pagingDataFlow
    )
}

@Stable
private class FavoriteListScreenStateImpl(
    private val scope: CoroutineScope,
    private val deleteFavoriteUseCase: DeleteFavoriteUseCase,
    override val lazyListState: LazyListState,
    override val pagingDataFlow: Flow<PagingData<Favorite>>,
) : FavoriteListScreenState {

    override fun onNavClick() {
        if (lazyListState.canScrollBackward) {
            scope.launch {
                lazyListState.scrollToItem(0)
            }
        }
    }

    override fun delete(favoriteId: FavoriteId) {
        deleteFavoriteUseCase(DeleteFavoriteUseCase.Request(favoriteId)).launchIn(scope)
    }
}
