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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

internal interface FavoriteListScreenState {
    val pagingDataFlow: Flow<PagingData<Favorite>>
    val lazyListState: LazyListState
    fun onNavClick()
}

@Composable
internal fun rememberFavoriteListScreenState(
    scope: CoroutineScope = rememberCoroutineScope(),
    lazyListState: LazyListState = rememberLazyListState(),
    viewModel: FavoriteListViewModel = hiltViewModel(),
): FavoriteListScreenState = remember {
    FavoriteListScreenStateImpl(
        lazyListState = lazyListState,
        scope = scope,
        viewModel = viewModel,
    )
}

@Stable
private class FavoriteListScreenStateImpl(
    viewModel: FavoriteListViewModel,
    override val lazyListState: LazyListState,
    private val scope: CoroutineScope,
) : FavoriteListScreenState {

    override val pagingDataFlow = viewModel.pagingDataFlow

    override fun onNavClick() {
        if (lazyListState.canScrollBackward) {
            scope.launch {
                lazyListState.scrollToItem(0)
            }
        }
    }
}
