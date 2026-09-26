/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.history

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.layout.LazyLayoutCacheWindow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.sorrowblue.comicviewer.domain.model.file.Book
import dev.zacsweers.metrox.viewmodel.metroViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal interface HistoryScreenState {
    val lazyPagingItems: LazyPagingItems<Book>
    val lazyGridState: LazyGridState

    fun onNavigationReSelect()
    fun onNavResult(result: ClearAllHistoryScreenResult)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun rememberHistoryScreenState(
    viewModel: HistoryViewModel = metroViewModel(),
): HistoryScreenState {
    val cacheWindow = LazyLayoutCacheWindow(ahead = 150.dp, behind = 100.dp)
    val lazyGridState = rememberLazyGridState(cacheWindow)
    val coroutineScope = rememberCoroutineScope()
    return remember(lazyGridState) {
        HistoryScreenStateImpl(
            lazyGridState = lazyGridState,
            coroutineScope = coroutineScope,
            clearAll = viewModel::clearAll,
        )
    }.apply {
        lazyPagingItems = viewModel.pagingDataFlow.collectAsLazyPagingItems()
    }
}

private class HistoryScreenStateImpl(
    override val lazyGridState: LazyGridState,
    private val coroutineScope: CoroutineScope,
    private var clearAll: () -> Unit,
) : HistoryScreenState {

    override lateinit var lazyPagingItems: LazyPagingItems<Book>

    override fun onNavigationReSelect() {
        if (lazyGridState.canScrollBackward) {
            coroutineScope.launch {
                lazyGridState.animateScrollToItem(0)
            }
        }
    }

    override fun onNavResult(result: ClearAllHistoryScreenResult) {
        if (result.confirmed) {
            clearAll()
        }
    }
}
