package com.sorrowblue.comicviewer.bookshelf.list

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.PlainTooltipBox
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDirections
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.sorrowblue.comicviewer.bookshelf.BookshelfConverter.source
import com.sorrowblue.comicviewer.bookshelf.R
import com.sorrowblue.comicviewer.domain.entity.BookshelfFolder
import com.sorrowblue.comicviewer.domain.entity.bookshelf.InternalStorage
import com.sorrowblue.comicviewer.domain.entity.bookshelf.SmbServer
import com.sorrowblue.comicviewer.domain.entity.file.IFolder
import com.sorrowblue.comicviewer.folder.FolderFragmentArgs
import com.sorrowblue.comicviewer.framework.compose.AppMaterialTheme
import com.sorrowblue.comicviewer.framework.resource.R as FrameworkResourceR


@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun BookshelfListTopAppBar(
    navController: NavHostController,
    scrollBehavior: TopAppBarScrollBehavior
) {
    TopAppBar(
        title = { Text(stringResource(id = R.string.bookshelf_list_title)) },
        scrollBehavior = scrollBehavior,
        actions = {
            PlainTooltipBox(
                tooltip = { Text(stringResource(id = FrameworkResourceR.string.framework_label_open_settings)) }
            ) {
                IconButton(
                    onClick = { navController.navigate(R.id.action_global_settings_navigation) },
                    modifier = Modifier.tooltipAnchor()
                ) {
                    Icon(
                        Icons.TwoTone.Settings,
                        stringResource(id = FrameworkResourceR.string.framework_label_open_settings)
                    )
                }
            }
        }
    )
}

@Composable
fun EmptyContent() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(FrameworkResourceR.drawable.ic_undraw_bookshelves_re_lxoy),
            contentDescription = null,
            modifier = Modifier.size(200.dp)
        )
        Text(
            text = stringResource(id = R.string.bookshelf_list_message_no_bookshelves_added_yet),
            style = MaterialTheme.typography.headlineSmall
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BookshelfListContents(
    contentPadding: PaddingValues,
    articles: LazyPagingItems<BookshelfFolder>,
    itemClick: (BookshelfFolder) -> Unit,
    itemLongClick: (BookshelfFolder) -> Unit,
    lazyListState: LazyGridState = rememberLazyGridState(),
    nestedScrollConnection: NestedScrollConnection = rememberNestedScrollInteropConnection()
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        state = lazyListState,
        contentPadding = PaddingValues(
            start = contentPadding.calculateStartPadding(LocalLayoutDirection.current) + AppMaterialTheme.dimens.margin,
            top = contentPadding.calculateTopPadding() + AppMaterialTheme.dimens.margin,
            end = contentPadding.calculateEndPadding(LocalLayoutDirection.current) + AppMaterialTheme.dimens.margin,
            bottom = contentPadding.calculateBottomPadding() + AppMaterialTheme.dimens.margin + 72.dp
        ),
        verticalArrangement = Arrangement.spacedBy(AppMaterialTheme.dimens.spacer),
        horizontalArrangement = Arrangement.spacedBy(AppMaterialTheme.dimens.spacer),
        modifier = Modifier.nestedScroll(nestedScrollConnection)
    ) {
        items(
            count = articles.itemCount,
            key = articles.itemKey { it.bookshelf.id.value },
            contentType = { articles.itemContentType { "contentType" } }
        ) {
            val item = articles[it]
            if (item != null) {
                BookshelfFolderRow(
                    bookshelfFolder = item,
                    modifier = Modifier
                        .fillMaxWidth()
                        .combinedClickable(
                            onLongClick = { itemClick(item) },
                            onClick = { itemLongClick(item) }
                        )
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun BookshelfListScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: BookshelfListViewModel = hiltViewModel()
) {
    val scrollBehavior: TopAppBarScrollBehavior =
        TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        topBar = {
            BookshelfListTopAppBar(navController = navController, scrollBehavior = scrollBehavior)
        },
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { contentPadding ->
        val articles = viewModel.pagingDataFlow.collectAsLazyPagingItems()
        var bookshelfFolder by remember { mutableStateOf<BookshelfFolder?>(null) }
        var showBottomSheet by remember { mutableStateOf(false) }

        if (articles.loadState.refresh is LoadState.NotLoading && articles.loadState.append.endOfPaginationReached && articles.itemCount == 0) {
            EmptyContent()
        } else {
            if (articles.loadState.refresh is LoadState.Loading) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = contentPadding.calculateTopPadding())
                )
            }
            BookshelfListContents(
                contentPadding,
                articles,
                {
                    bookshelfFolder = it
                    showBottomSheet = true
                },
                {
                    navController.navigate(
                        BookshelfListFragmentDirections.actionBookshelfListToFolder(it.folder)
                    )
                }
            )
        }

        var showDialog by remember { mutableStateOf(false) }
        if (showBottomSheet && bookshelfFolder != null) {
            BookshelfInfoSheet(
                bookshelfFolder = bookshelfFolder!!,
                onDismissRequest = { showBottomSheet = false },
                onEdit = {
                    when (bookshelfFolder!!.bookshelf) {
                        is InternalStorage ->
                            navController.navigate(
                                BookshelfListFragmentDirections.actionBookshelfListToBookshelfManageDevice(
                                    bookshelfFolder!!.bookshelf.id.value
                                )
                            )

                        is SmbServer ->
                            navController.navigate(
                                BookshelfListFragmentDirections.actionBookshelfListToBookshelfManageSmb(
                                    bookshelfFolder!!.bookshelf.id.value
                                )
                            )
                    }
                },
                onRemove = {
                    showBottomSheet = false
                    showDialog = true
                }
            )
        }
        if (showDialog && bookshelfFolder != null) {
            BookshelfRemoveDialog(
                title = bookshelfFolder!!.bookshelf.displayName,
                onDismissRequest = { showDialog = false },
                onRemove = {
                    viewModel.remove(bookshelfFolder!!.bookshelf)
                    showDialog = false
                }
            )
        }
    }
}

private fun BookshelfListFragmentDirections.Companion.actionBookshelfListToFolder(
    folder: IFolder,
) = object : NavDirections {
    override val actionId = actionBookshelfListToFolder().actionId
    override val arguments = FolderFragmentArgs(
        folder.bookshelfId.value,
        folder.base64Path(),
    ).toBundle()

}

@Composable
fun BookshelfRemoveDialog(title: String, onDismissRequest: () -> Unit, onRemove: () -> Unit) {
    AlertDialog(onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = onRemove) {
                Text(stringResource(id = android.R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(id = android.R.string.cancel))
            }
        },
        title = {
            Text("削除の確認")
        },
        text = {
            Text("$title を削除しますか？")
        })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookshelfInfoSheet(
    bookshelfFolder: BookshelfFolder,
    onDismissRequest: () -> Unit,
    onRemove: () -> Unit,
    onEdit: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(false)
) {
    val bookshelf = bookshelfFolder.bookshelf
    val folder = bookshelfFolder.folder
    ModalBottomSheet(onDismissRequest = onDismissRequest, sheetState = sheetState) {
        Column(Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = stringResource(id = bookshelf.source()),
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.align(Alignment.End)
            )
            Text(
                text = bookshelf.displayName,
                style = MaterialTheme.typography.titleLarge
            )

            when (bookshelf) {
                is InternalStorage -> {
                    Text(
                        text = stringResource(id = R.string.bookshelf_info_label_path),
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                    Text(
                        text = folder.path,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                is SmbServer -> {
                    Text(
                        text = stringResource(id = R.string.bookshelf_info_label_host),
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                    Text(
                        text = bookshelf.host,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = stringResource(id = R.string.bookshelf_info_label_port),
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                    Text(
                        text = bookshelf.port.toString(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = stringResource(id = R.string.bookshelf_info_label_path),
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                    Text(
                        text = folder.path,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Button(onClick = onRemove) {
                    Text(text = stringResource(id = R.string.bookshelf_info_btn_remove))
                }
                FilledTonalButton(onClick = onEdit) {
                    Text(text = stringResource(id = R.string.bookshelf_info_btn_edit))
                }
            }
        }
    }

}
