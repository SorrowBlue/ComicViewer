package com.sorrowblue.comicviewer.feature.collection.add.section

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.sorrowblue.comicviewer.domain.model.collection.Collection
import com.sorrowblue.comicviewer.feature.collection.add.component.CollectionListItem

@Composable
internal fun BasicCollectionContent(
    lazyPagingItems: LazyPagingItems<Pair<Collection, Boolean>>,
    onClick: (Collection, Boolean) -> Unit,
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(),
) {
    LazyColumn(
        state = state,
        contentPadding = contentPadding,
        modifier = modifier
    ) {
        items(
            count = lazyPagingItems.itemCount,
            key = lazyPagingItems.itemKey { it.first.id.value }
        ) { index ->
            lazyPagingItems[index]?.let { item ->
                CollectionListItem(
                    collection = item.first,
                    exist = item.second,
                    onClick = { onClick(item.first, item.second) }
                )
            }
        }
    }
}
