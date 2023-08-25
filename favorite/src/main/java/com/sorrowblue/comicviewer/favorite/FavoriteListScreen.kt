package com.sorrowblue.comicviewer.favorite

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import com.sorrowblue.comicviewer.domain.entity.favorite.Favorite
import com.sorrowblue.comicviewer.domain.entity.favorite.FavoriteId
import com.sorrowblue.comicviewer.favorite.list.FavoriteListViewModel
import com.sorrowblue.comicviewer.favorite.section.FavoriteListAppBar
import com.sorrowblue.comicviewer.framework.compose.AppMaterialTheme
import com.sorrowblue.comicviewer.framework.compose.FabVisibleState
import com.sorrowblue.comicviewer.framework.compose.LocalLifecycleState
import com.sorrowblue.comicviewer.framework.compose.isEmptyData
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

@Composable
internal fun FavoriteListRoute(
    onSettingsClick: () -> Unit,
    fabState: FabVisibleState,
    viewModel: FavoriteListViewModel = hiltViewModel()
) {
    val lazyPagingItems = viewModel.pagingDataFlow.collectAsLazyPagingItems()
    val favoriteName by viewModel.text.collectAsState()
    val isShowCreateDialog by viewModel.isShowCreateDialog.collectAsState()
    val scope = rememberCoroutineScope()
    LocalLifecycleState(
        onStart = {
            scope.launch {
                fabState.show(Icons.TwoTone.Add) {
                    viewModel.onChangeDialog(true)
                }
            }
        },
        onStop = {
            fabState.hide()
        }
    )
    FavoriteListScreen(
        lazyPagingItems = lazyPagingItems,
        onSettingsClick = onSettingsClick,
        onDismissRequest = {viewModel.onChangeDialog(false)},
        favoriteName = favoriteName,
        onCreateFavoriteClick = viewModel::create,
        isShowCreateDialog = isShowCreateDialog,
        onValueChange = viewModel::onChangeText
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FavoriteListScreen(
    lazyPagingItems: LazyPagingItems<Favorite>,
    isShowCreateDialog: Boolean = false,
    favoriteName: String ="",
    onDismissRequest: () -> Unit= {},
    onCreateFavoriteClick: () -> Unit= {},
    onValueChange: (String) -> Unit= {},
    onSettingsClick: () -> Unit = {},
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
        } else {
            LazyColumn(contentPadding = contentPadding) {
                items(lazyPagingItems.itemCount, key = lazyPagingItems.itemKey { it.id.value }) {
                    val item = lazyPagingItems[it]
                    if (item != null) {
                        ListItem(
                            modifier = Modifier.clickable { },
                            headlineContent = { Text(item.name) },
                            supportingContent = {
                                Text(
                                    pluralStringResource(
                                        id = R.plurals.favorite_list_label_file_count,
                                        count = item.count
                                    )
                                )
                            },
                            leadingContent = {
                                AsyncImage(model = item, null, Modifier.size(56.dp))
                            }
                        )
                    }
                }
            }
        }
    }
    if (isShowCreateDialog) {
        AlertDialog(
            title = { Text("新しいお気に入り") },
            text = { OutlinedTextField(value = favoriteName, onValueChange = onValueChange) },
            onDismissRequest = onDismissRequest,
            confirmButton = {
                TextButton(onClick = { onCreateFavoriteClick() }) {
                    Text("作成")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissRequest) {
                    Text("キャンセル")
                }
            }
        )
    }
}

@Preview
@Composable
fun PreviewFavoriteListScreen() {
    AppMaterialTheme {
        val items = List(20) {
            Favorite(FavoriteId(it), "Favorite name $it", it * 100)
        }
        FavoriteListScreen(flowOf(PagingData.from(items)).collectAsLazyPagingItems())
    }
}
