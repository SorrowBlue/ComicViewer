package com.sorrowblue.comicviewer.favorite.list

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.framework.ui.paging.collectAsLazyPagingItems
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewMultiScreen
import com.sorrowblue.comicviewer.framework.ui.preview.fake.fakeFavorite
import com.sorrowblue.comicviewer.framework.ui.preview.fake.flowData
import com.sorrowblue.comicviewer.framework.ui.preview.layout.PreviewCompliantNavigation

@PreviewMultiScreen
@Composable
private fun FavoriteListScreenPreview() {
    val lazyPagingItems = PagingData.flowData { fakeFavorite(favoriteId = it) }.collectAsLazyPagingItems()
    PreviewCompliantNavigation {
        FavoriteListScreen(
            lazyPagingItems = lazyPagingItems,
            lazyListState = rememberLazyListState(),
            onSettingsClick = {},
            onCreateFavoriteClick = {},
            onContentsAction = {}
        )
    }
}
