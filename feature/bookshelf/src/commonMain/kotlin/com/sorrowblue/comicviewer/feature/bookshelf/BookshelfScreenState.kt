/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.bookshelf

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.framework.ui.adaptive.AdaptiveNavigationSuiteScaffoldState
import com.sorrowblue.comicviewer.framework.ui.adaptive.rememberAdaptiveNavigationSuiteScaffoldState
import dev.zacsweers.metrox.viewmodel.metroViewModel
import kotlinx.coroutines.launch

internal interface BookshelfScreenState {
    val lazyPagingItems: LazyPagingItems<BookshelfFolder>
    val scaffoldState: AdaptiveNavigationSuiteScaffoldState
    val lazyGridState: LazyGridState
}

@Composable
internal fun rememberBookshelfScreenState(
    viewModel: BookshelfViewModel = metroViewModel(),
): BookshelfScreenState {
    val lazyGridState = rememberLazyGridState()
    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberAdaptiveNavigationSuiteScaffoldState(onNavigationReSelect = {
        if (lazyGridState.canScrollBackward) {
            coroutineScope.launch {
                lazyGridState.animateScrollToItem(0)
            }
        }
    })
    return remember(viewModel) {
        BookshelfScreenStateImpl(
            lazyGridState = lazyGridState,
            scaffoldState = scaffoldState,
        )
    }.apply {
        lazyPagingItems = viewModel.bookshelfPagingFlow.collectAsLazyPagingItems()
    }
}

private class BookshelfScreenStateImpl(
    override val lazyGridState: LazyGridState,
    override val scaffoldState: AdaptiveNavigationSuiteScaffoldState,
) : BookshelfScreenState {
    override lateinit var lazyPagingItems: LazyPagingItems<BookshelfFolder>
}
