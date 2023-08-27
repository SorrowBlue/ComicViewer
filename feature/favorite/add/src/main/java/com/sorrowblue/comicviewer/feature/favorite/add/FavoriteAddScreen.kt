package com.sorrowblue.comicviewer.feature.favorite.add

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Add
import androidx.compose.material.icons.twotone.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.sorrowblue.comicviewer.feature.favorite.common.section.FavoriteCreateDialog
import com.sorrowblue.comicviewer.feature.favorite.common.section.FavoriteCreateDialogUiState

@Composable
internal fun FavoriteAddRoute(
    onBackClick: () -> Unit,
    viewModel: FavoriteAddViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val lazyPagingItems = viewModel.pagingDataFlow.collectAsLazyPagingItems()
    FavoriteAddScreen(
        uiState = uiState,
        lazyPagingItems = lazyPagingItems,
        onBackClick = onBackClick,
        onFavoriteClick = viewModel::add,
        onAddClick = viewModel::showFavoriteCreateDialog,
        onFavoriteNameChange = viewModel::onFavoriteNameChange,
        onFavoriteCreateDialogDismissRequest = viewModel::dismissFavoriteCreateDialog,
        onCreateFavoriteClick = viewModel::createFavorite
    )
}

internal data class FavoriteAddScreenUiState(
    val favoriteCreateDialogUiState: FavoriteCreateDialogUiState
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FavoriteAddScreen(
    uiState: FavoriteAddScreenUiState,
    lazyPagingItems: LazyPagingItems<Favorite>,
    onBackClick: () -> Unit,
    onFavoriteClick: (FavoriteId) -> Unit,
    onAddClick: () -> Unit,
    onFavoriteNameChange: (String) -> Unit,
    onFavoriteCreateDialogDismissRequest: () -> Unit,
    onCreateFavoriteClick: () -> Unit,
) {
    val appBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.favorite_add_title)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.TwoTone.ArrowBack, "Back")
                    }
                },
                scrollBehavior = appBarScrollBehavior
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(Icons.TwoTone.Add, null)
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

    FavoriteCreateDialog(
        uiState = uiState.favoriteCreateDialogUiState,
        onNameChange = onFavoriteNameChange,
        onDismissRequest = onFavoriteCreateDialogDismissRequest,
        onCreateClick = onCreateFavoriteClick
    )
}
