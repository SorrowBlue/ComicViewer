package com.sorrowblue.comicviewer.feature.bookshelf.edit

import android.content.Context
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfType
import com.sorrowblue.comicviewer.domain.model.bookshelf.InternalStorage
import com.sorrowblue.comicviewer.domain.model.bookshelf.ShareContents
import com.sorrowblue.comicviewer.domain.model.bookshelf.SmbServer
import com.sorrowblue.comicviewer.framework.ui.SaveableScreenState
import com.sorrowblue.comicviewer.framework.ui.material3.Input
import com.sorrowblue.comicviewer.framework.ui.rememberSaveableScreenState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal interface BookshelfEditScreenState : SaveableScreenState {
    val innerScreenState: BookshelfEditInnerScreenState<*>
}

@Composable
internal fun rememberNeoBookshelfEditScreenState(
    args: BookshelfEditArgs,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    context: Context = LocalContext.current,
    scope: CoroutineScope = rememberCoroutineScope(),
    viewModel: BookshelfEditViewModel = hiltViewModel(),
    softwareKeyboardController: SoftwareKeyboardController? = LocalSoftwareKeyboardController.current,
): BookshelfEditScreenState = rememberSaveableScreenState {
    BookshelfEditScreenStateImpl(
        args = args,
        context = context,
        scope = scope,
        snackbarHostState = snackbarHostState,
        viewModel = viewModel,
        savedStateHandle = it,
        softwareKeyboardController = softwareKeyboardController,
    )
}

private class BookshelfEditScreenStateImpl(
    val snackbarHostState: SnackbarHostState,
    override val savedStateHandle: SavedStateHandle,
    private val args: BookshelfEditArgs,
    private val context: Context,
    private val scope: CoroutineScope,
    private val viewModel: BookshelfEditViewModel,
    private val softwareKeyboardController: SoftwareKeyboardController?,
) : BookshelfEditScreenState {

    override var innerScreenState: BookshelfEditInnerScreenState<*> by mutableStateOf(
        BookshelfEditLoading
    )
        private set

    init {
        val restoredUiState = savedStateHandle.get<BookshelfEditScreenUiState>("restoredUiState")
        if (restoredUiState != null) {
            when (restoredUiState) {
                is StorageEditScreenUiState -> {
                    innerScreenState = StorageEditScreenState(
                        restoredUiState,
                        null,
                        snackbarHostState,
                        args,
                        context,
                        scope,
                        viewModel
                    )
                }

                is SmbEditScreenUiState -> {
                    innerScreenState = SmbEditScreenState(
                        restoredUiState,
                        snackbarHostState,
                        args,
                        context,
                        scope,
                        softwareKeyboardController = softwareKeyboardController,
                        viewModel.registerBookshelfUseCase,
                    )
                }

                UnitUiState -> {}
            }
        } else {
            if (args.bookshelfId == BookshelfId.Default) {
                innerScreenState = when (args.bookshelfType) {
                    BookshelfType.SMB -> SmbEditScreenState(
                        SmbEditScreenUiState(),
                        snackbarHostState,
                        args,
                        context,
                        scope,
                        softwareKeyboardController = softwareKeyboardController,
                        viewModel.registerBookshelfUseCase,
                    )

                    BookshelfType.DEVICE -> StorageEditScreenState(
                        StorageEditScreenUiState(),
                        null,
                        snackbarHostState,
                        args,
                        context,
                        scope,
                        viewModel
                    )
                }
            } else {
                scope.launch {
                    viewModel.fetch(args.bookshelfId)?.let {
                        innerScreenState = when (val bookshelf = it.bookshelf) {
                            is InternalStorage -> {
                                val uiState = StorageEditScreenUiState(
                                    editType = EditType.Edit,
                                    displayName = Input(bookshelf.displayName),
                                    dir = Input(
                                        it.folder.path.toUri().lastPathSegment?.split(":")
                                            ?.lastOrNull()
                                            .orEmpty()
                                    ),
                                    isError = false,
                                    isProgress = false
                                )
                                StorageEditScreenState(
                                    uiState,
                                    it.folder.path.toUri(),
                                    snackbarHostState,
                                    args,
                                    context,
                                    scope,
                                    viewModel
                                )
                            }

                            is SmbServer -> {
                                val uiState = SmbEditScreenUiState(bookshelf, it.folder)
                                SmbEditScreenState(
                                    uiState = uiState,
                                    snackbarHostState = snackbarHostState,
                                    args = args,
                                    context = context,
                                    scope = scope,
                                    softwareKeyboardController = softwareKeyboardController,
                                    viewModel.registerBookshelfUseCase,
                                )
                            }

                            ShareContents -> BookshelfEditLoading
                        }
                    } ?: kotlin.run {
                        snackbarHostState.showSnackbar(context.getString(R.string.bookshelf_edit_msg_could_not_load_bookshelf))
                    }
                }
            }
        }
    }
}
