package com.sorrowblue.comicviewer.bookshelf

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.framework.ui.adaptive.rememberCanonicalScaffoldNavigator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal interface BookshelfScreenState {

    val pagingItems: LazyPagingItems<BookshelfFolder>
    val navigator: ThreePaneScaffoldNavigator<BookshelfId>
    val snackbarHostState: SnackbarHostState
    val lazyGridState: LazyGridState

    fun onBookshelfInfoClick(bookshelfFolder: BookshelfFolder)
    fun onNavClick()
    fun back()
}

@Composable
internal fun rememberBookshelfScreenState(
    navigator: ThreePaneScaffoldNavigator<BookshelfId> = rememberCanonicalScaffoldNavigator(),
    scope: CoroutineScope = rememberCoroutineScope(),
    viewModel: BookshelfViewModel = hiltViewModel(),
    lazyGridState: LazyGridState = rememberLazyGridState(),
): BookshelfScreenState {
    val pagingItems = viewModel.pagingDataFlow.collectAsLazyPagingItems()
    return remember {
        BookshelfScreenStateImpl(
            pagingItems = pagingItems,
            scope = scope,
            snackbarHostState = SnackbarHostState(),
            navigator = navigator,
            lazyGridState = lazyGridState,
        )
    }
}

private class BookshelfScreenStateImpl(
    override val pagingItems: LazyPagingItems<BookshelfFolder>,
    override val navigator: ThreePaneScaffoldNavigator<BookshelfId>,
    override val snackbarHostState: SnackbarHostState,
    override val lazyGridState: LazyGridState,
    private val scope: CoroutineScope,
) : BookshelfScreenState {

    override fun onBookshelfInfoClick(bookshelfFolder: BookshelfFolder) {
        scope.launch {
            navigator.navigateTo(SupportingPaneScaffoldRole.Extra, bookshelfFolder.bookshelf.id)
        }
    }

    override fun back() {
        scope.launch {
            navigator.navigateBack()
        }
    }

    override fun onNavClick() {
        if (lazyGridState.canScrollBackward) {
            scope.launch {
                lazyGridState.scrollToItem(0)
            }
        }
    }
}
