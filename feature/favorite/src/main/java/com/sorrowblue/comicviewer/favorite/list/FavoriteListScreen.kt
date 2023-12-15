package com.sorrowblue.comicviewer.favorite.list

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.sorrowblue.comicviewer.domain.model.favorite.Favorite
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteId
import com.sorrowblue.comicviewer.favorite.section.FavoriteListAppBar
import com.sorrowblue.comicviewer.favorite.section.FavoriteListEmptySheet
import com.sorrowblue.comicviewer.favorite.section.FavoriteListSheet
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.add
import com.sorrowblue.comicviewer.framework.ui.asWindowInsets
import com.sorrowblue.comicviewer.framework.ui.paging.isEmptyData

@Composable
internal fun NavBackStackEntry.FavoriteListRoute(
    contentPadding: PaddingValues,
    onSettingsClick: () -> Unit,
    onFavoriteClick: (FavoriteId) -> Unit,
    onCreateFavoriteClick: () -> Unit,
    viewModel: FavoriteListViewModel = hiltViewModel(),
) {
    val lazyPagingItems = viewModel.pagingDataFlow.collectAsLazyPagingItems()
    FavoriteListScreen(
        contentPadding = contentPadding,
        lazyPagingItems = lazyPagingItems,
        onSettingsClick = onSettingsClick,
        onFavoriteClick = onFavoriteClick,
        onCreateFavoriteClick = onCreateFavoriteClick,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FavoriteListScreen(
    lazyPagingItems: LazyPagingItems<Favorite>,
    onSettingsClick: () -> Unit,
    onFavoriteClick: (FavoriteId) -> Unit,
    onCreateFavoriteClick: () -> Unit,
    contentPadding: PaddingValues,
) {
    val appBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val lazyListState = rememberLazyListState()
    Scaffold(
        topBar = {
            FavoriteListAppBar(
                scrollBehavior = appBarScrollBehavior,
                onSettingsClick = onSettingsClick
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("Create Favorite") },
                icon = { Icon(imageVector = ComicIcons.Add, contentDescription = "") },
                onClick = onCreateFavoriteClick
            )
        },
        contentWindowInsets = contentPadding.asWindowInsets(),
        modifier = Modifier.nestedScroll(appBarScrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        if (lazyPagingItems.isEmptyData) {
            FavoriteListEmptySheet(contentPadding = innerPadding)
        } else {
            FavoriteListSheet(
                lazyPagingItems = lazyPagingItems,
                contentPadding = innerPadding.add(paddingValues = PaddingValues(bottom = 88.dp)),
                lazyListState = lazyListState,
                onFavoriteClick = onFavoriteClick
            )
        }
    }
}
