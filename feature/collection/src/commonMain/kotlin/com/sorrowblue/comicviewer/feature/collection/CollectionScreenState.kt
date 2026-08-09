/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.collection

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.sorrowblue.comicviewer.domain.model.collection.Collection
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.framework.ui.adaptive.AdaptiveNavigationSuiteScaffoldState
import com.sorrowblue.comicviewer.framework.ui.adaptive.rememberAdaptiveNavigationSuiteScaffoldState
import dev.zacsweers.metrox.viewmodel.assistedMetroViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

internal interface CollectionScreenState {
    val uiState: CollectionScreenUiState
    val collection: Collection
    val scaffoldState: AdaptiveNavigationSuiteScaffoldState
    val lazyPagingItems: LazyPagingItems<File>
}

@Composable
internal fun rememberCollectionScreenState(
    id: CollectionId,
    viewModel: CollectionViewModel =
        assistedMetroViewModel<CollectionViewModel, CollectionViewModel.Factory> {
            create(id)
        },
): CollectionScreenState {
    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberAdaptiveNavigationSuiteScaffoldState()
    return remember {
        CollectionScreenStateImpl(
            coroutineScope = coroutineScope,
            collectionFlow = viewModel.collectionFlow,
            scaffoldState = scaffoldState,
        )
    }.apply {
        lazyPagingItems = viewModel.pagingDataFlow.collectAsLazyPagingItems()
    }
}

private class CollectionScreenStateImpl(
    coroutineScope: CoroutineScope,
    collectionFlow: SharedFlow<Collection>,
    override val scaffoldState: AdaptiveNavigationSuiteScaffoldState,
) : CollectionScreenState {

    override var uiState by mutableStateOf(CollectionScreenUiState())

    override lateinit var lazyPagingItems: LazyPagingItems<File>

    override lateinit var collection: Collection

    init {
        collectionFlow.onEach {
            collection = it
            uiState = uiState.copy(appBarUiState = uiState.appBarUiState.copy(title = it.name))
        }.launchIn(coroutineScope)
    }
}
