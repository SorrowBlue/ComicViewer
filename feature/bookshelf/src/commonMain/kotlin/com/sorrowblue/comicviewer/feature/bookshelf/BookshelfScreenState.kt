package com.sorrowblue.comicviewer.feature.bookshelf

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldRole
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.ViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfType
import com.sorrowblue.comicviewer.feature.bookshelf.edit.BookshelfEditDialogState
import com.sorrowblue.comicviewer.framework.ui.CanonicalScaffoldState
import com.sorrowblue.comicviewer.framework.ui.rememberCanonicalScaffoldState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import logcat.logcat
import org.koin.android.annotation.KoinViewModel
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

internal interface BookshelfScreenState {
    val bookshelfEditDialogState: BookshelfEditDialogState
    val pagingItems: LazyPagingItems<BookshelfFolder>
    val scaffoldState: CanonicalScaffoldState<BookshelfId>
    val lazyGridState: LazyGridState
    fun onBookshelfInfoClick(bookshelfFolder: BookshelfFolder)
    fun onNavItemReSelected()
    fun onSheetCloseClick()
    fun onFabClick()
    fun onEditClick(id: BookshelfId, type: BookshelfType)
}

@KoinViewModel
class CommonViewModel : ViewModel()

@Composable
internal fun rememberBookshelfScreenState(
    viewModel: BookshelfViewModel = koinViewModel(),
    scope: CoroutineScope = rememberCoroutineScope(),
    lazyGridState: LazyGridState = rememberLazyGridState(),
): BookshelfScreenState {
    val pagingItems = viewModel.pagingDataFlow.collectAsLazyPagingItems()
    logcat("pagingItems") { "pagingItems $pagingItems" }
    val stateImpl = remember(scope, lazyGridState) {
        BookshelfScreenStateImpl(
            scope = scope,
            lazyGridState = lazyGridState,
        )
    }
    stateImpl.bookshelfEditDialogState = koinInject()
    stateImpl.pagingItems = pagingItems
    stateImpl.scaffoldState =
        rememberCanonicalScaffoldState(onReSelect = stateImpl::onNavItemReSelected)
    return stateImpl
}

private class BookshelfScreenStateImpl(
    override val lazyGridState: LazyGridState,
    private val scope: CoroutineScope,
) : BookshelfScreenState {
    override lateinit var bookshelfEditDialogState: BookshelfEditDialogState
    override lateinit var pagingItems: LazyPagingItems<BookshelfFolder>
    override lateinit var scaffoldState: CanonicalScaffoldState<BookshelfId>

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
