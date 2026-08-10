/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.collection.list

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.paging.compose.collectAsLazyPagingItems
import com.sorrowblue.comicviewer.domain.model.collection.Collection
import dev.zacsweers.metrox.viewmodel.metroViewModel

@Composable
internal fun CollectionListScreenRoot(
    onItemClick: (Collection) -> Unit,
    onEditClick: (Collection) -> Unit,
    onDeleteClick: (Collection) -> Unit,
    onSettingsClick: () -> Unit,
    onCreateBasicCollectionClick: () -> Unit,
    onCreateSmartCollectionClick: () -> Unit,
    viewModel: CollectionListViewModel = metroViewModel(),
) {
    val state = rememberCollectionListScreenState()
    val lazyPagingItems = viewModel.pagingDataFlow.collectAsLazyPagingItems()
    state.scaffoldState.CollectionListScreen(
        lazyPagingItems = lazyPagingItems,
        lazyListState = state.lazyListState,
        onItemClick = onItemClick,
        onEditClick = onEditClick,
        onDeleteClick = onDeleteClick,
        onSettingsClick = onSettingsClick,
        onCreateBasicCollectionClick = onCreateBasicCollectionClick,
        onCreateSmartCollectionClick = onCreateSmartCollectionClick,
        modifier = Modifier.testTag("CollectionListScreenRoot"),
    )
}
