package com.sorrowblue.comicviewer.feature.collection

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.paging.PagingConfig
import androidx.paging.compose.LazyPagingItems
import com.sorrowblue.comicviewer.domain.model.collection.Collection
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.domain.model.dataOrNull
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.usecase.collection.GetCollectionUseCase
import com.sorrowblue.comicviewer.domain.usecase.collection.PagingCollectionFileUseCase
import com.sorrowblue.comicviewer.framework.ui.adaptive.AdaptiveNavigationSuiteScaffoldState
import com.sorrowblue.comicviewer.framework.ui.adaptive.rememberAdaptiveNavigationSuiteScaffoldState
import com.sorrowblue.comicviewer.framework.ui.paging.rememberPagingItems
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach

internal interface CollectionScreenState {
    val uiState: CollectionScreenUiState
    val collection: Collection
    val scaffoldState: AdaptiveNavigationSuiteScaffoldState
    val lazyPagingItems: LazyPagingItems<File>
    val lazyGridState: LazyGridState
}

@Composable
context(context: CollectionScreenContext)
internal fun rememberCollectionScreenState(id: CollectionId): CollectionScreenState {
    val lazyGridState = rememberLazyGridState()
    val coroutineScope = rememberCoroutineScope()
    val lazyPagingItems = rememberPagingItems {
        context.pagingCollectionFileUseCase(
            PagingCollectionFileUseCase.Request(PagingConfig(20), id),
        )
    }
    val scaffoldState = rememberAdaptiveNavigationSuiteScaffoldState()
    return remember(id) {
        CollectionScreenStateImpl(
            id = id,
            coroutineScope = coroutineScope,
            getCollectionUseCase = context.getCollectionUseCase,
            lazyGridState = lazyGridState,
            scaffoldState = scaffoldState,
            lazyPagingItems = lazyPagingItems,
        )
    }
}

private class CollectionScreenStateImpl(
    id: CollectionId,
    coroutineScope: CoroutineScope,
    getCollectionUseCase: GetCollectionUseCase,
    override val lazyGridState: LazyGridState,
    override val scaffoldState: AdaptiveNavigationSuiteScaffoldState,
    override val lazyPagingItems: LazyPagingItems<File>,
) : CollectionScreenState {
    override var uiState by mutableStateOf(CollectionScreenUiState())

    override lateinit var collection: Collection

    init {
        getCollectionUseCase(GetCollectionUseCase.Request(id))
            .mapNotNull { it.dataOrNull() }
            .onEach {
                collection = it
                uiState = uiState.copy(appBarUiState = uiState.appBarUiState.copy(title = it.name))
            }.launchIn(coroutineScope)
    }
}
