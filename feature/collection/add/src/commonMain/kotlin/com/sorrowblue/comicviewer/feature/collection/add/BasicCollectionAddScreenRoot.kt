/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.collection.add

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.paging.compose.collectAsLazyPagingItems
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.PathString
import com.sorrowblue.comicviewer.framework.ui.EventEffect
import dev.zacsweers.metrox.viewmodel.assistedMetroViewModel

@Composable
internal fun BasicCollectionAddScreenRoot(
    bookshelfId: BookshelfId,
    path: String,
    onBackClick: () -> Unit,
    onCollectionCreateClick: (BookshelfId, PathString) -> Unit,
) {
    val viewModel =
        assistedMetroViewModel<BasicCollectionAddViewModel, BasicCollectionAddViewModel.Factory> {
            create(bookshelfId, path)
        }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lazyPagingItems = viewModel.pagingDataFlow.collectAsLazyPagingItems()
    val lazyListState = rememberLazyListState()
    BasicCollectionAddScreen(
        uiState = uiState,
        lazyPagingItems = lazyPagingItems,
        lazyListState = lazyListState,
        onDismissRequest = onBackClick,
        onClick = { collection, exist ->
            if (exist) {
                viewModel.removeCollection(collection.id)
            } else {
                viewModel.addCollection(collection.id)
            }
        },
        onClickCollectionSort = viewModel::updateCollectionSort,
        onCollectionCreateClick = dropUnlessResumed {
            onCollectionCreateClick(bookshelfId, path)
        },
        modifier = Modifier.testTag("BasicCollectionAddScreenRoot"),
    )
    EventEffect(viewModel.events) { event ->
        when (event) {
            BasicCollectionAddEvent.CollectionSortChanged -> lazyPagingItems.refresh()
        }
    }
}
