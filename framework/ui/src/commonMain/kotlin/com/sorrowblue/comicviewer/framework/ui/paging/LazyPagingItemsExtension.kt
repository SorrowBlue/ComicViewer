package com.sorrowblue.comicviewer.framework.ui.paging

import androidx.paging.LoadState

val <T : Any> LazyPagingItems<T>.isEmptyData
    get() =
        loadState.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached && loadState.source.append.endOfPaginationReached && itemCount == 0

val <T : Any> LazyPagingItems<T>.isLoadedData
    get() =
        loadState.source.refresh is LoadState.NotLoading && (loadState.mediator == null || loadState.mediator!!.refresh is LoadState.NotLoading)

fun <T : Any> LazyPagingItems<T>.indexOf(op: (T?) -> Boolean): Int {
    for (i in 0..<itemCount) {
        if (op(get(i))) {
            return i
        }
    }
    return -1
}
