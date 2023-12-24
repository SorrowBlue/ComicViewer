package com.sorrowblue.comicviewer.bookshelf

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.PaneAdaptedValue
import androidx.compose.material3.adaptive.ThreePaneScaffoldNavigator
import androidx.compose.material3.adaptive.rememberSupportingPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.sorrowblue.comicviewer.bookshelf.section.BookshelfInfoSheet
import com.sorrowblue.comicviewer.bookshelf.section.BookshelfMainSheet
import com.sorrowblue.comicviewer.bookshelf.section.BookshelfRemoveDialog
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.bookshelf.InternalStorage
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.feature.bookshelf.R
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.LocalDimension
import com.sorrowblue.comicviewer.framework.ui.CanonicalScaffold
import com.sorrowblue.comicviewer.framework.ui.PreviewComic
import com.sorrowblue.comicviewer.framework.ui.add
import com.sorrowblue.comicviewer.framework.ui.asWindowInsets
import com.sorrowblue.comicviewer.framework.ui.material3.PreviewTheme
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableStateFlow

context(NavBackStackEntry)
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
internal fun BookshelfRoute(
    contentPadding: PaddingValues,
    onSettingsClick: () -> Unit,
    onFabClick: () -> Unit,
    onBookshelfClick: (BookshelfId, String) -> Unit,
    onEditClick: (BookshelfId) -> Unit,
    state: BookshelfScreenState = rememberBookshelfScreenState(),
) {
    BookshelfScreen(
        navigator = state.navigator,
        bookshelfFolder = state.bookshelfFolder,
        lazyPagingItems = state.pagingDataFlow.collectAsLazyPagingItems(),
        snackbarHostState = state.snackbarHostState,
        contentPadding = contentPadding,
        onFabClick = onFabClick,
        onSettingsClick = onSettingsClick,
        onBookshelfClick = onBookshelfClick,
        onBookshelfInfoClick = state::onBookshelfLongClick,
        onInfoSheetRemoveClick = state::onRemoveClick,
        onInfoSheetEditClick = { onEditClick(state.bookshelfId) },
        onInfoSheetCloseClick = state::onInfoSheetCloseClick,
        onInfoSheetScanClick = state::onInfoSheetScanClick,
    )
    val removeDialogController = state.removeDialogController
    if (removeDialogController.isShow) {
        BookshelfRemoveDialog(
            title = removeDialogController.value!!.bookshelf.displayName,
            onDismissRequest = state::onDismissRequest,
            onConfirmClick = state::onConfirmClick,
        )
    }

    BackHandler(enabled = state.navigator.scaffoldState.scaffoldValue.tertiary == PaneAdaptedValue.Expanded) {
        state.navigator.navigateBack()
    }
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun BookshelfScreen(
    navigator: ThreePaneScaffoldNavigator,
    bookshelfFolder: BookshelfFolder?,
    lazyPagingItems: LazyPagingItems<BookshelfFolder>,
    snackbarHostState: SnackbarHostState,
    contentPadding: PaddingValues,
    onFabClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onBookshelfClick: (BookshelfId, String) -> Unit,
    onBookshelfInfoClick: (BookshelfFolder) -> Unit,
    onInfoSheetRemoveClick: () -> Unit,
    onInfoSheetEditClick: () -> Unit,
    onInfoSheetScanClick: () -> Unit,
    onInfoSheetCloseClick: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val lazyGridState = rememberLazyGridState()
    CanonicalScaffold(
        navigator = navigator,
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.bookshelf_list_title))
                },
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(imageVector = ComicIcons.Settings, contentDescription = null)
                    }
                },
                windowInsets = contentPadding.asWindowInsets()
                    .only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal),
                scrollBehavior = scrollBehavior,
            )
        },
        floatingActionButton = {
            val expanded by remember(lazyGridState) {
                derivedStateOf { !lazyGridState.canScrollForward || !lazyGridState.canScrollBackward }
            }
            ExtendedFloatingActionButton(
                expanded = expanded,
                onClick = onFabClick,
                text = { Text(text = "Add") },
                icon = { Icon(imageVector = ComicIcons.Add, contentDescription = null) },
                modifier = Modifier.windowInsetsPadding(
                    WindowInsets.safeDrawing.only(
                        WindowInsetsSides.End
                    )
                )
            )
        },
        extraPane = { innerPadding ->
            if (bookshelfFolder != null) {
                BookshelfInfoSheet(
                    contentPadding = innerPadding,
                    scaffoldDirective = navigator.scaffoldState.scaffoldDirective,
                    bookshelfFolder = bookshelfFolder,
                    onRemoveClick = onInfoSheetRemoveClick,
                    onEditClick = onInfoSheetEditClick,
                    onScanClick = onInfoSheetScanClick,
                    onCloseClick = onInfoSheetCloseClick
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentPadding = contentPadding,
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { innerPadding ->
        val dimension = LocalDimension.current
        val inInnerPadding = innerPadding.add(
            PaddingValues(
                start = dimension.margin,
                top = dimension.margin,
                end = dimension.margin,
                bottom = dimension.margin + FabSpace
            )
        )
        BookshelfMainSheet(
            lazyPagingItems = lazyPagingItems,
            lazyGridState = lazyGridState,
            onBookshelfClick = onBookshelfClick,
            onBookshelfInfoClick = onBookshelfInfoClick,
            innerPadding = inInnerPadding
        )
    }
}

private val FabSpace get() = 72.dp

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@PreviewComic
@Composable
private fun PreviewBookshelfScreen() {
    PreviewTheme {
        val pagingDataFlow = remember {
            List(15) {
                BookshelfFolder(
                    InternalStorage(BookshelfId(it), "name"),
                    Folder(
                        BookshelfId(it),
                        "path",
                        "name",
                        "name",
                        0L,
                        0,
                        emptyMap(),
                        0,
                        0,
                    )
                )
            }.toPersistentList().let {
                MutableStateFlow(PagingData.from(it))
            }
        }
        val lazyPagingItems = pagingDataFlow.collectAsLazyPagingItems()
        BookshelfScreen(
            snackbarHostState = remember { SnackbarHostState() },
            navigator = rememberSupportingPaneScaffoldNavigator(),
            bookshelfFolder = BookshelfFolder(
                InternalStorage(BookshelfId(0), "display name", 0),
                Folder(
                    BookshelfId(0),
                    "",
                    "",
                    "",
                    0,
                    0
                )
            ),
            lazyPagingItems = lazyPagingItems,
            contentPadding = PaddingValues(),
            onFabClick = {},
            onSettingsClick = {},
            onBookshelfClick = { _, _ -> },
            onBookshelfInfoClick = {},
            onInfoSheetRemoveClick = {},
            onInfoSheetEditClick = {},
            onInfoSheetScanClick = {},
            onInfoSheetCloseClick = {},
        )
    }
}
