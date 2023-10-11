package com.sorrowblue.comicviewer.favorite.section

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.sorrowblue.comicviewer.domain.model.favorite.Favorite
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteId
import com.sorrowblue.comicviewer.feature.favorite.common.component.FavoriteItem
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.designsystem.theme.LocalWindowSize
import com.sorrowblue.comicviewer.framework.designsystem.theme.largeBottom
import com.sorrowblue.comicviewer.framework.designsystem.theme.largeTop

@Composable
fun FavoriteListSheet(
    lazyPagingItems: LazyPagingItems<Favorite>,
    innerPadding: PaddingValues,
    lazyListState: LazyListState,
    onFavoriteClick: (FavoriteId) -> Unit,
) {
    LazyColumn(
        contentPadding = innerPadding,
        state = lazyListState
    ) {
        items(lazyPagingItems.itemCount, key = lazyPagingItems.itemKey { it.id.value }) {
            val item = lazyPagingItems[it]
            if (item != null) {
                FavoriteItem(
                    favorite = item,
                    onClick = { onFavoriteClick(item.id) },
                    modifier = Modifier.then(
                        if (LocalWindowSize.current.widthSizeClass == WindowWidthSizeClass.Compact) {
                            Modifier
                        } else {
                            when (it) {
                                0 -> Modifier.clip(ComicTheme.shapes.largeTop)
                                lazyPagingItems.itemCount - 1 -> Modifier.clip(ComicTheme.shapes.largeBottom)
                                else -> Modifier
                            }
                        }
                    )
                )
            }
        }
    }
}
