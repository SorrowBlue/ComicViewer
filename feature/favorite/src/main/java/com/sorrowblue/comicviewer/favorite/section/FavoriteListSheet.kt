package com.sorrowblue.comicviewer.favorite.section

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.sorrowblue.comicviewer.domain.model.favorite.Favorite
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteId
import com.sorrowblue.comicviewer.feature.favorite.common.component.FavoriteItem
import com.sorrowblue.comicviewer.framework.ui.material3.drawVerticalScrollbar

@Composable
fun FavoriteListSheet(
    lazyPagingItems: LazyPagingItems<Favorite>,
    contentPadding: PaddingValues,
    lazyListState: LazyListState,
    onFavoriteClick: (FavoriteId) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        contentPadding = contentPadding,
        state = lazyListState,
        modifier = modifier.drawVerticalScrollbar(lazyListState)
    ) {
        items(lazyPagingItems.itemCount, key = lazyPagingItems.itemKey { it.id.value }) {
            val item = lazyPagingItems[it]
            if (item != null) {
                FavoriteItem(
                    favorite = item,
                    onClick = { onFavoriteClick(item.id) },
                )
            }
        }
    }
}
