package com.sorrowblue.comicviewer.feature.favorite.add

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.sorrowblue.comicviewer.domain.model.favorite.Favorite
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteId
import com.sorrowblue.comicviewer.feature.favorite.common.component.FavoriteItem
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.material3.TopAppBar
import com.sorrowblue.comicviewer.framework.ui.material3.TopAppBarDefaults
import com.sorrowblue.comicviewer.framework.ui.material3.pinnedScrollBehavior

@Composable
internal fun FavoriteAddRoute(
    onBackClick: () -> Unit,
    onAddClick: () -> Unit,
    viewModel: FavoriteAddViewModel = hiltViewModel(),
) {
    val lazyPagingItems = viewModel.pagingDataFlow.collectAsLazyPagingItems()
    FavoriteAddScreen(
        lazyPagingItems = lazyPagingItems,
        onBackClick = onBackClick,
        onFavoriteClick = viewModel::add,
        onAddClick = onAddClick,
    )
}

@Composable
private fun FavoriteAddScreen(
    lazyPagingItems: LazyPagingItems<Favorite>,
    onBackClick: () -> Unit,
    onFavoriteClick: (FavoriteId) -> Unit,
    onAddClick: () -> Unit,
) {
    val appBarScrollBehavior =
        TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = {
            TopAppBar(
                title = R.string.favorite_add_title,
                onBackClick = onBackClick,
                scrollBehavior = appBarScrollBehavior
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(ComicIcons.Add, null)
            }
        },
        modifier = Modifier.nestedScroll(appBarScrollBehavior.nestedScrollConnection)
    ) { contentPadding ->
        LazyColumn(contentPadding = contentPadding) {
            items(lazyPagingItems.itemCount, key = lazyPagingItems.itemKey { it.id.value }) {
                val item = lazyPagingItems[it]
                if (item != null) {
                    FavoriteItem(favorite = item, onClick = { onFavoriteClick(item.id) })
                }
            }
        }
    }
}
