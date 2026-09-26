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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal interface CollectionListScreenState {
    val lazyListState: LazyListState

    fun onNavigationReSelect()
}

@Composable
internal fun rememberCollectionListScreenState(): CollectionListScreenState {
    val coroutineScope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()
    return remember {
        CollectionListScreenStateImpl(
            coroutineScope = coroutineScope,
            lazyListState = lazyListState,
        )
    }
}

@Stable
private class CollectionListScreenStateImpl(
    private val coroutineScope: CoroutineScope,
    override val lazyListState: LazyListState,
) : CollectionListScreenState {

    override fun onNavigationReSelect() {
        if (lazyListState.canScrollBackward) {
            coroutineScope.launch {
                lazyListState.scrollToItem(0)
            }
        }
    }
}
