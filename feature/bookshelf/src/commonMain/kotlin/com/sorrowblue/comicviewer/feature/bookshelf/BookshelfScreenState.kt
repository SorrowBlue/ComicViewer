package com.sorrowblue.comicviewer.feature.bookshelf

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.rememberCanonicalScaffoldNavigator
import com.sorrowblue.comicviewer.framework.ui.paging.LazyPagingItems
import com.sorrowblue.comicviewer.framework.ui.paging.collectAsLazyPagingItems
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

internal interface BookshelfScreenState {
    val pagingItems: LazyPagingItems<BookshelfFolder>
    val navigator: ThreePaneScaffoldNavigator<BookshelfId>
    val snackbarHostState: SnackbarHostState
    val lazyGridState: LazyGridState
    fun onBookshelfInfoClick(bookshelfFolder: BookshelfFolder)
    fun onNavClick()
    fun onSheetCloseClick()
}

@Composable
internal fun rememberBookshelfScreenState(
    viewModel: BookshelfViewModel = koinViewModel(),
    navigator: ThreePaneScaffoldNavigator<BookshelfId> = rememberCanonicalScaffoldNavigator(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    scope: CoroutineScope = rememberCoroutineScope(),
    lazyGridState: LazyGridState = rememberLazyGridState(),
): BookshelfScreenState {
    val pagingItems = viewModel.pagingDataFlow.collectAsLazyPagingItems()
    return remember {
        BookshelfScreenStateImpl(
            pagingItems = pagingItems,
            scope = scope,
            snackbarHostState = snackbarHostState,
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

    override fun onSheetCloseClick() {
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
