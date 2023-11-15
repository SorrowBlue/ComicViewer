package com.sorrowblue.comicviewer.feature.favorite.edit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.BookFile
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.undraw.UndrawNoData
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.EmptyContent
import com.sorrowblue.comicviewer.framework.ui.paging.isEmptyData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@Composable
internal fun FavoriteEditRoute(
    onBackClick: () -> Unit,
    onComplete: () -> Unit,
    viewModel: FavoriteEditViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val lazyPagingItems = viewModel.pagingDataFlow.collectAsLazyPagingItems()
    FavoriteEditScreen(
        uiState = uiState,
        lazyPagingItems = lazyPagingItems,
        onBackClick = onBackClick,
        onSaveClick = { viewModel.save(onComplete) },
        onNameChange = viewModel::updateName,
        onDeleteClick = viewModel::removeFile
    )
}

data class FavoriteEditScreenUiState(
    val name: String = "",
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FavoriteEditScreen(
    uiState: FavoriteEditScreenUiState,
    lazyPagingItems: LazyPagingItems<File>,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
    onNameChange: (String) -> Unit,
    onDeleteClick: (File) -> Unit,
) {
    val appBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.wrapContentHeight(),
                title = {
                    Text(stringResource(R.string.favorite_edit_title))
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(ComicIcons.ArrowBack, null)
                    }
                },
                scrollBehavior = appBarScrollBehavior
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onSaveClick) {
                Icon(ComicIcons.Save, null)
            }
        },
        modifier = Modifier.nestedScroll(appBarScrollBehavior.nestedScrollConnection)
    ) { contentPadding ->
        Column {
            OutlinedTextField(
                value = uiState.name,
                onValueChange = onNameChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(contentPadding)
                    .padding(horizontal = ComicTheme.dimension.margin)
            )
            if (lazyPagingItems.isEmptyData) {
                EmptyContent(
                    imageVector = ComicIcons.UndrawNoData,
                    text = "お気に入りはありません",
                    contentPadding = contentPadding
                )
            } else {
                LazyColumn(contentPadding = contentPadding) {
                    items(
                        lazyPagingItems.itemCount,
                        key = lazyPagingItems.itemKey { "${it.bookshelfId.value}${it.path}" }
                    ) {
                        val item = lazyPagingItems[it]
                        if (item != null) {
                            ListItem(
                                headlineContent = { Text(item.name) },
                                leadingContent = {
                                    AsyncImage(model = item, null, Modifier.size(56.dp))
                                },
                                trailingContent = {
                                    IconButton(onClick = { onDeleteClick(item) }) {
                                        Icon(ComicIcons.Delete, null)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun PreviewFavoriteEditScreen() {
    val files = List(20) {
        BookFile(BookshelfId(it), "Name $it", "", "", 0, 0, "", 0, 0, 0)
    }
    val a: Flow<PagingData<File>> = flowOf(PagingData.from(files))
    ComicTheme {
        FavoriteEditScreen(
            FavoriteEditScreenUiState(),
            a.collectAsLazyPagingItems(),
            {},
            {},
            {},
            {}
        )
    }
}
