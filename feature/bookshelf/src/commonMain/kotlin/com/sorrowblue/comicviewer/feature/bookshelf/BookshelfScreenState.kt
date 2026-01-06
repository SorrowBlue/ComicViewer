package com.sorrowblue.comicviewer.feature.bookshelf

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.paging.PagingConfig
import androidx.paging.compose.LazyPagingItems
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.PagingBookshelfFolderUseCase
import com.sorrowblue.comicviewer.framework.ui.adaptive.AdaptiveNavigationSuiteScaffoldState
import com.sorrowblue.comicviewer.framework.ui.adaptive.rememberAdaptiveNavigationSuiteScaffoldState
import com.sorrowblue.comicviewer.framework.ui.paging.rememberPagingItems
import kotlinx.coroutines.launch

interface BookshelfScreenState {
    val lazyPagingItems: LazyPagingItems<BookshelfFolder>
    val scaffoldState: AdaptiveNavigationSuiteScaffoldState
    val lazyGridState: LazyGridState
}

@Composable
context(context: BookshelfScreenContext)
internal fun rememberBookshelfScreenState(): BookshelfScreenState {
    val lazyGridState = rememberLazyGridState()
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberAdaptiveNavigationSuiteScaffoldState(onNavigationReSelect = {
        if (lazyGridState.canScrollBackward) {
            scope.launch {
                lazyGridState.animateScrollToItem(0)
            }
        }
    })
    val lazyPagingItems = rememberPagingItems {
        context.pagingBookshelfFolderUseCase(
            PagingBookshelfFolderUseCase.Request(
                PagingConfig(PageSize),
            ),
        )
    }
    return remember(lazyGridState, scaffoldState, lazyPagingItems) {
        BookshelfScreenStateImpl(
            lazyGridState = lazyGridState,
            scaffoldState = scaffoldState,
            lazyPagingItems = lazyPagingItems,
        )
    }
}

private class BookshelfScreenStateImpl(
    override val lazyGridState: LazyGridState,
    override val scaffoldState: AdaptiveNavigationSuiteScaffoldState,
    override val lazyPagingItems: LazyPagingItems<BookshelfFolder>,
) : BookshelfScreenState

private const val PageSize = 20
