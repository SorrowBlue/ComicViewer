/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.bookshelf

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.sorrowblue.comicviewer.framework.ui.adaptive.AdaptiveNavigationSuiteScaffoldState
import com.sorrowblue.comicviewer.framework.ui.adaptive.rememberAdaptiveNavigationSuiteScaffoldState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
internal fun rememberBookshelfScreenState(): BookshelfScreenState {
    val coroutineScope = rememberCoroutineScope()
    val lazyGridState = rememberLazyGridState()
    val state = remember {
        BookshelfScreenStateImpl(
            coroutineScope = coroutineScope,
            lazyGridState = lazyGridState,
        )
    }.apply {
        scaffoldState =
            rememberAdaptiveNavigationSuiteScaffoldState(
                onNavigationReSelect = ::onNavigationReSelect,
            )
    }
    return state
}

internal interface BookshelfScreenState {
    val scaffoldState: AdaptiveNavigationSuiteScaffoldState
    val lazyGridState: LazyGridState
}

private class BookshelfScreenStateImpl(
    private val coroutineScope: CoroutineScope,
    override val lazyGridState: LazyGridState,
) : BookshelfScreenState {

    override lateinit var scaffoldState: AdaptiveNavigationSuiteScaffoldState

    fun onNavigationReSelect() {
        if (lazyGridState.canScrollBackward) {
            coroutineScope.launch {
                lazyGridState.scrollToItem(0)
            }
        }
    }
}
