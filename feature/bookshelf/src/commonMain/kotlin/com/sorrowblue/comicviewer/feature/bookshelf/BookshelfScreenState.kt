package com.sorrowblue.comicviewer.feature.bookshelf

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.paging.PagingConfig
import androidx.paging.compose.LazyPagingItems
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfType
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.PagingBookshelfFolderUseCase
import com.sorrowblue.comicviewer.feature.bookshelf.edit.BookshelfEditDialogState
import com.sorrowblue.comicviewer.framework.ui.adaptive.AdaptiveNavigationSuiteScaffoldState
import com.sorrowblue.comicviewer.framework.ui.adaptive.rememberAdaptiveNavigationSuiteScaffoldState
import com.sorrowblue.comicviewer.framework.ui.paging.rememberPagingItems
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

interface BookshelfScreenState {
    val bookshelfEditDialogState: BookshelfEditDialogState
    val lazyPagingItems: LazyPagingItems<BookshelfFolder>
    val scaffoldState: AdaptiveNavigationSuiteScaffoldState
    val lazyGridState: LazyGridState

    fun onNavItemReSelected()

    fun onFabClick()

    fun onEditClick(id: BookshelfId, type: BookshelfType)
}

@Composable
context(context: BookshelfScreenContext)
internal fun rememberBookshelfScreenState(): BookshelfScreenState {
    val stateImpl = remember {
        BookshelfScreenStateImpl()
    }.apply {
        scope = rememberCoroutineScope()
        lazyGridState = rememberLazyGridState()
        lazyPagingItems = rememberPagingItems {
            context.pagingBookshelfFolderUseCase(
                PagingBookshelfFolderUseCase.Request(
                    PagingConfig(PageSize),
                ),
            )
        }
        scaffoldState = rememberAdaptiveNavigationSuiteScaffoldState()
    }
    return stateImpl
}

private class BookshelfScreenStateImpl : BookshelfScreenState {
    override lateinit var lazyGridState: LazyGridState
    lateinit var scope: CoroutineScope
    override lateinit var scaffoldState: AdaptiveNavigationSuiteScaffoldState
    override lateinit var bookshelfEditDialogState: BookshelfEditDialogState
    override lateinit var lazyPagingItems: LazyPagingItems<BookshelfFolder>

    override fun onNavItemReSelected() {
        if (lazyGridState.canScrollBackward) {
            scope.launch {
                lazyGridState.animateScrollToItem(0)
            }
        }
    }

    override fun onFabClick() {
        bookshelfEditDialogState.showSelectionDialog()
    }

    override fun onEditClick(id: BookshelfId, type: BookshelfType) {
        bookshelfEditDialogState.showEditorDialog(id, type)
    }
}

private const val PageSize = 20
