package com.sorrowblue.comicviewer.feature.bookshelf.edit

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfType
import com.sorrowblue.comicviewer.domain.model.bookshelf.InternalStorage
import com.sorrowblue.comicviewer.domain.model.bookshelf.ShareContents
import com.sorrowblue.comicviewer.domain.model.bookshelf.SmbServer
import com.sorrowblue.comicviewer.domain.model.fold
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.GetBookshelfInfoUseCase
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.RegisterBookshelfUseCase
import com.sorrowblue.comicviewer.framework.ui.EventFlow
import comicviewer.feature.bookshelf.edit.generated.resources.Res
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_error_bad_auth
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_error_bad_host
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_error_bad_network
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_error_bad_path
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import logcat.logcat
import org.jetbrains.compose.resources.getString
import org.koin.compose.viewmodel.koinViewModel

sealed interface BookshelfEditScreenEvent {
    data object Complete : BookshelfEditScreenEvent
}

internal interface BookshelfEditScreenState {
    val uiState: BookshelfEditScreenUiState
    val snackbarHostState: SnackbarHostState
    val events: EventFlow<BookshelfEditScreenEvent>
    fun onSubmit(form: BookshelfEditForm)
}

@Composable
internal fun rememberBookshelfEditScreenState(
    editMode: BookshelfEditMode,
    scope: CoroutineScope = rememberCoroutineScope(),
    viewModel: BookshelfEditScreenViewModel = koinViewModel(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
): BookshelfEditScreenState {
    return remember {
        BookshelfEditScreenStateImpl(
            editMode = editMode,
            getBookshelfInfoUseCase = viewModel.getBookshelfInfoUseCase,
            registerBookshelfUseCase = viewModel.registerBookshelfUseCase,
            scope = scope,
            snackbarHostState = snackbarHostState,
        )
    }
}

private class BookshelfEditScreenStateImpl(
    private val editMode: BookshelfEditMode,
    private val getBookshelfInfoUseCase: GetBookshelfInfoUseCase,
    private val registerBookshelfUseCase: RegisterBookshelfUseCase,
    private val scope: CoroutineScope,
    override val snackbarHostState: SnackbarHostState,
) : BookshelfEditScreenState {

    override var uiState: BookshelfEditScreenUiState by mutableStateOf(
        BookshelfEditScreenUiState.Loading(editMode)
    )

    override val events: EventFlow<BookshelfEditScreenEvent> = EventFlow()

    init {
        when (editMode) {
            is BookshelfEditMode.Edit -> fetch(editMode.bookshelfId)
            is BookshelfEditMode.Register ->
                uiState = when (editMode.bookshelfType) {
                    BookshelfType.DEVICE ->
                        InternalStorageEditScreenUiState(
                            form = InternalStorageEditScreenForm(),
                            editMode = editMode
                        )

                    BookshelfType.SMB ->
                        SmbEditScreenUiState(
                            form = SmbEditScreenForm(),
                            editMode = editMode
                        )
                }
        }
    }

    fun fetch(bookshelfId: BookshelfId) {
        scope.launch {
            delay(250)
            getBookshelfInfoUseCase(GetBookshelfInfoUseCase.Request(bookshelfId)).first().fold(
                onSuccess = {
                    when (val bookshelf = it.bookshelf) {
                        is InternalStorage -> {
                            uiState = InternalStorageEditScreenUiState(
                                form = InternalStorageEditScreenForm(
                                    displayName = bookshelf.displayName,
                                    path = it.folder.path
                                ),
                                editMode = editMode
                            )
                        }

                        is SmbServer ->
                            uiState = SmbEditScreenUiState(
                                form = SmbEditScreenForm(
                                    displayName = bookshelf.displayName,
                                    host = bookshelf.host,
                                    port = bookshelf.port,
                                    path = it.folder.path.removePrefix("/").removeSuffix("/"),
                                    auth = when (bookshelf.auth) {
                                        is SmbServer.Auth.Guest -> SmbEditScreenForm.Auth.Guest
                                        is SmbServer.Auth.UsernamePassword ->
                                            SmbEditScreenForm.Auth.UserPass
                                    },
                                    domain = when (val auth = bookshelf.auth) {
                                        is SmbServer.Auth.Guest -> ""
                                        is SmbServer.Auth.UsernamePassword -> auth.domain
                                    },
                                    username = when (val auth = bookshelf.auth) {
                                        is SmbServer.Auth.Guest -> ""
                                        is SmbServer.Auth.UsernamePassword -> auth.username
                                    },
                                    password = when (val auth = bookshelf.auth) {
                                        is SmbServer.Auth.Guest -> ""
                                        is SmbServer.Auth.UsernamePassword -> auth.password
                                    }
                                ),
                                editMode = editMode
                            )

                        ShareContents -> Unit
                    }
                },
                onError = {
                }
            )
        }
    }

    override fun onSubmit(form: BookshelfEditForm) {
        logcat { "onSubmit(form: $form)" }
        uiState = when (val currentUiState = uiState) {
            is BookshelfEditScreenUiState.Loading -> currentUiState
            is InternalStorageEditScreenUiState ->
                currentUiState.copy(form = currentUiState.form.copy(isRunning = true))

            is SmbEditScreenUiState -> currentUiState.copy(form = currentUiState.form.copy(isRunning = true))
        }
        val bookshelf: Bookshelf
        val path: String
        when (form) {
            is InternalStorageEditScreenForm -> when (editMode) {
                is BookshelfEditMode.Edit -> {
                    bookshelf = InternalStorage(
                        editMode.bookshelfId,
                        form.displayName
                    )
                    path = form.path!!
                }

                is BookshelfEditMode.Register -> {
                    bookshelf = InternalStorage(form.displayName)
                    path = form.path!!
                }
            }

            is SmbEditScreenForm -> {
                val paths = "/${form.path}/".replace("(/+)".toRegex(), "/")
                val auth = when (form.auth) {
                    SmbEditScreenForm.Auth.Guest -> SmbServer.Auth.Guest
                    SmbEditScreenForm.Auth.UserPass -> SmbServer.Auth.UsernamePassword(
                        domain = form.domain,
                        username = form.username,
                        password = form.password
                    )
                }
                when (editMode) {
                    is BookshelfEditMode.Edit -> {
                        bookshelf = SmbServer(
                            editMode.bookshelfId,
                            displayName = form.displayName,
                            host = form.host,
                            port = form.port,
                            auth = auth,
                        )
                        path = paths
                    }

                    is BookshelfEditMode.Register -> {
                        bookshelf = SmbServer(
                            displayName = form.displayName,
                            host = form.host,
                            port = form.port,
                            auth = auth,
                        )
                        path = paths
                    }
                }
            }
        }
        scope.launch {
            delay(300)
            registerBookshelfUseCase(RegisterBookshelfUseCase.Request(bookshelf, path)).fold(
                onSuccess = {
                    events.tryEmit(BookshelfEditScreenEvent.Complete)
                },
                onError = {
                    scope.launch {
                        when (it) {
                            RegisterBookshelfUseCase.Error.Auth ->
                                snackbarHostState.showSnackbar(getString(Res.string.bookshelf_edit_error_bad_auth))

                            RegisterBookshelfUseCase.Error.Host ->
                                snackbarHostState.showSnackbar(getString(Res.string.bookshelf_edit_error_bad_host))

                            RegisterBookshelfUseCase.Error.Network ->
                                snackbarHostState.showSnackbar(getString(Res.string.bookshelf_edit_error_bad_network))

                            RegisterBookshelfUseCase.Error.Path ->
                                snackbarHostState.showSnackbar(getString(Res.string.bookshelf_edit_error_bad_path))

                            RegisterBookshelfUseCase.Error.System ->
                                snackbarHostState.showSnackbar("?????????????")
                        }
                    }
                }
            )
        }
    }
}
