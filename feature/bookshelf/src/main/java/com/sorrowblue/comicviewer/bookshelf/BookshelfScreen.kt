package com.sorrowblue.comicviewer.bookshelf

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.sorrowblue.comicviewer.bookshelf.component.BookshelfAppBar
import com.sorrowblue.comicviewer.bookshelf.component.BookshelfFab
import com.sorrowblue.comicviewer.bookshelf.section.BookshelfEmptyContents
import com.sorrowblue.comicviewer.bookshelf.section.BookshelfInfoDialog
import com.sorrowblue.comicviewer.bookshelf.section.BookshelfInfoSheet
import com.sorrowblue.comicviewer.bookshelf.section.BookshelfListContents
import com.sorrowblue.comicviewer.bookshelf.section.BookshelfRemoveDialog
import com.sorrowblue.comicviewer.bookshelf.section.BookshelfRemoveDialogUiState
import com.sorrowblue.comicviewer.domain.entity.BookshelfFolder
import com.sorrowblue.comicviewer.domain.entity.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.framework.designsystem.theme.LocalWindowSize
import com.sorrowblue.comicviewer.framework.ui.lifecycle.LaunchedEffectUiEvent
import com.sorrowblue.comicviewer.framework.ui.material3.ReversePermanentNavigationDrawer
import com.sorrowblue.comicviewer.framework.ui.paging.isEmptyData
import com.sorrowblue.comicviewer.framework.ui.toWindowInsets

@Composable
internal fun BookshelfRoute(
    contentPadding: PaddingValues,
    onClickFab: () -> Unit,
    onSettingsClick: () -> Unit,
    onBookshelfClick: (BookshelfId, String) -> Unit,
    onEditClick: (BookshelfId) -> Unit,
    viewModel: BookshelfViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val lazyPagingItems = viewModel.pagingDataFlow.collectAsLazyPagingItems()
    val snackbarHostState = remember { SnackbarHostState() }
    BookshelfScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        lazyPagingItems = lazyPagingItems,
        contentPadding = contentPadding,
        onClickFab = onClickFab,
        onSettingsClick = onSettingsClick,
        onBookshelfClick = onBookshelfClick,
        onBookshelfLongClick = viewModel::onBookshelfLongClick,
        onInfoSheetRemoveClick = viewModel::onRemoveClick,
        onInfoSheetEditClick = { onEditClick(viewModel.uiState.value.bookshelfInfoSheetUiState.bookshelfFolder!!.bookshelf.id) },
        onInfoSheetCloseClick = viewModel::close,
        onRemoveDialogConfirmClick = viewModel::remove,
        onRemoveDialogDismissRequest = viewModel::onRemoveDialogDismissRequest
    )
    LaunchedEffectUiEvent(viewModel) {
        when (it) {
            is BookshelfUiEvent.Message -> {
                snackbarHostState.showSnackbar(it.text)
            }
        }
    }
}

data class BookshelfContentUiState(
    val isVisibleSheet: Boolean = false,
    val bookshelfFolder: BookshelfFolder? = null,
)

data class BookshelfScreenUiState(
    val bookshelfInfoSheetUiState: BookshelfContentUiState = BookshelfContentUiState(),
    val removeDialogUiState: BookshelfRemoveDialogUiState = BookshelfRemoveDialogUiState.Hide,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BookshelfScreen(
    uiState: BookshelfScreenUiState,
    snackbarHostState: SnackbarHostState,
    lazyPagingItems: LazyPagingItems<BookshelfFolder>,
    contentPadding: PaddingValues,
    onClickFab: () -> Unit,
    onSettingsClick: () -> Unit,
    onBookshelfClick: (BookshelfId, String) -> Unit,
    onBookshelfLongClick: (BookshelfFolder) -> Unit,
    onInfoSheetRemoveClick: () -> Unit,
    onInfoSheetEditClick: () -> Unit,
    onInfoSheetCloseClick: () -> Unit,
    onRemoveDialogConfirmClick: () -> Unit,
    onRemoveDialogDismissRequest: () -> Unit,
) {
    val windowSize = LocalWindowSize.current.widthSizeClass
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val lazyGridState = rememberLazyGridState()
    Scaffold(
        topBar = { BookshelfAppBar(onSettingsClick, scrollBehavior) },
        floatingActionButton = {
            AnimatedVisibility(
                visible = !uiState.bookshelfInfoSheetUiState.isVisibleSheet,
                enter = fadeIn(),
                exit = fadeOut(),
                label = "fab"
            ) {
                BookshelfFab(
                    expanded = !lazyGridState.canScrollForward || !lazyGridState.canScrollBackward,
                    onClick = onClickFab
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentWindowInsets = contentPadding.toWindowInsets(),
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        when (windowSize) {
            WindowWidthSizeClass.Compact -> BookshelfCompactContents(
                uiState = uiState.bookshelfInfoSheetUiState,
                lazyPagingItems = lazyPagingItems,
                lazyGridState = lazyGridState,
                contentPadding = innerPadding,
                onBookshelfClick = onBookshelfClick,
                onBookshelfLongClick = onBookshelfLongClick,
                onRemoveClick = onInfoSheetRemoveClick,
                onEditClick = onInfoSheetEditClick,
                onCloseClick = onInfoSheetCloseClick,
            )

            WindowWidthSizeClass.Medium, WindowWidthSizeClass.Expanded -> BookshelfMediumContents(
                uiState = uiState.bookshelfInfoSheetUiState,
                lazyPagingItems = lazyPagingItems,
                lazyGridState = lazyGridState,
                contentPadding = innerPadding,
                onBookshelfClick = onBookshelfClick,
                onBookshelfLongClick = onBookshelfLongClick,
                onRemoveClick = onInfoSheetRemoveClick,
                onEditClick = onInfoSheetEditClick,
                onCloseClick = onInfoSheetCloseClick,
            )
        }
        BookshelfRemoveDialog(
            uiState = uiState.removeDialogUiState,
            onDismissRequest = onRemoveDialogDismissRequest,
            onConfirmClick = onRemoveDialogConfirmClick
        )
    }
}

@Composable
internal fun BookshelfCompactContents(
    uiState: BookshelfContentUiState,
    lazyPagingItems: LazyPagingItems<BookshelfFolder>,
    lazyGridState: LazyGridState,
    contentPadding: PaddingValues,
    onBookshelfClick: (BookshelfId, String) -> Unit,
    onBookshelfLongClick: (BookshelfFolder) -> Unit,
    onRemoveClick: () -> Unit,
    onEditClick: () -> Unit,
    onCloseClick: () -> Unit,
) {
    if (lazyPagingItems.loadState.refresh is LoadState.Loading) {
        LinearProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = contentPadding.calculateTopPadding())
        )
    }
    if (lazyPagingItems.isEmptyData) {
        BookshelfEmptyContents(innerPadding = contentPadding)
    } else {
        BookshelfListContents(
            lazyGridState = lazyGridState,
            innerPadding = contentPadding,
            lazyPagingItems = lazyPagingItems,
            onBookshelfClick = onBookshelfClick,
            onBookshelfLongClick = onBookshelfLongClick
        )
    }
    if (uiState.isVisibleSheet) {
        BookshelfInfoDialog(
            bookshelfFolder = uiState.bookshelfFolder!!,
            onDismissRequest = onCloseClick,
            onRemove = onRemoveClick,
            onEdit = onEditClick
        )
    }
}

@Composable
internal fun BookshelfMediumContents(
    uiState: BookshelfContentUiState,
    lazyPagingItems: LazyPagingItems<BookshelfFolder>,
    lazyGridState: LazyGridState,
    contentPadding: PaddingValues,
    onBookshelfClick: (BookshelfId, String) -> Unit,
    onBookshelfLongClick: (BookshelfFolder) -> Unit,
    onRemoveClick: () -> Unit,
    onEditClick: () -> Unit,
    onCloseClick: () -> Unit,
) {
    ReversePermanentNavigationDrawer(
        drawerContent = {
            AnimatedVisibility(
                visible = uiState.isVisibleSheet,
                enter = expandIn(),
                exit = shrinkOut()
            ) {
                if (uiState.bookshelfFolder != null) {
                    PermanentDrawerSheet(windowInsets = WindowInsets(0)) {
                        BookshelfInfoSheet(
                            contentPadding = contentPadding,
                            bookshelfFolder = uiState.bookshelfFolder,
                            onRemove = onRemoveClick,
                            onEdit = onEditClick,
                            onCloseClick = onCloseClick
                        )
                    }
                }
            }
        }
    ) {
        if (lazyPagingItems.loadState.refresh is LoadState.Loading) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = contentPadding.calculateTopPadding())
            )
        }
        if (lazyPagingItems.isEmptyData) {
            BookshelfEmptyContents(innerPadding = contentPadding)
        } else {
            BookshelfListContents(
                lazyGridState = lazyGridState,
                innerPadding = contentPadding,
                lazyPagingItems = lazyPagingItems,
                onBookshelfClick = onBookshelfClick,
                onBookshelfLongClick = onBookshelfLongClick
            )
        }
    }
}
