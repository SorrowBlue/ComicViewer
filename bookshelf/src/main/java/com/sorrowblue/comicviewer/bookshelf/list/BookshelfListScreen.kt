package com.sorrowblue.comicviewer.bookshelf.list

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.compose.currentBackStackEntryAsState
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
internal fun BookshelfListScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: BookshelfListViewModel = hiltViewModel()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val lazyListState = rememberLazyGridState()
    val state = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(state)
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        navBackStackEntry?.destination?.label?.toString().orEmpty(),
                        modifier = Modifier.basicMarquee()
                    )
                },
                scrollBehavior = scrollBehavior,
                actions = {
                    IconButton(onClick = {
                        navController.navigate(R.id.action_global_settings_navigation)
                    }) {
                        Icon(Icons.TwoTone.Settings, null)
                    }
                }
            )
        },
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { contentPadding ->
        val articles = viewModel.pagingDataFlow.collectAsLazyPagingItems()
        val nestedScrollConnection = rememberNestedScrollInteropConnection()
        val isRefreshing = viewModel.isRefreshingFlow.collectAsState()
        val isEmptyData = viewModel.isEmptyDataFlow.collectAsState()
        var bookshelfFolder by remember { mutableStateOf<BookshelfFolder?>(null) }
        var showBottomSheet by remember { mutableStateOf(false) }
        if (isEmptyData.value) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(com.sorrowblue.comicviewer.framework.resource.R.drawable.ic_undraw_bookshelves_re_lxoy),
                    contentDescription = null,
                    modifier = Modifier.size(200.dp)
                )
                Text(
                    text = stringResource(id = R.string.bookshelf_list_message_no_bookshelves_added_yet),
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        } else {
            Column {
                Spacer(modifier = Modifier.padding(top = contentPadding.calculateTopPadding()))
                if (isRefreshing.value) {
                    LinearProgressIndicator(Modifier.fillMaxWidth())
                }
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    state = lazyListState,
                    contentPadding = PaddingValues(
                        start = contentPadding.calculateStartPadding(LocalLayoutDirection.current) + 16.dp,
                        top = 16.dp,
                        end = contentPadding.calculateEndPadding(LocalLayoutDirection.current) + 16.dp,
                        bottom = contentPadding.calculateBottomPadding() + 16.dp + 72.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.nestedScroll(nestedScrollConnection)
                ) {
                    items(count = articles.itemCount,
                        key = articles.itemKey { it.bookshelf.id.value },
                        contentType = { articles.itemContentType { "contentType" } }
                    ) {
                        val item = articles[it]
                        if (item != null) {
                            BookshelfFolderRow(
                                bookshelfFolder = item, modifier = Modifier
                                    .fillMaxWidth()
                                    .combinedClickable(
                                        onLongClick = {
                                            bookshelfFolder = item
                                            showBottomSheet = true
                                        },
                                        onClick = {
                                            navController.navigate(
                                                BookshelfListFragmentDirections.actionBookshelfListToFolder(
                                                    item.folder
                                                )
                                            )
                                        }
                                    )
                            )
                        }
                    }
                }
            }
            val sheetState = rememberModalBottomSheetState(false)
            if (showBottomSheet && bookshelfFolder != null) {
                val bookshelf = bookshelfFolder!!.bookshelf
                val folder = bookshelfFolder!!.folder
                ModalBottomSheet(
                    onDismissRequest = { showBottomSheet = false },
                    sheetState = sheetState
                ) {
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
                            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                        ) {
                            Button(onClick = {
                                navController.navigate(
                                    BookshelfListFragmentDirections.actionBookshelfListToBookshelfRemoveConfirm(bookshelf.id.value)
                                )
                            }) {
                                Text(text = stringResource(id = R.string.bookshelf_info_btn_remove))
                            }
                            FilledTonalButton(onClick = {
                                when (bookshelf) {
                                    is InternalStorage ->
                                        navController.navigate(
                                            BookshelfListFragmentDirections.actionBookshelfListToBookshelfManageDevice(
                                                bookshelf.id.value
                                            )
                                        )
                                    is SmbServer ->
                                        navController.navigate(
                                            BookshelfListFragmentDirections.actionBookshelfListToBookshelfManageSmb(bookshelf.id.value)
                                        )
                                }
                            }) {
                                Text(text = stringResource(id = R.string.bookshelf_info_btn_edit))
                            }
                        }
                    }
                }
            }
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
