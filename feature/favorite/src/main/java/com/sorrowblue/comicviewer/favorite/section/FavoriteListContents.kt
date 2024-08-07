package com.sorrowblue.comicviewer.favorite.section

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.sorrowblue.comicviewer.domain.model.favorite.Favorite
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteId
import com.sorrowblue.comicviewer.feature.favorite.R
import com.sorrowblue.comicviewer.feature.favorite.common.component.FavoriteItem
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.undraw.UndrawNoData
import com.sorrowblue.comicviewer.framework.ui.EmptyContent
import com.sorrowblue.comicviewer.framework.ui.material3.drawVerticalScrollbar
import com.sorrowblue.comicviewer.framework.ui.paging.isEmptyData

internal sealed interface FavoriteListContentsAction {
    data class FavoriteClick(val favoriteId: FavoriteId) : FavoriteListContentsAction
    data class EditClick(val favoriteId: FavoriteId) : FavoriteListContentsAction
}

@Composable
internal fun FavoriteListContents(
    lazyPagingItems: LazyPagingItems<Favorite>,
    onAction: (FavoriteListContentsAction) -> Unit,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(),
) {
    if (lazyPagingItems.isEmptyData) {
        EmptyContent(
            imageVector = ComicIcons.UndrawNoData,
            text = stringResource(id = R.string.favorite_list_label_no_favorites),
            modifier = modifier
        )
    } else {
        LazyColumn(
            contentPadding = contentPadding,
            state = lazyListState,
            modifier = modifier.drawVerticalScrollbar(lazyListState)
        ) {
            items(
                count = lazyPagingItems.itemCount,
                key = lazyPagingItems.itemKey { it.id.value }
            ) { index ->
                lazyPagingItems[index]?.let {
                    FavoriteItem(
                        favorite = it,
                        onClick = { onAction(FavoriteListContentsAction.FavoriteClick(it.id)) },
                        trailingContent = {
                            IconButton(onClick = { onAction(FavoriteListContentsAction.EditClick(it.id)) }) {
                                Icon(imageVector = ComicIcons.Edit, contentDescription = null)
                            }
                        }
                    )
                }
            }
        }
    }
}
