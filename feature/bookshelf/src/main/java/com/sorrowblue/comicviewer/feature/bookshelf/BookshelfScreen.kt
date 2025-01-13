package com.sorrowblue.comicviewer.feature.bookshelf

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.parameters.CodeGenVisibility
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.feature.bookshelf.component.BookshelfFab
import com.sorrowblue.comicviewer.feature.bookshelf.info.BookshelfInfoSheet
import com.sorrowblue.comicviewer.feature.bookshelf.info.BookshelfInfoSheetNavigator
import com.sorrowblue.comicviewer.feature.bookshelf.navigation.BookshelfGraph
import com.sorrowblue.comicviewer.feature.bookshelf.section.BookshelfAppBar
import com.sorrowblue.comicviewer.feature.bookshelf.section.BookshelfMainSheet
import com.sorrowblue.comicviewer.framework.ui.NavTabHandler
import com.sorrowblue.comicviewer.framework.ui.adaptive.CanonicalScaffold
import com.sorrowblue.comicviewer.framework.ui.navigation.DefaultDestinationScopeWrapper
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewMultiScreen
import com.sorrowblue.comicviewer.framework.ui.preview.fake.fakeFolder
import com.sorrowblue.comicviewer.framework.ui.preview.fake.fakeInternalStorage
import com.sorrowblue.comicviewer.framework.ui.preview.fake.flowData
import com.sorrowblue.comicviewer.framework.ui.preview.fake.flowEmptyData
import com.sorrowblue.comicviewer.framework.ui.preview.fake.flowLoadingData
import com.sorrowblue.comicviewer.framework.ui.preview.layout.PreviewCompliantNavigation
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow

internal interface BookshelfScreenNavigator : BookshelfInfoSheetNavigator {
    fun onSettingsClick()
    fun onFabClick()
    fun onBookshelfClick(bookshelfId: BookshelfId, path: String)
}

@Destination<BookshelfGraph>(
    start = true,
    wrappers = [DefaultDestinationScopeWrapper::class],
    visibility = CodeGenVisibility.INTERNAL
)
@Composable
internal fun BookshelfScreen(
    navigator: BookshelfScreenNavigator,
    state: BookshelfScreenState = rememberBookshelfScreenState(),
) {
    BookshelfScreen(
        navigator = state.navigator,
        lazyPagingItems = state.pagingItems,
        lazyGridState = state.lazyGridState,
        snackbarHostState = state.snackbarHostState,
        onFabClick = navigator::onFabClick,
        onSettingsClick = navigator::onSettingsClick,
        onBookshelfClick = navigator::onBookshelfClick,
        onBookshelfInfoClick = state::onBookshelfInfoClick,
    ) { contentKey ->
        BookshelfInfoSheet(
            bookshelfId = contentKey,
            onCloseClick = state::onSheetCloseClick,
            navigator = navigator,
            snackbarHostState = state.snackbarHostState,
        )
    }

    NavTabHandler(onClick = state::onNavClick)
}

@Composable
private fun BookshelfScreen(
    navigator: ThreePaneScaffoldNavigator<BookshelfId>,
    lazyPagingItems: LazyPagingItems<BookshelfFolder>,
    snackbarHostState: SnackbarHostState,
    onFabClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onBookshelfClick: (BookshelfId, String) -> Unit,
    onBookshelfInfoClick: (BookshelfFolder) -> Unit,
    modifier: Modifier = Modifier,
    lazyGridState: LazyGridState = rememberLazyGridState(),
    extraPane: @Composable (BookshelfId) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val expanded by rememberLastScrolledForward(lazyGridState, 300)
    CanonicalScaffold(
        navigator = navigator,
        topBar = {
            BookshelfAppBar(
                onSettingsClick = onSettingsClick,
                scrollBehavior = scrollBehavior,
                scrollableState = lazyGridState
            )
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

@Composable
private fun rememberLastScrolledForward(lazyGridState: LazyGridState, delay: Long): State<Boolean> {
    val expanded = remember { mutableStateOf(true) }
    LaunchedEffect(lazyGridState.lastScrolledForward) {
        delay(delay)
        expanded.value = !lazyGridState.lastScrolledForward
    }
    return expanded
}

@PreviewMultiScreen
@Composable
private fun PreviewBookshelfScreen(
    @PreviewParameter(PagingDataProvider::class) pagingDataFlow: Flow<PagingData<BookshelfFolder>>,
) {
    PreviewCompliantNavigation {
        val lazyPagingItems = pagingDataFlow.collectAsLazyPagingItems()
        val lazyGridState = rememberLazyGridState()
        BookshelfScreen(
            navigator = rememberSupportingPaneScaffoldNavigator<BookshelfId>(),
            lazyPagingItems = lazyPagingItems,
            snackbarHostState = remember { SnackbarHostState() },
            onFabClick = {},
            onSettingsClick = {},
            onBookshelfClick = { _, _ -> },
            onBookshelfInfoClick = {},
            lazyGridState = lazyGridState
        ) { _ -> }
    }
}

private class PagingDataProvider : PreviewParameterProvider<Flow<PagingData<BookshelfFolder>>> {
    override val values: Sequence<Flow<PagingData<BookshelfFolder>>> = sequenceOf(
        PagingData.flowData { BookshelfFolder(fakeInternalStorage(it), fakeFolder()) },
        PagingData.flowLoadingData(),
        PagingData.flowEmptyData()
    )
}
