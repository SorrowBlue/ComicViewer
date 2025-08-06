package com.sorrowblue.comicviewer.feature.collection.list

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.collection.Collection
import com.sorrowblue.comicviewer.framework.ui.CanonicalScaffoldState
import com.sorrowblue.comicviewer.framework.ui.rememberCanonicalScaffoldState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

internal interface CollectionListScreenState {
    val scaffoldState: CanonicalScaffoldState<Unit>
    val pagingDataFlow: Flow<PagingData<Collection>>
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
    CollectionListScreenStateImpl(
        scaffoldState = scaffoldState,
        lazyListState = lazyListState,
        pagingDataFlow = viewModel.pagingDataFlow,
        scope = scope,
    )
}

@Stable
private class CollectionListScreenStateImpl(
    override val scaffoldState: CanonicalScaffoldState<Unit>,
    override val lazyListState: LazyListState,
    override val pagingDataFlow: Flow<PagingData<Collection>>,
    private val scope: CoroutineScope,
) : CollectionListScreenState {

    override fun onNavClick() {
        if (lazyListState.canScrollBackward) {
            scope.launch {
                lazyListState.scrollToItem(0)
            }
        }
    }
}
