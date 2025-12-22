package com.sorrowblue.comicviewer.feature.collection.list

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.sorrowblue.comicviewer.domain.model.collection.Collection

@Composable
context(context: CollectionListScreenContext)
fun CollectionListScreenRoot(
    onItemClick: (Collection) -> Unit,
    onEditClick: (Collection) -> Unit,
    onDeleteClick: (Collection) -> Unit,
    onSettingsClick: () -> Unit,
    onCreateBasicCollectionClick: () -> Unit,
    onCreateSmartCollectionClick: () -> Unit,
) {
    val state = rememberCollectionListScreenState()
    state.scaffoldState.CollectionListScreen(
        lazyPagingItems = state.lazyPagingItems,
        lazyListState = state.lazyListState,
        onItemClick = onItemClick,
        onEditClick = onEditClick,
        onDeleteClick = onDeleteClick,
        onSettingsClick = onSettingsClick,
        onCreateBasicCollectionClick = onCreateBasicCollectionClick,
        onCreateSmartCollectionClick = onCreateSmartCollectionClick,
        modifier = Modifier.testTag("CollectionListScreenRoot")
    )
}
