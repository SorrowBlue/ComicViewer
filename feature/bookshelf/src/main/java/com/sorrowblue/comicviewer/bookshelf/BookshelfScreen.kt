package com.sorrowblue.comicviewer.bookshelf

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.parameters.CodeGenVisibility
import com.sorrowblue.comicviewer.bookshelf.component.BookshelfFab
import com.sorrowblue.comicviewer.bookshelf.navigation.BookshelfGraph
import com.sorrowblue.comicviewer.bookshelf.navigation.BookshelfGraphTransitions
import com.sorrowblue.comicviewer.bookshelf.section.BookshelfAppBar
import com.sorrowblue.comicviewer.bookshelf.section.BookshelfInfoSheet
import com.sorrowblue.comicviewer.bookshelf.section.BookshelfMainSheet
import com.sorrowblue.comicviewer.bookshelf.section.BookshelfRemoveDialog
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.bookshelf.InternalStorage
import com.sorrowblue.comicviewer.domain.model.file.fakeFolder
import com.sorrowblue.comicviewer.framework.designsystem.theme.LocalDimension
import com.sorrowblue.comicviewer.framework.ui.CanonicalScaffold
import com.sorrowblue.comicviewer.framework.ui.NavTabHandler
import com.sorrowblue.comicviewer.framework.ui.add
import com.sorrowblue.comicviewer.framework.ui.material3.PreviewTheme
import com.sorrowblue.comicviewer.framework.ui.material3.adaptive.navigation.BackHandlerForNavigator
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableStateFlow

interface BookshelfScreenNavigator {
    fun onSettingsClick()
    fun onFabClick()
    fun onBookshelfClick(bookshelfId: BookshelfId, path: String)
    fun onEditClick(bookshelfId: BookshelfId)
}

@Destination<BookshelfGraph>(
    start = true,
    style = BookshelfGraphTransitions::class,
    visibility = CodeGenVisibility.INTERNAL,
)
@Composable
internal fun BookshelfScreen(navigator: BookshelfScreenNavigator) {
    BookshelfScreen(
        onSettingsClick = navigator::onSettingsClick,
        onFabClick = navigator::onFabClick,
        onBookshelfClick = navigator::onBookshelfClick,
        onEditClick = navigator::onEditClick
    )
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
private fun BookshelfScreen(
    onSettingsClick: () -> Unit,
    onFabClick: () -> Unit,
    onBookshelfClick: (BookshelfId, String) -> Unit,
    onEditClick: (BookshelfId) -> Unit,
    state: BookshelfScreenState = rememberBookshelfScreenState(),
) {
    BookshelfScreen(
        navigator = state.navigator,
        lazyPagingItems = state.pagingDataFlow.collectAsLazyPagingItems(),
        lazyGridState = state.lazyGridState,
        snackbarHostState = state.snackbarHostState,
        onFabClick = onFabClick,
        onSettingsClick = onSettingsClick,
        onBookshelfClick = onBookshelfClick,
        onBookshelfInfoClick = state::onBookshelfInfoClick,
        onInfoSheetRemoveClick = state::onRemoveClick,
        onInfoSheetEditClick = { onEditClick(state.bookshelfId) },
        onInfoSheetCloseClick = state::onInfoSheetCloseClick,
        onInfoSheetScanClick = state::onInfoSheetScanClick,
        onReThumbnailsClick = state::onReThumbnailsClick,
    )
    val removeDialogController = state.removeDialogController
    if (removeDialogController.isShow) {
        BookshelfRemoveDialog(
            title = removeDialogController.value!!.bookshelf.displayName,
            onDismissRequest = state::onDismissRequest,
            onConfirmClick = state::onConfirmClick,
        )
    }

    BackHandlerForNavigator(navigator = state.navigator)

    NavTabHandler(onClick = state::onNavClick)
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun BookshelfScreen(
    navigator: ThreePaneScaffoldNavigator<BookshelfFolder>,
    lazyPagingItems: LazyPagingItems<BookshelfFolder>,
    snackbarHostState: SnackbarHostState,
    onFabClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onBookshelfClick: (BookshelfId, String) -> Unit,
    onBookshelfInfoClick: (BookshelfFolder) -> Unit,
    onInfoSheetRemoveClick: () -> Unit,
    onInfoSheetEditClick: () -> Unit,
    onInfoSheetScanClick: () -> Unit,
    onReThumbnailsClick: () -> Unit,
    onInfoSheetCloseClick: () -> Unit,
    lazyGridState: LazyGridState = rememberLazyGridState(),
) {
    val expanded by remember(lazyGridState) {
        derivedStateOf { !lazyGridState.canScrollForward || !lazyGridState.canScrollBackward }
    }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    CanonicalScaffold(
        navigator = navigator,
        topBar = {
            BookshelfAppBar(onSettingsClick = onSettingsClick, scrollBehavior = scrollBehavior)
        },
        floatingActionButton = { BookshelfFab(expanded = expanded, onClick = onFabClick) },
        extraPane = { innerPadding ->
            navigator.currentDestination?.content?.let {
                BookshelfInfoSheet(
                    contentPadding = innerPadding,
                    scaffoldDirective = navigator.scaffoldDirective,
                    bookshelfFolder = it,
                    onRemoveClick = onInfoSheetRemoveClick,
                    onEditClick = onInfoSheetEditClick,
                    onScanClick = onInfoSheetScanClick,
                    onReThumbnailsClick = onReThumbnailsClick,
                    onCloseClick = onInfoSheetCloseClick
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { contentPadding ->
        val dimension = LocalDimension.current
        val innerPadding = contentPadding.add(
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
            innerPadding = innerPadding
        )
    }
}

private val FabSpace get() = 72.dp

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@PreviewScreenSizes
@Composable
private fun PreviewBookshelfScreen() {
    PreviewTheme {
        val pagingDataFlow = remember {
            List(15) {
                BookshelfFolder(
                    InternalStorage(BookshelfId(it), "name"),
                    fakeFolder()
                )
            }.toPersistentList().let {
                MutableStateFlow(PagingData.from(it))
            }
        }
        val lazyPagingItems = pagingDataFlow.collectAsLazyPagingItems()
        BookshelfScreen(
            snackbarHostState = remember { SnackbarHostState() },
            navigator = rememberSupportingPaneScaffoldNavigator<BookshelfFolder>(),
            lazyPagingItems = lazyPagingItems,
            onFabClick = {},
            onSettingsClick = {},
            onBookshelfClick = { _, _ -> },
            onBookshelfInfoClick = {},
            onInfoSheetRemoveClick = {},
            onInfoSheetEditClick = {},
            onInfoSheetScanClick = {},
            onReThumbnailsClick = {},
            onInfoSheetCloseClick = {},
        )
    }
}
