package com.sorrowblue.comicviewer.bookshelf

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.sorrowblue.comicviewer.bookshelf.section.BookshelfBottomSheet
import com.sorrowblue.comicviewer.bookshelf.section.BookshelfEmptyContents
import com.sorrowblue.comicviewer.bookshelf.section.BookshelfListContents
import com.sorrowblue.comicviewer.bookshelf.section.BookshelfRemoveDialog
import com.sorrowblue.comicviewer.bookshelf.section.BookshelfRemoveDialogUiState
import com.sorrowblue.comicviewer.bookshelf.section.BookshelfSideSheet
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.feature.bookshelf.R
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.CommonViewModel
import com.sorrowblue.comicviewer.framework.ui.add
import com.sorrowblue.comicviewer.framework.ui.asWindowInsets
import com.sorrowblue.comicviewer.framework.ui.lifecycle.LaunchedEffectUiEvent
import com.sorrowblue.comicviewer.framework.ui.material3.TopAppBarDefaults
import com.sorrowblue.comicviewer.framework.ui.material3.pinnedScrollBehavior
import com.sorrowblue.comicviewer.framework.ui.paging.isEmptyData
import com.sorrowblue.comicviewer.framework.ui.preview.rememberMobile
import com.sorrowblue.comicviewer.framework.ui.responsive.ResponsiveScaffold
import com.sorrowblue.comicviewer.framework.ui.responsive.ResponsiveScaffoldState
import com.sorrowblue.comicviewer.framework.ui.responsive.ResponsiveTopAppBar
import com.sorrowblue.comicviewer.framework.ui.responsive.SideSheetValueState
import com.sorrowblue.comicviewer.framework.ui.responsive.rememberResponsiveScaffoldState

@Composable
internal fun BookshelfRoute(
    contentPadding: PaddingValues,
    onSettingsClick: () -> Unit,
    onBookshelfClick: (BookshelfId, String) -> Unit,
    onEditClick: (BookshelfId) -> Unit,
    viewModel: BookshelfViewModel = hiltViewModel(),
    commonViewModel: CommonViewModel = hiltViewModel(LocalContext.current as ComponentActivity),
) {
    val uiState by viewModel.uiState.collectAsState()
    val lazyPagingItems = viewModel.pagingDataFlow.collectAsLazyPagingItems()
    val state = rememberResponsiveScaffoldState(
        sideSheetState = rememberSideSheetBookshelfFolderState()
    )
    BookshelfScreen(
        state = state,
        uiState = uiState,
        lazyPagingItems = lazyPagingItems,
        contentPadding = contentPadding,
        canScroll = { commonViewModel.canScroll = it },
        onSettingsClick = onSettingsClick,
        onBookshelfClick = onBookshelfClick,
        onBookshelfLongClick = state.sheetState::show,
        onInfoSheetRemoveClick = {
            viewModel.onRemoveClick(state.sheetState.currentValue!!.bookshelf)
        },
        onInfoSheetEditClick = { onEditClick(state.sheetState.currentValue!!.bookshelf.id) },
        onInfoSheetCloseClick = state.sheetState::hide,
        onInfoSheetScanClick = {
            viewModel.scan(state.sheetState.currentValue!!.folder)
        },
        onRemoveDialogConfirmClick = {
            state.sheetState.hide()
            viewModel.remove(state.sheetState.currentValue!!.bookshelf)
        },
        onRemoveDialogDismissRequest = viewModel::onRemoveDialogDismissRequest
    )
    LaunchedEffectUiEvent(viewModel) {
        when (it) {
            is BookshelfUiEvent.Message -> {
                state.snackbarHostState.showSnackbar(it.text)
            }
        }
    }
}

data class BookshelfScreenUiState(
    val removeDialogUiState: BookshelfRemoveDialogUiState = BookshelfRemoveDialogUiState.Hide,
)

@Composable
private fun BookshelfScreen(
    state: ResponsiveScaffoldState<BookshelfFolder>,
    uiState: BookshelfScreenUiState,
    lazyPagingItems: LazyPagingItems<BookshelfFolder>,
    contentPadding: PaddingValues,
    canScroll: (Boolean) -> Unit,
    onSettingsClick: () -> Unit,
    onBookshelfClick: (BookshelfId, String) -> Unit,
    onBookshelfLongClick: (BookshelfFolder) -> Unit,
    onInfoSheetRemoveClick: () -> Unit,
    onInfoSheetEditClick: () -> Unit,
    onInfoSheetScanClick: () -> Unit,
    onInfoSheetCloseClick: () -> Unit,
    onRemoveDialogConfirmClick: () -> Unit,
    onRemoveDialogDismissRequest: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val lazyGridState = rememberLazyGridState()
    LaunchedEffect(lazyGridState.canScrollForward, lazyGridState.canScrollBackward) {
        canScroll(lazyGridState.canScrollForward && lazyGridState.canScrollBackward)
    }
    ResponsiveScaffold(
        topBar = {
            ResponsiveTopAppBar(
                title = R.string.bookshelf_list_title,
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(imageVector = ComicIcons.Settings, contentDescription = null)
                    }
                },
                windowInsets = contentPadding.asWindowInsets(),
                scrollBehavior = scrollBehavior,
            )
        },
        bottomSheet = { bookshelfFolder ->
            BookshelfBottomSheet(
                bookshelfFolder = bookshelfFolder,
                onRemove = onInfoSheetRemoveClick,
                onEdit = onInfoSheetEditClick,
                onScanClick = onInfoSheetScanClick
            )
        },
        sideSheet = { bookshelfFolder, innerPadding ->
            BookshelfSideSheet(
                bookshelfFolder = bookshelfFolder,
                innerPadding = innerPadding,
                onRemoveClick = onInfoSheetRemoveClick,
                onEditClick = onInfoSheetEditClick,
                onScanClick = onInfoSheetScanClick,
                onCloseClick = onInfoSheetCloseClick
            )
        },
        state = state,
        contentWindowInsets = contentPadding.asWindowInsets(),
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        BookshelfMainSheet(
            lazyPagingItems,
            innerPadding.add(
                paddingValues = PaddingValues(
                    top = if (rememberMobile()) 0.dp else ComicTheme.dimension.margin,
                    start = ComicTheme.dimension.margin,
                    end = ComicTheme.dimension.margin,
                    bottom = ComicTheme.dimension.margin + if (rememberMobile()) 72.dp else 0.dp
                )
            ),
            lazyGridState,
            onBookshelfClick,
            onBookshelfLongClick
        )
    }
    BookshelfRemoveDialog(
        uiState = uiState.removeDialogUiState,
        onDismissRequest = onRemoveDialogDismissRequest,
        onConfirmClick = onRemoveDialogConfirmClick
    )
}

@Composable
fun rememberSideSheetBookshelfFolderState(
    initialValue: BookshelfFolder? = null,
): SideSheetValueState<BookshelfFolder> {
    return rememberSaveable(saver =
    mapSaver(
        save = { mapOf("show" to it.show, "currentValue" to it.currentValue) },
        restore = { savedValue ->
            val bookshelf = savedValue["currentValue"] as? BookshelfFolder
            val show = savedValue["show"] as? Boolean ?: false
            SideSheetValueState(
                initialValue = bookshelf,
                initialShow = show
            )
        }
    )) {
        SideSheetValueState(initialValue)
    }
}

@Composable
private fun BookshelfMainSheet(
    lazyPagingItems: LazyPagingItems<BookshelfFolder>,
    innerPadding: PaddingValues,
    lazyGridState: LazyGridState,
    onBookshelfClick: (BookshelfId, String) -> Unit,
    onBookshelfLongClick: (BookshelfFolder) -> Unit,
) {
    if (lazyPagingItems.isEmptyData) {
        BookshelfEmptyContents(innerPadding = innerPadding)
    } else {
        BookshelfListContents(
            lazyGridState = lazyGridState,
            innerPadding = innerPadding,
            lazyPagingItems = lazyPagingItems,
            onBookshelfClick = onBookshelfClick,
            onBookshelfLongClick = onBookshelfLongClick
        )
    }
    if (lazyPagingItems.loadState.refresh is LoadState.Loading) {
        LinearProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = innerPadding.calculateTopPadding())
        )
    }
}
