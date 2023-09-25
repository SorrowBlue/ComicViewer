package com.sorrowblue.comicviewer.feature.favorite.add

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.sorrowblue.comicviewer.domain.entity.favorite.Favorite
import com.sorrowblue.comicviewer.domain.entity.favorite.FavoriteId
import com.sorrowblue.comicviewer.feature.favorite.common.component.FavoriteItem
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FavoriteAddScreen(
    lazyPagingItems: LazyPagingItems<Favorite>,
    onBackClick: () -> Unit,
    onFavoriteClick: (FavoriteId) -> Unit,
    onAddClick: () -> Unit,
) {
    val appBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.favorite_add_title)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(ComicIcons.ArrowBack, "Back")
                    }
                },
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
