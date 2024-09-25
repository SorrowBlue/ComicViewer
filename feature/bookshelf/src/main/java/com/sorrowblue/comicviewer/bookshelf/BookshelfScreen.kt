package com.sorrowblue.comicviewer.bookshelf

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.parameters.CodeGenVisibility
import com.ramcosta.composedestinations.result.ResultRecipient
import com.sorrowblue.comicviewer.bookshelf.component.BookshelfFab
import com.sorrowblue.comicviewer.bookshelf.info.BookshelfInfoSheet
import com.sorrowblue.comicviewer.bookshelf.info.BookshelfInfoSheetNavigator
import com.sorrowblue.comicviewer.bookshelf.navigation.BookshelfGraph
import com.sorrowblue.comicviewer.bookshelf.section.BookshelfAppBar
import com.sorrowblue.comicviewer.bookshelf.section.BookshelfMainSheet
import com.sorrowblue.comicviewer.bookshelf.section.NotificationRequestResult
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.feature.bookshelf.destinations.NotificationRequestDialogDestination
import com.sorrowblue.comicviewer.feature.bookshelf.remove.destinations.BookshelfRemoveDialogDestination
import com.sorrowblue.comicviewer.framework.ui.NavTabHandler
import com.sorrowblue.comicviewer.framework.ui.adaptive.CanonicalScaffold
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewMultiScreen
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme3
import com.sorrowblue.comicviewer.framework.ui.preview.fakeFolder
import com.sorrowblue.comicviewer.framework.ui.preview.fakeInternalStorage
import com.sorrowblue.comicviewer.framework.ui.preview.flowData2
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

internal interface BookshelfScreenNavigator : BookshelfInfoSheetNavigator {
    fun onSettingsClick()
    fun onFabClick()
    fun onBookshelfClick(bookshelfId: BookshelfId, path: String)
    fun onEditClick(bookshelfId: BookshelfId)
}

@Destination<BookshelfGraph>(start = true, visibility = CodeGenVisibility.INTERNAL)
@Composable
internal fun BookshelfScreen(
    navigator: BookshelfScreenNavigator,
    removeDialogResultRecipient: ResultRecipient<BookshelfRemoveDialogDestination, Boolean>,
    notificationResultRecipient: ResultRecipient<NotificationRequestDialogDestination, NotificationRequestResult>,
    state: BookshelfScreenState = rememberBookshelfScreenState(),
) {
    BookshelfScreen(
        navigator = state.navigator,
        lazyPagingItems = state.pagingDataFlow.collectAsLazyPagingItems(),
        lazyGridState = state.lazyGridState,
        snackbarHostState = state.snackbarHostState,
        onFabClick = navigator::onFabClick,
        onSettingsClick = navigator::onSettingsClick,
        onBookshelfClick = navigator::onBookshelfClick,
        onBookshelfInfoClick = state::onBookshelfInfoClick,
    ) { content ->
        BookshelfInfoSheet(
            content = content,
            snackbarHostState = state.snackbarHostState,
            scaffoldNavigator = state.navigator,
            navigator = navigator,
            removeDialogResultRecipient = removeDialogResultRecipient,
            notificationResultRecipient = notificationResultRecipient,
        )
    }

    NavTabHandler(onClick = state::onNavClick)
}

@Composable
private fun BookshelfScreen(
    navigator: ThreePaneScaffoldNavigator<BookshelfFolder>,
    lazyPagingItems: LazyPagingItems<BookshelfFolder>,
    snackbarHostState: SnackbarHostState,
    onFabClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onBookshelfClick: (BookshelfId, String) -> Unit,
    onBookshelfInfoClick: (BookshelfFolder) -> Unit,
    modifier: Modifier = Modifier,
    lazyGridState: LazyGridState = rememberLazyGridState(),
    extraPane: @Composable (BookshelfFolder) -> Unit,
) {
    val expanded by remember(lazyGridState) {
        derivedStateOf { !lazyGridState.canScrollForward || !lazyGridState.canScrollBackward }
    }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    CanonicalScaffold(
        navigator = navigator,
        topBar = {
            BookshelfAppBar(onSettingsClick = onSettingsClick, scrollBehavior = scrollBehavior)
        },
        floatingActionButton = { BookshelfFab(expanded = expanded, onClick = onFabClick) },
        extraPane = extraPane,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { contentPadding ->
        BookshelfMainSheet(
            lazyPagingItems = lazyPagingItems,
            lazyGridState = lazyGridState,
            onBookshelfClick = onBookshelfClick,
            onBookshelfInfoClick = onBookshelfInfoClick,
            contentPadding = contentPadding
        )
    }
}

@PreviewMultiScreen
@Composable
private fun PreviewBookshelfScreen(
    @PreviewParameter(PagingDataProvider::class) pagingData: PagingData<BookshelfFolder>,
) {
    PreviewTheme3 {
        val lazyPagingItems = flowOf(pagingData).collectAsLazyPagingItems()
        val lazyGridState = rememberLazyGridState()
        BookshelfScreen(
            navigator = rememberSupportingPaneScaffoldNavigator<BookshelfFolder>(),
            lazyPagingItems = lazyPagingItems,
            snackbarHostState = remember { SnackbarHostState() },
            onFabClick = {},
            onSettingsClick = {},
            onBookshelfClick = { _, _ -> },
            onBookshelfInfoClick = {},
            lazyGridState = lazyGridState
        ) {}
        val coroutine = rememberCoroutineScope()
        LaunchedEffect(Unit) {
            coroutine.launch {
                lazyGridState.scrollToItem(lazyPagingItems.itemCount - 1)
            }
        }
    }
}

private class PagingDataProvider : PreviewParameterProvider<PagingData<BookshelfFolder>> {
    override val values: Sequence<PagingData<BookshelfFolder>> = sequenceOf(
        PagingData.flowData2(20) { BookshelfFolder(fakeInternalStorage(it), fakeFolder()) },
        PagingData.empty(),
        PagingData.empty(
            sourceLoadStates = LoadStates(
                LoadState.NotLoading(true),
                LoadState.NotLoading(true),
                LoadState.NotLoading(true)
            )
        )
    )
}
