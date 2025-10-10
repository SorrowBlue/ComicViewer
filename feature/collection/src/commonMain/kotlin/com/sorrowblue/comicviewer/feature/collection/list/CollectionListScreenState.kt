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
import com.sorrowblue.comicviewer.framework.ui.CanonicalScaffoldState
import com.sorrowblue.comicviewer.framework.ui.rememberCanonicalScaffoldState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

internal interface CollectionListScreenState {
    val scaffoldState: CanonicalScaffoldState<Unit>
    val lazyPagingItems: LazyPagingItems<Collection>
    val lazyListState: LazyListState
    fun onNavClick()
}

@Composable
internal fun rememberCollectionListScreenState(
    scaffoldState: CanonicalScaffoldState<Unit> = rememberCanonicalScaffoldState(),
    scope: CoroutineScope = rememberCoroutineScope(),
    lazyListState: LazyListState = rememberLazyListState(),
    viewModel: CollectionListViewModel = koinViewModel(),
): CollectionListScreenState = remember {
    CollectionListScreenStateImpl()
}.apply {
    this.scaffoldState = scaffoldState
    this.lazyListState = lazyListState
    this.scope = scope
    this.lazyPagingItems = viewModel.pagingDataFlow.collectAsLazyPagingItems()
}

@Stable
private class CollectionListScreenStateImpl : CollectionListScreenState {

    override lateinit var scaffoldState: CanonicalScaffoldState<Unit>
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
