/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.history

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.framework.ui.adaptive.AdaptiveNavigationSuiteScaffoldState
import com.sorrowblue.comicviewer.framework.ui.adaptive.rememberAdaptiveNavigationSuiteScaffoldState
import dev.zacsweers.metrox.viewmodel.metroViewModel
import kotlinx.coroutines.launch

internal interface HistoryScreenState {
    val lazyPagingItems: LazyPagingItems<Book>
    val lazyGridState: LazyGridState
    val scaffoldState: AdaptiveNavigationSuiteScaffoldState

    fun onNavResult(result: Boolean)
}

@Composable
internal fun rememberHistoryScreenState(
    viewModel: HistoryViewModel = metroViewModel(),
): HistoryScreenState {
    val lazyGridState = rememberLazyGridState()
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberAdaptiveNavigationSuiteScaffoldState(
        onNavigationReSelect = {
            if (lazyGridState.canScrollBackward) {
                scope.launch {
                    lazyGridState.animateScrollToItem(0)
                }
            }
        },
    )
    return remember(lazyGridState, scaffoldState) {
        HistoryScreenStateImpl(
            lazyGridState = lazyGridState,
            scaffoldState = scaffoldState,
            clearAll = viewModel::clearAll,
        )
    }.apply {
        lazyPagingItems = viewModel.pagingDataFlow.collectAsLazyPagingItems()
    }
}

private class HistoryScreenStateImpl(
    override val lazyGridState: LazyGridState,
    override val scaffoldState: AdaptiveNavigationSuiteScaffoldState,
    private var clearAll: () -> Unit,
) : HistoryScreenState {

    override lateinit var lazyPagingItems: LazyPagingItems<Book>

    override fun onNavResult(result: Boolean) {
        if (result) {
            clearAll()
        }
    }
}
