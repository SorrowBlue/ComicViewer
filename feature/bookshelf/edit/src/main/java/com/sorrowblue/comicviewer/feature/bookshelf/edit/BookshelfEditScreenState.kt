package com.sorrowblue.comicviewer.feature.bookshelf.edit

import android.content.Context
import android.os.Parcelable
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfType
import com.sorrowblue.comicviewer.domain.model.bookshelf.InternalStorage
import com.sorrowblue.comicviewer.domain.model.bookshelf.SmbServer
import com.sorrowblue.comicviewer.framework.ui.material3.Input
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

sealed interface BookshelfEditScreenUiState : Parcelable

@Parcelize
data object UnitUiState : BookshelfEditScreenUiState

sealed class BookshelfEditInnerScreenState<T : BookshelfEditScreenUiState> {
    abstract var uiState: T
        protected set
}

data object BookshelfEditLoading : BookshelfEditInnerScreenState<UnitUiState>() {
    override var uiState: UnitUiState = UnitUiState
}

internal class BookshelfEditScreenState(
    restoredUiState: BookshelfEditScreenUiState?,
    val snackbarHostState: SnackbarHostState,
    private val args: BookshelfEditArgs,
    private val context: Context,
    private val scope: CoroutineScope,
    private val viewModel: BookshelfEditViewModel,
) {

    var innerScreenState: BookshelfEditInnerScreenState<*> by mutableStateOf(BookshelfEditLoading)
        private set

    init {
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
                        viewModel,
                        context,
                        scope,
                    )
                }

                UnitUiState -> {}
            }
            // Restore
        } else {
            if (args.bookshelfId == BookshelfId.Default) {
                innerScreenState = when (args.bookshelfType) {
                    BookshelfType.SMB -> SmbEditScreenState(
                        SmbEditScreenUiState(),
                        snackbarHostState,
                        args,
                        viewModel,
                        context,
                        scope,
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
                // 新規作成
            } else {
                scope.launch {
                    delay(1000)
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
                                    viewModel = viewModel,
                                    context = context,
                                    scope = scope,
                                )
                            }
                        }
                    } ?: kotlin.run {
                        snackbarHostState.showSnackbar(context.getString(R.string.bookshelf_edit_msg_could_not_load_bookshelf))
                    }
                }
            }
        }
    }
}

@Composable
internal fun rememberNeoBookshelfEditScreenState(
    args: BookshelfEditArgs,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    context: Context = LocalContext.current,
    scope: CoroutineScope = rememberCoroutineScope(),
    viewModel: BookshelfEditViewModel = hiltViewModel(),
) = rememberSaveable(
    saver = Saver(
        save = {
            it.innerScreenState.uiState
        },
        restore = {
            BookshelfEditScreenState(
                restoredUiState = it,
                args = args,
                context = context,
                scope = scope,
                snackbarHostState = snackbarHostState,
                viewModel = viewModel
            )
        }
    )
) {
    BookshelfEditScreenState(
        args = args,
        restoredUiState = null,
        context = context,
        scope = scope,
        snackbarHostState = snackbarHostState,
        viewModel = viewModel
    )
}
