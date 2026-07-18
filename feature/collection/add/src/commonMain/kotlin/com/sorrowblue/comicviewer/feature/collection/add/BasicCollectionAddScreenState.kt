/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.collection.add

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.collection.Collection
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.domain.model.settings.CollectionSettings
import com.sorrowblue.comicviewer.feature.collection.add.component.CollectionSort
import dev.zacsweers.metrox.viewmodel.assistedMetroViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

internal interface BasicCollectionAddScreenState {
    val uiState: BasicCollectionAddScreenUiState
    val lazyPagingItems: LazyPagingItems<Pair<Collection, Boolean>>
    val lazyListState: LazyListState

    fun onCollectionClick(collection: Collection, exist: Boolean)

    fun onClickCollectionSort(sort: CollectionSort)
}

@Composable
internal fun rememberBasicCollectionAddScreenState(
    bookshelfId: BookshelfId,
    path: String,
    viewModel: BasicCollectionAddViewModel = assistedMetroViewModel<BasicCollectionAddViewModel, BasicCollectionAddViewModel.Factory> {
        create(bookshelfId, path)
    },
): BasicCollectionAddScreenState {
    val coroutineScope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()
    return remember(viewModel) {
        BasicCollectionAddScreenStateImpl(
            coroutineScope = coroutineScope,
            lazyListState = lazyListState,
            collectionSettingsFlow = viewModel.collectionSettingsFlow,
            updateCollectionSettings = viewModel::updateCollectionSettings,
            addCollection = viewModel::addCollection,
            removeCollection = viewModel::removeCollection
        )
    }.apply {
        lazyPagingItems = viewModel.pagingDataFlow.collectAsLazyPagingItems()
    }
}

private class BasicCollectionAddScreenStateImpl(
    coroutineScope: CoroutineScope,
    override val lazyListState: LazyListState,
    collectionSettingsFlow: SharedFlow<CollectionSettings>,
    private val updateCollectionSettings: ((CollectionSettings) -> CollectionSettings) -> Unit,
    private val addCollection: (CollectionId) -> Unit,
    private val removeCollection: (CollectionId) -> Unit,
) : BasicCollectionAddScreenState {

    override var uiState by mutableStateOf(BasicCollectionAddScreenUiState())
        private set
    override lateinit var lazyPagingItems: LazyPagingItems<Pair<Collection, Boolean>>

    init {
        collectionSettingsFlow
            .map { it.recent }
            .distinctUntilChanged()
            .onEach {
                uiState =
                    uiState.copy(
                        collectionSort = if (it) CollectionSort.Recent else CollectionSort.Created,
                    )
            }.launchIn(coroutineScope)
    }

    override fun onClickCollectionSort(sort: CollectionSort) {
        updateCollectionSettings {
            it.copy(recent = sort == CollectionSort.Recent)
        }
        lazyPagingItems.refresh()
    }

    override fun onCollectionClick(collection: Collection, exist: Boolean) {
        if (exist) {
            removeCollection(collection.id)
        } else {
            addCollection(collection.id)
        }
    }
}
