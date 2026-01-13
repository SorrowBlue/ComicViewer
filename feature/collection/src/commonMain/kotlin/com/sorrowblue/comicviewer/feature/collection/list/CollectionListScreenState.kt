package com.sorrowblue.comicviewer.feature.collection.list

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.paging.PagingConfig
import androidx.paging.compose.LazyPagingItems
import com.sorrowblue.comicviewer.domain.model.collection.Collection
import com.sorrowblue.comicviewer.domain.usecase.collection.PagingCollectionUseCase
import com.sorrowblue.comicviewer.framework.ui.adaptive.AdaptiveNavigationSuiteScaffoldState
import com.sorrowblue.comicviewer.framework.ui.adaptive.rememberAdaptiveNavigationSuiteScaffoldState
import com.sorrowblue.comicviewer.framework.ui.paging.rememberPagingItems
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal interface CollectionListScreenState {
    val scaffoldState: AdaptiveNavigationSuiteScaffoldState
    val lazyPagingItems: LazyPagingItems<Collection>
    val lazyListState: LazyListState

    fun onNavClick()
}

@Composable
context(context: CollectionListScreenContext)
internal fun rememberCollectionListScreenState(): CollectionListScreenState {
    val lazyListState = rememberLazyListState()
    val scaffoldState = rememberAdaptiveNavigationSuiteScaffoldState()
    val lazyPagingItems = rememberPagingItems {
        context.pagingCollectionUseCase(PagingCollectionUseCase.Request(PagingConfig(20)))
    }
    val coroutineScope = rememberCoroutineScope()
    return remember {
        CollectionListScreenStateImpl(
            lazyListState = lazyListState,
            scaffoldState = scaffoldState,
            lazyPagingItems = lazyPagingItems,
            coroutineScope = coroutineScope,
        )
    }
}

@Stable
private class CollectionListScreenStateImpl(
    override val lazyListState: LazyListState,
    override val scaffoldState: AdaptiveNavigationSuiteScaffoldState,
    override val lazyPagingItems: LazyPagingItems<Collection>,
    val coroutineScope: CoroutineScope,
) : CollectionListScreenState {
    override fun onNavClick() {
        if (lazyListState.canScrollBackward) {
            coroutineScope.launch {
                lazyListState.scrollToItem(0)
            }
        }
    }
}
