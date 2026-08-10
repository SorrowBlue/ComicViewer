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
import com.sorrowblue.comicviewer.framework.ui.adaptive.AdaptiveNavigationSuiteScaffoldState
import com.sorrowblue.comicviewer.framework.ui.adaptive.rememberAdaptiveNavigationSuiteScaffoldState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal interface CollectionListScreenState {
    val scaffoldState: AdaptiveNavigationSuiteScaffoldState
    val lazyListState: LazyListState

    fun onNavClick()
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
    }.apply {
        scaffoldState =
            rememberAdaptiveNavigationSuiteScaffoldState(onNavigationReSelect = ::onNavClick)
    }
}

@Stable
private class CollectionListScreenStateImpl(
    private val coroutineScope: CoroutineScope,
    override val lazyListState: LazyListState,
) : CollectionListScreenState {
    override lateinit var scaffoldState: AdaptiveNavigationSuiteScaffoldState

    override fun onNavClick() {
        if (lazyListState.canScrollBackward) {
            coroutineScope.launch {
                lazyListState.scrollToItem(0)
            }
        }
    }
}
