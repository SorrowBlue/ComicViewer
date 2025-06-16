package com.sorrowblue.comicviewer.feature.bookshelf

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldRole
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.framework.ui.NavigationSuiteScaffold2State
import com.sorrowblue.comicviewer.framework.ui.paging.LazyPagingItems
import com.sorrowblue.comicviewer.framework.ui.paging.collectAsLazyPagingItems
import com.sorrowblue.comicviewer.framework.ui.rememberCanonicalScaffoldLayoutState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

internal interface BookshelfScreenState {
    val pagingItems: LazyPagingItems<BookshelfFolder>
    val scaffoldState: NavigationSuiteScaffold2State<BookshelfId>
    val lazyGridState: LazyGridState
    fun onBookshelfInfoClick(bookshelfFolder: BookshelfFolder)
    fun onNavClick()
    fun onSheetCloseClick()
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun rememberBookshelfScreenState(
    viewModel: BookshelfViewModel = koinViewModel(),
    scaffoldState: NavigationSuiteScaffold2State<BookshelfId> = rememberCanonicalScaffoldLayoutState<BookshelfId>(),
    scope: CoroutineScope = rememberCoroutineScope(),
    lazyGridState: LazyGridState = rememberLazyGridState(),
): BookshelfScreenState {
    val pagingItems = viewModel.pagingDataFlow.collectAsLazyPagingItems()
    return remember {
        BookshelfScreenStateImpl(
            pagingItems = pagingItems,
            scope = scope,
            scaffoldState = scaffoldState,
            lazyGridState = lazyGridState,
        )
    }
}

private class BookshelfScreenStateImpl(
    override val pagingItems: LazyPagingItems<BookshelfFolder>,
    override val scaffoldState: NavigationSuiteScaffold2State<BookshelfId>,
    override val lazyGridState: LazyGridState,
    private val scope: CoroutineScope,
) : BookshelfScreenState {

    override fun onBookshelfInfoClick(bookshelfFolder: BookshelfFolder) {
        scope.launch {
            scaffoldState.navigator.navigateTo(
                SupportingPaneScaffoldRole.Extra,
                bookshelfFolder.bookshelf.id
            )
        }
    }

    override fun onSheetCloseClick() {
        scope.launch {
            scaffoldState.navigator.navigateBack()
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
