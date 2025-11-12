package com.sorrowblue.comicviewer.framework.ui.preview.fake

import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

fun <T : Any> PagingData.Companion.flowEmptyData() = MutableStateFlow(
    empty<T>(
        LoadStates(
            refresh = LoadState.NotLoading(true),
            append = LoadState.NotLoading(true),
            prepend = LoadState.NotLoading(true),
        ),
    ),
)

fun <T : Any> PagingData.Companion.flowLoadingData() = MutableStateFlow(
    empty<T>(
        sourceLoadStates = LoadStates(
            LoadState.Loading,
            LoadState.NotLoading(false),
            LoadState.NotLoading(false),
        ),
    ),
)

fun <T : Any> PagingData.Companion.flowData(size: Int = 20, init: (Int) -> T): Flow<PagingData<T>> =
    MutableStateFlow(from(data = List(size, init)))
