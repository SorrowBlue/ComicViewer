package com.sorrowblue.comicviewer.framework.preview

import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

fun <T : Any> PagingData.Companion.flowEmptyData() = flowOf(
    empty<T>(
        LoadStates(
            refresh = LoadState.NotLoading(true),
            append = LoadState.NotLoading(true),
            prepend = LoadState.NotLoading(true),
        )
    )
)

fun <T : Any> PagingData.Companion.flowData(size: Int = 20, init: (Int) -> T): Flow<PagingData<T>> {
    return flowOf(from(data = List(size, init)))
}
