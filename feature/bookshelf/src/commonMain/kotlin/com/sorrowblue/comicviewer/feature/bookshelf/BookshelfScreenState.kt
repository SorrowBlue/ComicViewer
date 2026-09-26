/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.bookshelf

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
internal fun rememberBookshelfScreenState(): BookshelfScreenState {
    val coroutineScope = rememberCoroutineScope()
    val lazyGridState = rememberLazyGridState()
    return remember {
        BookshelfScreenStateImpl(
            coroutineScope = coroutineScope,
            lazyGridState = lazyGridState,
        )
    }
}

internal interface BookshelfScreenState {
    val lazyGridState: LazyGridState
    fun onNavigationReSelect()
}

private class BookshelfScreenStateImpl(
    private val coroutineScope: CoroutineScope,
    override val lazyGridState: LazyGridState,
) : BookshelfScreenState {

    override fun onNavigationReSelect() {
        if (lazyGridState.canScrollBackward) {
            coroutineScope.launch {
                lazyGridState.scrollToItem(0)
            }
        }
    }
}
