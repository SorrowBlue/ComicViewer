@file:OptIn(
    ExperimentalMaterial3AdaptiveApi::class, SavedStateHandleSaveableApi::class,
    ExperimentalMaterial3AdaptiveApi::class
)

package com.sorrowblue.comicviewer.bookshelf

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.SupportingPaneScaffoldRole
import androidx.compose.material3.adaptive.ThreePaneScaffoldNavigator
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.rememberSupportingPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.framework.ui.DialogController
import com.sorrowblue.comicviewer.framework.ui.SavableState
import com.sorrowblue.comicviewer.framework.ui.calculateStandardPaneScaffoldDirective
import com.sorrowblue.comicviewer.framework.ui.rememberSavableState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@ExperimentalMaterial3AdaptiveApi
@Stable
internal class BookshelfScreenState(
    private val viewModel: BookshelfViewModel,
    private val scope: CoroutineScope,
    val snackbarHostState: SnackbarHostState,
    val navigator: ThreePaneScaffoldNavigator<SupportingPaneScaffoldRole>,
    val removeDialogController: DialogController<BookshelfFolder?>,
    override val savedStateHandle: SavedStateHandle,
) : SavableState {

    val pagingDataFlow: Flow<PagingData<BookshelfFolder>> = viewModel.pagingDataFlow

    var bookshelfFolder: BookshelfFolder? by savedStateHandle.saveable(
        "bookshelfFolder",
        stateSaver = mapSaver(
            save = { mapOf("bookshelf" to it?.bookshelf, "folder" to it?.folder) },
            restore = {
                val bookshelf = it["bookshelf"] as? Bookshelf
                val folder = it["folder"] as? Folder
                if (bookshelf != null && folder != null) BookshelfFolder(
                    bookshelf,
                    folder
                ) else null
            }
        ),
    ) { mutableStateOf(null) }

    fun onBookshelfLongClick(bookshelfFolder: BookshelfFolder) {
        this.bookshelfFolder = bookshelfFolder
        scope.launch {
            navigator.navigateTo(SupportingPaneScaffoldRole.Extra)
        }
    }

    fun onRemoveClick() {
        removeDialogController.show(bookshelfFolder)
    }

    fun onInfoSheetCloseClick() {
        scope.launch {
            navigator.navigateBack()
        }
    }

    fun onInfoSheetScanClick() {
        viewModel.scan(bookshelfFolder!!.folder)
    }

    fun onDismissRequest() {
        removeDialogController.dismiss()
    }

    fun onConfirmClick() {
        viewModel.remove(bookshelfFolder!!.bookshelf)
        removeDialogController.dismiss()
        scope.launch {
            navigator.navigateBack()
            snackbarHostState.showSnackbar("削除しました。")
        }
    }

    val bookshelfId get() = bookshelfFolder!!.bookshelf.id
}

@Composable
internal fun rememberBookshelfScreenState(
    viewModel: BookshelfViewModel = hiltViewModel(),
    navigator: ThreePaneScaffoldNavigator<SupportingPaneScaffoldRole> = rememberSupportingPaneScaffoldNavigator(
        calculateStandardPaneScaffoldDirective(currentWindowAdaptiveInfo())
    ),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    scope: CoroutineScope = rememberCoroutineScope(),
    removeDialogController: DialogController<BookshelfFolder?> = remember { DialogController(null) },
): BookshelfScreenState {
    return rememberSavableState(restore = {
        BookshelfScreenState(
            viewModel = viewModel,
            scope = scope,
            navigator = navigator,
            removeDialogController = removeDialogController,
            snackbarHostState = snackbarHostState,
            savedStateHandle = it
        )
    }) {
        BookshelfScreenState(
            viewModel = viewModel,
            scope = scope,
            navigator = navigator,
            removeDialogController = removeDialogController,
            snackbarHostState = snackbarHostState,
            savedStateHandle = SavedStateHandle()
        )
    }
}
