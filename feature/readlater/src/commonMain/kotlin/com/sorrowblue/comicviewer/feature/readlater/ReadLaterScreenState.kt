/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.readlater

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
import com.sorrowblue.comicviewer.domain.model.file.File
import dev.zacsweers.metrox.viewmodel.metroViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal interface ReadLaterScreenState {
    val lazyPagingItems: LazyPagingItems<File>
    val lazyGridState: LazyGridState

    fun onNavigationReSelect()
    fun onClearAllClick()
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun rememberReadLaterScreenState(
    viewModel: ReadLaterViewModel = metroViewModel(),
): ReadLaterScreenState {
    val cacheWindow = LazyLayoutCacheWindow(ahead = 150.dp, behind = 100.dp)
    val lazyGridState = rememberLazyGridState(cacheWindow)
    val coroutineScope = rememberCoroutineScope()
    return remember {
        ReadLaterScreenStateImpl(
            lazyGridState = lazyGridState,
            clearAll = viewModel::clearAll,
            coroutineScope = coroutineScope,
        )
    }.apply {
        lazyPagingItems = viewModel.pagingDataFlow.collectAsLazyPagingItems()
    }
}

private class ReadLaterScreenStateImpl(
    override val lazyGridState: LazyGridState,
    private val coroutineScope: CoroutineScope,
    private val clearAll: () -> Unit,
) : ReadLaterScreenState {

    override lateinit var lazyPagingItems: LazyPagingItems<File>

    override fun onNavigationReSelect() {
        if (lazyGridState.canScrollBackward) {
            coroutineScope.launch {
                lazyGridState.animateScrollToItem(0)
            }
        }
    }

    override fun onClearAllClick() {
        clearAll()
    }
}
