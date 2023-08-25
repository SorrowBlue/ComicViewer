package com.sorrowblue.comicviewer.framework.compose

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems

val <T : Any> LazyPagingItems<T>.isEmptyData
    get() =
        loadState.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached &&loadState.source.append.endOfPaginationReached && itemCount == 0

val <T : Any> LazyPagingItems<T>.isLoadedData
    get() =
        loadState.source.refresh is LoadState.NotLoading && loadState.mediator?.refresh is LoadState.NotLoading

@Composable
fun LazyGridState.isScrollingUp(): Boolean {
    var previousIndex by remember(this) { mutableIntStateOf(firstVisibleItemIndex) }
    var previousScrollOffset by remember(this) { mutableIntStateOf(firstVisibleItemScrollOffset) }
    return remember(this) {
        derivedStateOf {
            if (!canScrollBackward) {
                true
            } else if (!canScrollForward) {
                false
            } else if (previousIndex != firstVisibleItemIndex) {
                previousIndex > firstVisibleItemIndex
            } else {
                previousScrollOffset >= firstVisibleItemScrollOffset
            }.also {
                previousIndex = firstVisibleItemIndex
                previousScrollOffset = firstVisibleItemScrollOffset
            }
        }
    }.value
}
