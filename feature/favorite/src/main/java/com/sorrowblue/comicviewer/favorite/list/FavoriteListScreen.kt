package com.sorrowblue.comicviewer.favorite.list

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.sorrowblue.comicviewer.domain.entity.favorite.Favorite
import com.sorrowblue.comicviewer.domain.entity.favorite.FavoriteId
import com.sorrowblue.comicviewer.favorite.section.FavoriteListAppBar
import com.sorrowblue.comicviewer.feature.favorite.R
import com.sorrowblue.comicviewer.feature.favorite.common.component.FavoriteItem
import com.sorrowblue.comicviewer.feature.favorite.common.section.FavoriteCreateDialog
import com.sorrowblue.comicviewer.feature.favorite.common.section.FavoriteCreateDialogUiState
import com.sorrowblue.comicviewer.framework.compose.AppMaterialTheme
import com.sorrowblue.comicviewer.framework.compose.FabVisibleState
import com.sorrowblue.comicviewer.framework.compose.LocalLifecycleState
import com.sorrowblue.comicviewer.framework.compose.isEmptyData
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import com.sorrowblue.comicviewer.framework.resource.R as FrameworkResourceR

@Composable
internal fun FavoriteListRoute(
    onSettingsClick: () -> Unit,
    onFavoriteClick: (FavoriteId) -> Unit,
    fabState: FabVisibleState,
    viewModel: FavoriteListViewModel = hiltViewModel()
) {
    val lazyPagingItems = viewModel.pagingDataFlow.collectAsLazyPagingItems()
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    LocalLifecycleState(
        onStart = {
            scope.launch {
                fabState.show(Icons.TwoTone.Add) {
                    viewModel.showCreateDialog()
                }
            }
        },
        onStop = {
            fabState.hide()
        }
    )
    FavoriteListScreen(
        uiState = uiState,
        lazyPagingItems = lazyPagingItems,
        onSettingsClick = onSettingsClick,
        onDismissRequest = { viewModel.closeCreateDialog() },
        onCreateFavoriteClick = viewModel::create,
        onValueChange = viewModel::onChangeText,
        onFavoriteClick = onFavoriteClick
    )
}

internal data class FavoriteListScreenUiState(
    val favoriteCreateDialogUiState: FavoriteCreateDialogUiState
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FavoriteListScreen(
    uiState: FavoriteListScreenUiState = FavoriteListScreenUiState(FavoriteCreateDialogUiState.Hide),
    lazyPagingItems: LazyPagingItems<Favorite>,
    onDismissRequest: () -> Unit = {},
    onCreateFavoriteClick: () -> Unit = {},
    onValueChange: (String) -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onFavoriteClick: (FavoriteId) -> Unit = {}
) {
    val appBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = {
            FavoriteListAppBar(
                topAppBarScrollBehavior = appBarScrollBehavior,
                onSettingsClick = onSettingsClick
            )
        },
        modifier = Modifier.nestedScroll(appBarScrollBehavior.nestedScrollConnection)
    ) { contentPadding ->
        if (lazyPagingItems.isEmptyData) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(FrameworkResourceR.drawable.ic_undraw_no_data_re_kwbl),
                    contentDescription = null,
                    modifier = Modifier.size(200.dp)
                )
                Text(
                    text = stringResource(id = R.string.favorite_list_label_no_favorites),
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        } else {
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
    FavoriteCreateDialog(
        uiState = uiState.favoriteCreateDialogUiState,
        onNameChange = onValueChange,
        onDismissRequest = onDismissRequest,
        onCreateClick = onCreateFavoriteClick
    )
}

@Preview
@Composable
fun PreviewFavoriteListScreen() {
    AppMaterialTheme {
        val items = List(20) {
            Favorite(FavoriteId(it), "Favorite name $it", it * 100)
        }
        FavoriteListScreen(lazyPagingItems = flowOf(PagingData.from(items)).collectAsLazyPagingItems())
    }
}
