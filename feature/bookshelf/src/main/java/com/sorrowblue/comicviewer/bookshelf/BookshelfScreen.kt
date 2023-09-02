package com.sorrowblue.comicviewer.bookshelf

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Add
import androidx.compose.material.icons.twotone.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltipBox
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.sorrowblue.comicviewer.bookshelf.component.BookshelfFolderRow
import com.sorrowblue.comicviewer.bookshelf.section.BookshelfInfoSheet
import com.sorrowblue.comicviewer.bookshelf.section.BookshelfRemoveDialog
import com.sorrowblue.comicviewer.domain.entity.BookshelfFolder
import com.sorrowblue.comicviewer.domain.entity.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.entity.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.feature.bookshelf.R
import com.sorrowblue.comicviewer.framework.compose.AppMaterialTheme
import com.sorrowblue.comicviewer.framework.compose.copy
import com.sorrowblue.comicviewer.framework.compose.isEmptyData

@Composable
fun BookshelfRoute(
    contentPadding: PaddingValues,
    onClickFab: () -> Unit,
    onSettingsClick: () -> Unit,
    onBookshelfClick: (BookshelfFolder) -> Unit,
    onEditClick: (BookshelfId) -> Unit,
    viewModel: BookshelfViewModel = hiltViewModel()
) {
    val lazyPagingItems = viewModel.pagingDataFlow.collectAsLazyPagingItems()
    BookshelfScreen(
        lazyPagingItems,
        onClickFab = onClickFab,
        contentPadding = contentPadding,
        onSettingsClick = onSettingsClick,
        onBookshelfClick = onBookshelfClick,
        onEditClick = onEditClick,
        onRemoveClick = viewModel::remove,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookshelfScreen(
    lazyPagingItems: LazyPagingItems<BookshelfFolder>,
    contentPadding: PaddingValues,
    onClickFab: () -> Unit,
    onSettingsClick: () -> Unit,
    onBookshelfClick: (BookshelfFolder) -> Unit,
    onEditClick: (BookshelfId) -> Unit,
    onRemoveClick: (Bookshelf) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val localLayoutDirection = LocalLayoutDirection.current
    var showBookshelfInfoSheet by remember { mutableStateOf(false) }
    var selectedBookshelfInfo by remember { mutableStateOf<BookshelfFolder?>(null) }
    val lazyGridState = rememberLazyGridState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.bookshelf_list_title)) },
                actions = {
                    PlainTooltipBox(tooltip = { Text(stringResource(id = com.sorrowblue.comicviewer.framework.resource.R.string.framework_label_open_settings)) }) {
                        IconButton(onSettingsClick, Modifier.tooltipAnchor()) {
                            Icon(
                                Icons.TwoTone.Settings,
                                stringResource(id = com.sorrowblue.comicviewer.framework.resource.R.string.framework_label_open_settings)
                            )
                        }
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onClickFab,
                text = { Text("New Bookshelf") },
                icon = { Icon(Icons.TwoTone.Add, contentDescription = null) },
                expanded = !lazyGridState.canScrollForward || !lazyGridState.canScrollBackward
            )
        },
        contentWindowInsets = WindowInsets(
            left = contentPadding.calculateLeftPadding(localLayoutDirection),
            top = contentPadding.calculateTopPadding(),
            right = contentPadding.calculateRightPadding(localLayoutDirection),
            bottom = contentPadding.calculateBottomPadding()
        ),
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { contentPadding ->
        if (lazyPagingItems.isEmptyData) {
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
            if (lazyPagingItems.loadState.refresh is LoadState.Loading) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = contentPadding.calculateTopPadding())
                )
            }
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                state = lazyGridState,
                contentPadding = contentPadding.copy(
                    start = AppMaterialTheme.dimens.margin,
                    end = AppMaterialTheme.dimens.margin,
                    top = AppMaterialTheme.dimens.margin,
                    bottom = AppMaterialTheme.dimens.margin + 96.dp
                ),
                verticalArrangement = Arrangement.spacedBy(AppMaterialTheme.dimens.spacer),
                horizontalArrangement = Arrangement.spacedBy(AppMaterialTheme.dimens.spacer)
            ) {
                items(
                    count = lazyPagingItems.itemCount,
                    key = lazyPagingItems.itemKey { it.bookshelf.id.value },
                    contentType = { lazyPagingItems.itemContentType { "contentType" } }
                ) {
                    val item = lazyPagingItems[it]
                    if (item != null) {
                        BookshelfFolderRow(
                            bookshelfFolder = item,
                            onClick = { onBookshelfClick(item) },
                            onLongClick = {
                                selectedBookshelfInfo = item
                                showBookshelfInfoSheet = true
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }

    var showBookshelfRemoveDialog by remember { mutableStateOf(false) }

    if (showBookshelfInfoSheet && selectedBookshelfInfo != null) {
        BookshelfInfoSheet(
            bookshelfFolder = selectedBookshelfInfo!!,
            onDismissRequest = { showBookshelfInfoSheet = false },
            onEdit = { onEditClick(selectedBookshelfInfo!!.bookshelf.id) },
            onRemove = {
                showBookshelfInfoSheet = false
                showBookshelfRemoveDialog = true
            }
        )
    }

    if (showBookshelfRemoveDialog && selectedBookshelfInfo != null) {
        BookshelfRemoveDialog(
            title = selectedBookshelfInfo!!.bookshelf.displayName,
            onDismissRequest = { showBookshelfRemoveDialog = false },
            onRemove = {
                onRemoveClick(selectedBookshelfInfo!!.bookshelf)
                showBookshelfRemoveDialog = false
            }
        )
    }
}
