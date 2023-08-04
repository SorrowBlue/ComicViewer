package com.sorrowblue.comicviewer.framework.compose

import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import logcat.logcat

val <T : Any> LazyPagingItems<T>.isEmptyData: Boolean
    get() {
        logcat { """
            isEmpty=${loadState.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached && itemCount == 0}
            itemCount=${itemCount}
            loadState.source=${loadState.source}
            loadState.append=${loadState.append}
        """.trimIndent() }
        return loadState.refresh is LoadState.NotLoading && loadState.source.append.endOfPaginationReached && itemCount == 0
    }
