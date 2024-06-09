package com.sorrowblue.comicviewer.bookshelf

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.framework.ui.SaveableScreenState
import com.sorrowblue.comicviewer.framework.ui.rememberSaveableScreenState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
internal interface BookshelfScreenState : SaveableScreenState {

    val navigator: ThreePaneScaffoldNavigator<BookshelfFolder>
    val snackbarHostState: SnackbarHostState
    val lazyGridState: LazyGridState
    val pagingDataFlow: Flow<PagingData<BookshelfFolder>>

    fun onBookshelfInfoClick(bookshelfFolder: BookshelfFolder)
    fun onNavClick()
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
internal fun rememberBookshelfScreenState(
    navigator: ThreePaneScaffoldNavigator<BookshelfFolder> = rememberSupportingPaneScaffoldNavigator<BookshelfFolder>(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    scope: CoroutineScope = rememberCoroutineScope(),
    viewModel: BookshelfViewModel = hiltViewModel(),
    lazyGridState: LazyGridState = rememberLazyGridState(),
): BookshelfScreenState = rememberSaveableScreenState {
    BookshelfScreenStateImpl(
        savedStateHandle = it,
        viewModel = viewModel,
        scope = scope,
        snackbarHostState = snackbarHostState,
        navigator = navigator,
        lazyGridState = lazyGridState,
    )
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
private class BookshelfScreenStateImpl(
    viewModel: BookshelfViewModel,
    override val savedStateHandle: SavedStateHandle,
    override val navigator: ThreePaneScaffoldNavigator<BookshelfFolder>,
    override val snackbarHostState: SnackbarHostState,
    override val lazyGridState: LazyGridState,
    private val scope: CoroutineScope,
) : BookshelfScreenState {

    override val pagingDataFlow = viewModel.pagingDataFlow

    override fun onBookshelfInfoClick(bookshelfFolder: BookshelfFolder) {
        navigator.navigateTo(SupportingPaneScaffoldRole.Extra, bookshelfFolder)
    }

    override fun onNavClick() {
        if (lazyGridState.canScrollBackward) {
            scope.launch {
                lazyGridState.scrollToItem(0)
            }
        }
    }
}
