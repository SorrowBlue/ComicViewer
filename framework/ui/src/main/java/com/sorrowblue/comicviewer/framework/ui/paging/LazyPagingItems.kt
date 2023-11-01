package com.sorrowblue.comicviewer.framework.ui.paging

import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems

val <T : Any> LazyPagingItems<T>.isEmptyData
    get() =
        loadState.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached && loadState.source.append.endOfPaginationReached && itemCount == 0

val <T : Any> LazyPagingItems<T>.isLoadedData
    get() =
        loadState.source.refresh is LoadState.NotLoading && (loadState.mediator == null || loadState.mediator?.refresh is LoadState.NotLoading)