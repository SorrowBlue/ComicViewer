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
internal fun rememberCollectionListScreenState(
    scope: CoroutineScope = rememberCoroutineScope(),
    lazyListState: LazyListState = rememberLazyListState(),
): CollectionListScreenState {
    val lazyPagingItems = rememberPagingItems {
        context.pagingCollectionUseCase(PagingCollectionUseCase.Request(PagingConfig(20)))
    }
    return remember {
        CollectionListScreenStateImpl()
    }.apply {
        this.scaffoldState = rememberAdaptiveNavigationSuiteScaffoldState()
        this.lazyListState = lazyListState
        this.scope = scope
        this.lazyPagingItems = lazyPagingItems
    }
}

@Stable
private class CollectionListScreenStateImpl : CollectionListScreenState {
    override lateinit var scaffoldState: AdaptiveNavigationSuiteScaffoldState
    override lateinit var lazyListState: LazyListState
    lateinit var scope: CoroutineScope

    override lateinit var lazyPagingItems: LazyPagingItems<Collection>

    override fun onNavClick() {
        if (lazyListState.canScrollBackward) {
            scope.launch {
                lazyListState.scrollToItem(0)
            }
        }
    }
}
