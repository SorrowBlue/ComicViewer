package com.sorrowblue.comicviewer.bookshelf

import android.content.Context
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.SupportingPaneScaffoldRole
import androidx.compose.material3.adaptive.ThreePaneScaffoldNavigator
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.rememberSupportingPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asFlow
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.dataOrNull
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.GetBookshelfInfoUseCase
import com.sorrowblue.comicviewer.feature.bookshelf.R
import com.sorrowblue.comicviewer.framework.ui.DialogController
import com.sorrowblue.comicviewer.framework.ui.SaveableScreenState
import com.sorrowblue.comicviewer.framework.ui.calculateStandardPaneScaffoldDirective
import com.sorrowblue.comicviewer.framework.ui.rememberSaveableScreenState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
internal interface BookshelfScreenState : SaveableScreenState {
    val navigator: ThreePaneScaffoldNavigator
    val snackbarHostState: SnackbarHostState
    val removeDialogController: DialogController<BookshelfFolder?>
    val lazyGridState: LazyGridState
    val pagingDataFlow: Flow<PagingData<BookshelfFolder>>
    var bookshelfFolder: Flow<BookshelfFolder?>
    fun onBookshelfInfoClick(bookshelfFolder: BookshelfFolder)
    fun onRemoveClick()
    fun onInfoSheetCloseClick()
    fun onInfoSheetScanClick()
    fun onDismissRequest()
    fun onConfirmClick()
    fun onNavClick()
    val bookshelfId: BookshelfId
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
internal fun rememberBookshelfScreenState(
    navigator: ThreePaneScaffoldNavigator = rememberSupportingPaneScaffoldNavigator(
        calculateStandardPaneScaffoldDirective(currentWindowAdaptiveInfo())
    ),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    scope: CoroutineScope = rememberCoroutineScope(),
    viewModel: BookshelfViewModel = hiltViewModel(),
    removeDialogController: DialogController<BookshelfFolder?> = remember { DialogController(null) },
    lazyGridState: LazyGridState = rememberLazyGridState(),
    context: Context = LocalContext.current,
): BookshelfScreenState = rememberSaveableScreenState {
    BookshelfScreenStateImpl(
        savedStateHandle = it,
        viewModel = viewModel,
        scope = scope,
        snackbarHostState = snackbarHostState,
        navigator = navigator,
        removeDialogController = removeDialogController,
        lazyGridState = lazyGridState,
        context = context
    )
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Stable
private class BookshelfScreenStateImpl(
    override val savedStateHandle: SavedStateHandle,
    private val viewModel: BookshelfViewModel,
    private val scope: CoroutineScope,
    override val navigator: ThreePaneScaffoldNavigator,
    override val snackbarHostState: SnackbarHostState,
    override val removeDialogController: DialogController<BookshelfFolder?>,
    override val lazyGridState: LazyGridState,
    private val context: Context,
) : BookshelfScreenState {

    override val bookshelfId get() = bookshelfId2.value!!

    override val pagingDataFlow: Flow<PagingData<BookshelfFolder>> = viewModel.pagingDataFlow

    private var bookshelfId2 = savedStateHandle.getLiveData<BookshelfId?>("bookshelfId")

    @OptIn(ExperimentalCoroutinesApi::class)
    override var bookshelfFolder: Flow<BookshelfFolder?> = bookshelfId2.asFlow().flatMapLatest {
        if (it != null) {
            viewModel.getBookshelfInfoUseCase.execute(GetBookshelfInfoUseCase.Request(bookshelfId = it))
                .map { it.dataOrNull() }
        } else {
            flowOf(null)
        }
    }


    override fun onBookshelfInfoClick(bookshelfFolder: BookshelfFolder) {
        bookshelfId2.value = bookshelfFolder.bookshelf.id
        navigator.navigateTo(SupportingPaneScaffoldRole.Extra)
    }

    override fun onRemoveClick() {
        scope.launch {
            removeDialogController.show(bookshelfFolder.first())
        }
    }

    override fun onInfoSheetCloseClick() {
        scope.launch {
            navigator.navigateBack()
        }
    }

    override fun onInfoSheetScanClick() {
        scope.launch {
            viewModel.scan(bookshelfFolder.first()!!.folder)
        }
    }

    override fun onDismissRequest() {
        removeDialogController.dismiss()
    }

    override fun onConfirmClick() {
        scope.launch {
            val bookshelf = bookshelfFolder.first()!!.bookshelf
            viewModel.remove(bookshelf)
            removeDialogController.dismiss()
            navigator.navigateBack()
            snackbarHostState.showSnackbar(
                context.getString(R.string.bookshelf_msg_delete, bookshelf.displayName)
            )
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
