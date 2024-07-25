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
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultRecipient
import com.sorrowblue.comicviewer.bookshelf.component.BookshelfFab
import com.sorrowblue.comicviewer.bookshelf.info.BookshelfInfoSheet
import com.sorrowblue.comicviewer.bookshelf.info.NotificationRequestResult
import com.sorrowblue.comicviewer.bookshelf.navigation.BookshelfGraph
import com.sorrowblue.comicviewer.bookshelf.navigation.BookshelfGraphTransitions
import com.sorrowblue.comicviewer.bookshelf.section.BookshelfAppBar
import com.sorrowblue.comicviewer.bookshelf.section.BookshelfMainSheet
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.bookshelf.InternalStorage
import com.sorrowblue.comicviewer.feature.bookshelf.destinations.BookshelfRemoveDialogDestination
import com.sorrowblue.comicviewer.feature.bookshelf.destinations.NotificationRequestDialogDestination
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.preview.PreviewTheme
import com.sorrowblue.comicviewer.framework.preview.fakeFolder
import com.sorrowblue.comicviewer.framework.ui.CanonicalScaffold
import com.sorrowblue.comicviewer.framework.ui.NavTabHandler
import com.sorrowblue.comicviewer.framework.ui.add
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
internal fun BookshelfScreen(
    navigator: BookshelfScreenNavigator,
    removeDialogResultRecipient: ResultRecipient<BookshelfRemoveDialogDestination, Boolean>,
    notificationResultRecipient: ResultRecipient<NotificationRequestDialogDestination, NotificationRequestResult>,
    destinationsNavigator: DestinationsNavigator,
) {
    BookshelfScreen(
        removeDialogResultRecipient = removeDialogResultRecipient,
        notificationResultRecipient = notificationResultRecipient,
        destinationsNavigator = destinationsNavigator,
        onSettingsClick = navigator::onSettingsClick,
        onFabClick = navigator::onFabClick,
        onBookshelfClick = navigator::onBookshelfClick,
        onEditClick = navigator::onEditClick
    )
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
private fun BookshelfScreen(
    removeDialogResultRecipient: ResultRecipient<BookshelfRemoveDialogDestination, Boolean>,
    notificationResultRecipient: ResultRecipient<NotificationRequestDialogDestination, NotificationRequestResult>,
    destinationsNavigator: DestinationsNavigator,
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
    ) { contentPadding ->
        state.navigator.currentDestination?.content?.let {
            BookshelfInfoSheet(
                removeDialogResultRecipient = removeDialogResultRecipient,
                notificationResultRecipient = notificationResultRecipient,
                destinationsNavigator = destinationsNavigator,
                navigator = state.navigator,
                contentPadding = contentPadding,
                snackbarHostState = state.snackbarHostState,
                onEditClick = onEditClick,
            )
        }
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
    modifier: Modifier = Modifier,
    lazyGridState: LazyGridState = rememberLazyGridState(),
    extraPane: @Composable (PaddingValues) -> Unit,
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
            extraPane(innerPadding)
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { contentPadding ->
        val dimension = ComicTheme.dimension
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

internal val FabSpace get() = 72.dp

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
        ) {
        }
    }
}
