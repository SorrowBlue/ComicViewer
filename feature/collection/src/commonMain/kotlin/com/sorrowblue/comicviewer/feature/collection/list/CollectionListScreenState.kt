/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.collection.list

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.sorrowblue.comicviewer.domain.model.collection.Collection
import com.sorrowblue.comicviewer.framework.ui.adaptive.AdaptiveNavigationSuiteScaffoldState
import com.sorrowblue.comicviewer.framework.ui.adaptive.rememberAdaptiveNavigationSuiteScaffoldState
import dev.zacsweers.metrox.viewmodel.metroViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal interface CollectionListScreenState {
    val scaffoldState: AdaptiveNavigationSuiteScaffoldState
    val lazyPagingItems: LazyPagingItems<Collection>
    val lazyListState: LazyListState

    fun onNavClick()
}

@Composable
internal fun rememberCollectionListScreenState(viewModel: CollectionListViewModel = metroViewModel()): CollectionListScreenState {
    val lazyListState = rememberLazyListState()
    val scaffoldState = rememberAdaptiveNavigationSuiteScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    return remember {
        CollectionListScreenStateImpl(
            lazyListState = lazyListState,
            scaffoldState = scaffoldState,
            coroutineScope = coroutineScope,
        )
    }.apply {
        lazyPagingItems = viewModel.pagingDataFlow.collectAsLazyPagingItems()
    }
}

@Stable
private class CollectionListScreenStateImpl(
    override val lazyListState: LazyListState,
    override val scaffoldState: AdaptiveNavigationSuiteScaffoldState,
    val coroutineScope: CoroutineScope,
) : CollectionListScreenState {

    override lateinit var lazyPagingItems: LazyPagingItems<Collection>

    override fun onNavClick() {
        if (lazyListState.canScrollBackward) {
            coroutineScope.launch {
                lazyListState.scrollToItem(0)
            }
        }
    }
}
