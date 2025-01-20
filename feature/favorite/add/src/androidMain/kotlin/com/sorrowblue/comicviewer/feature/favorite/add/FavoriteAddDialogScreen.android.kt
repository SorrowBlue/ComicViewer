package com.sorrowblue.comicviewer.feature.favorite.add

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.framework.ui.paging.collectAsLazyPagingItems
import com.sorrowblue.comicviewer.framework.ui.preview.fake.fakeFavorite
import com.sorrowblue.comicviewer.framework.ui.preview.fake.flowData

@Composable
@Preview
private fun FavoriteAddDialogScreenPreview() {
    val lazyPagingItems = PagingData.flowData { fakeFavorite(it).copy(exist = it % 3 == 0) }
        .collectAsLazyPagingItems()
    FavoriteAddDialogScreen(
        lazyPagingItems = lazyPagingItems,
        recentFavorites = lazyPagingItems,
        onDismissRequest = {},
        onClick = {},
        onNewFavoriteClick = {}
    )
}
