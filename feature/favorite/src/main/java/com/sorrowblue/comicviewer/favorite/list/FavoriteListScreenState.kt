package com.sorrowblue.comicviewer.favorite.list

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.favorite.Favorite
import com.sorrowblue.comicviewer.feature.favorite.common.component.FavoriteCreateDialogUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

internal interface FavoriteListScreenState {
    val dialogUiState: FavoriteCreateDialogUiState
    val pagingDataFlow: Flow<PagingData<Favorite>>
    val lazyListState: LazyListState
    fun onNavClick()
}

@Composable
internal fun rememberFavoriteListScreenState(
    savedStateHandle: SavedStateHandle,
    scope: CoroutineScope = rememberCoroutineScope(),
    lazyListState: LazyListState = rememberLazyListState(),
    viewModel: FavoriteListViewModel = hiltViewModel(),
): FavoriteListScreenState = remember {
    FavoriteListScreenStateImpl(
        savedStateHandle = savedStateHandle,
        lazyListState = lazyListState,
        scope = scope,
        viewModel = viewModel,
    )
}

@OptIn(SavedStateHandleSaveableApi::class)
@Stable
private class FavoriteListScreenStateImpl(
    savedStateHandle: SavedStateHandle,
    viewModel: FavoriteListViewModel,
    override val lazyListState: LazyListState,
    private val scope: CoroutineScope,
) : FavoriteListScreenState {

    override var dialogUiState by savedStateHandle.saveable {
        mutableStateOf(FavoriteCreateDialogUiState())
    }
        private set

    override val pagingDataFlow = viewModel.pagingDataFlow

    override fun onNavClick() {
        if (lazyListState.canScrollBackward) {
            scope.launch {
                lazyListState.scrollToItem(0)
            }
        }
    }
}
