package com.sorrowblue.comicviewer.feature.bookshelf

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.feature.bookshelf.component.BookshelfAppBar
import com.sorrowblue.comicviewer.feature.bookshelf.component.BookshelfFab
import com.sorrowblue.comicviewer.feature.bookshelf.info.BookshelfInfoSheet
import com.sorrowblue.comicviewer.feature.bookshelf.info.BookshelfInfoSheetNavigator
import com.sorrowblue.comicviewer.feature.bookshelf.info.delete.BookshelfDelete
import com.sorrowblue.comicviewer.feature.bookshelf.info.notification.NotificationRequest
import com.sorrowblue.comicviewer.feature.bookshelf.info.notification.NotificationRequestResult
import com.sorrowblue.comicviewer.feature.bookshelf.section.BookshelfSheet
import com.sorrowblue.cmpdestinations.annotation.Destination
import com.sorrowblue.cmpdestinations.result.NavResultReceiver
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.CanonicalScaffold
import com.sorrowblue.comicviewer.framework.ui.navigation.NavTabHandler
import com.sorrowblue.comicviewer.framework.ui.paging.LazyPagingItems
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable
import org.koin.compose.koinInject

@Serializable
data object Bookshelf

interface BookshelfScreenNavigator : BookshelfInfoSheetNavigator {
    fun onSettingsClick()
    fun onFabClick()
    fun onBookshelfClick(bookshelfId: BookshelfId, path: String)
}

@Destination<Bookshelf>()
@Composable
internal fun BookshelfScreen(
    deleteNavResultReceiver: NavResultReceiver<BookshelfDelete, Boolean>,
    notificationNavResultReceiver: NavResultReceiver<NotificationRequest, NotificationRequestResult>,
    navigator: BookshelfScreenNavigator = koinInject(),
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
            deleteNavResultReceiver = deleteNavResultReceiver,
            notificationNavResultReceiver = notificationNavResultReceiver,
        )
    }

    NavTabHandler(onClick = state::onNavClick)
}

@Composable
internal fun BookshelfScreen(
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
        BookshelfSheet(
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
