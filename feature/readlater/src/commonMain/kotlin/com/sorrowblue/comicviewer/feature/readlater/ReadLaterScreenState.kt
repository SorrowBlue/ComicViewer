/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.readlater

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.framework.ui.adaptive.AdaptiveNavigationSuiteScaffoldState
import com.sorrowblue.comicviewer.framework.ui.adaptive.rememberAdaptiveNavigationSuiteScaffoldState
import dev.zacsweers.metrox.viewmodel.metroViewModel
import kotlinx.coroutines.launch

internal interface ReadLaterScreenState {
    val scaffoldState: AdaptiveNavigationSuiteScaffoldState
    val lazyPagingItems: LazyPagingItems<File>
    val lazyGridState: LazyGridState

    fun onClearAllClick()
}

@Composable
internal fun rememberReadLaterScreenState(
    viewModel: ReadLaterViewModel = metroViewModel(),
): ReadLaterScreenState {
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
    return remember {
        ReadLaterScreenStateImpl(
            lazyGridState = lazyGridState,
            scaffoldState = scaffoldState,
            clearAll = viewModel::clearAll,
        )
    }.apply {
        lazyPagingItems = viewModel.pagingDataFlow.collectAsLazyPagingItems()
    }
}

private class ReadLaterScreenStateImpl(
    override val lazyGridState: LazyGridState,
    override val scaffoldState: AdaptiveNavigationSuiteScaffoldState,
    private val clearAll: () -> Unit,
) : ReadLaterScreenState {

    override lateinit var lazyPagingItems: LazyPagingItems<File>

    override fun onClearAllClick() {
        clearAll()
    }
}
