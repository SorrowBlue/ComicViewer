package com.sorrowblue.comicviewer.bookshelf

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.dataOrNull
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.GetBookshelfInfoUseCase
import com.sorrowblue.comicviewer.framework.ui.DialogController
import com.sorrowblue.comicviewer.framework.ui.SaveableScreenState
import com.sorrowblue.comicviewer.framework.ui.rememberSaveableScreenState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
internal interface BookshelfScreenState : SaveableScreenState {
    val navigator: ThreePaneScaffoldNavigator<BookshelfFolder>
    val snackbarHostState: SnackbarHostState
    val removeDialogController: DialogController<BookshelfFolder?>
    val lazyGridState: LazyGridState
    val pagingDataFlow: Flow<PagingData<BookshelfFolder>>
    fun onBookshelfInfoClick(bookshelfFolder: BookshelfFolder)
    fun onNavClick()
    val bookshelfId: BookshelfId
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
internal fun rememberBookshelfScreenState(
    navigator: ThreePaneScaffoldNavigator<BookshelfFolder> = rememberSupportingPaneScaffoldNavigator<BookshelfFolder>(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    scope: CoroutineScope = rememberCoroutineScope(),
    viewModel: BookshelfViewModel = hiltViewModel(),
    removeDialogController: DialogController<BookshelfFolder?> = remember { DialogController(null) },
    lazyGridState: LazyGridState = rememberLazyGridState(),
): BookshelfScreenState = rememberSaveableScreenState {
    BookshelfScreenStateImpl(
        savedStateHandle = it,
        viewModel = viewModel,
        scope = scope,
        snackbarHostState = snackbarHostState,
        navigator = navigator,
        removeDialogController = removeDialogController,
        lazyGridState = lazyGridState,
    )
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Stable
private class BookshelfScreenStateImpl(
    override val savedStateHandle: SavedStateHandle,
    private val viewModel: BookshelfViewModel,
    private val scope: CoroutineScope,
    override val navigator: ThreePaneScaffoldNavigator<BookshelfFolder>,
    override val snackbarHostState: SnackbarHostState,
    override val removeDialogController: DialogController<BookshelfFolder?>,
    override val lazyGridState: LazyGridState,
) : BookshelfScreenState {

    override val bookshelfId get() = bookshelfIdLiveData.value!!

    override val pagingDataFlow: Flow<PagingData<BookshelfFolder>> = viewModel.pagingDataFlow

    private val bookshelfIdLiveData =
        MutableStateFlow(navigator.currentDestination?.content?.bookshelf?.id)

    init {
        bookshelfIdLiveData.onEach { id ->
            if (id != null) {
                viewModel.getBookshelfInfoUseCase.execute(
                    GetBookshelfInfoUseCase.Request(bookshelfId = id)
                ).collectLatest { resource ->
                    resource.dataOrNull()?.let {
                        navigator.navigateTo(SupportingPaneScaffoldRole.Extra, it)
                    } ?: run {
                        navigator.navigateTo(SupportingPaneScaffoldRole.Extra, null)
                    }
                }
            } else {
                navigator.navigateBack()
            }
        }.launchIn(scope)
    }

    override fun onBookshelfInfoClick(bookshelfFolder: BookshelfFolder) {
        bookshelfIdLiveData.value = bookshelfFolder.bookshelf.id
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
