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
    val lazyGridState: LazyGridState = rememberLazyGridState()
    val scope = rememberCoroutineScope()
    val state = remember(id, scope) {
        CollectionScreenStateImpl(
            id = id,
            scope = scope,
            getCollectionUseCase = context.getCollectionUseCase,
        )
    }.apply {
        this.lazyPagingItems = rememberPagingItems {
            context.pagingCollectionFileUseCase(
                PagingCollectionFileUseCase.Request(PagingConfig(20), id),
            )
        }
        this.scaffoldState = rememberAdaptiveNavigationSuiteScaffoldState()
        this.lazyGridState = lazyGridState
    }
    return state
}

private class CollectionScreenStateImpl(
    id: CollectionId,
    scope: CoroutineScope,
    getCollectionUseCase: GetCollectionUseCase,
) : CollectionScreenState {
    override lateinit var scaffoldState: AdaptiveNavigationSuiteScaffoldState
    override lateinit var lazyGridState: LazyGridState
    override lateinit var lazyPagingItems: LazyPagingItems<File>
    override var uiState by mutableStateOf(CollectionScreenUiState())

    override lateinit var collection: Collection

    init {
        getCollectionUseCase(GetCollectionUseCase.Request(id))
            .mapNotNull { it.dataOrNull() }
            .onEach {
                collection = it
                uiState = uiState.copy(appBarUiState = uiState.appBarUiState.copy(title = it.name))
            }.launchIn(scope)
    }
}
