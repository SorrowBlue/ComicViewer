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
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfType
import com.sorrowblue.comicviewer.domain.model.bookshelf.InternalStorage
import com.sorrowblue.comicviewer.domain.model.bookshelf.ShareContents
import com.sorrowblue.comicviewer.domain.model.bookshelf.SmbServer
import com.sorrowblue.comicviewer.domain.model.fold
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.GetBookshelfInfoUseCase
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.RegisterBookshelfUseCase
import com.sorrowblue.comicviewer.framework.ui.ScreenStateEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import logcat.logcat

sealed interface BookshelfEditScreenEvent {
    data object Complete : BookshelfEditScreenEvent
}

internal interface BookshelfEditScreenState : ScreenStateEvent<BookshelfEditScreenEvent> {
    val uiState: BookshelfEditScreenUiState
    val snackbarHostState: SnackbarHostState
    suspend fun onSubmit(form: BookshelfEditForm)
}

@Composable
internal fun rememberBookshelfEditScreenState(
    navArgs: BookshelfEditArgs,
    context: Context = LocalContext.current,
    scope: CoroutineScope = rememberCoroutineScope(),
    viewModel: BookshelfEditScreenViewModel = hiltViewModel(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
): BookshelfEditScreenState {
    return remember {
        BookshelfEditScreenStateImpl(
            context = context,
            navArgs = navArgs,
            getBookshelfInfoUseCase = viewModel.getBookshelfInfoUseCase,
            registerBookshelfUseCase = viewModel.registerBookshelfUseCase,
            scope = scope,
            snackbarHostState = snackbarHostState,
        )
    }
}

private class BookshelfEditScreenStateImpl(
    private val context: Context,
    private val navArgs: BookshelfEditArgs,
    private val getBookshelfInfoUseCase: GetBookshelfInfoUseCase,
    private val registerBookshelfUseCase: RegisterBookshelfUseCase,
    override val scope: CoroutineScope,
    override val snackbarHostState: SnackbarHostState,
) : BookshelfEditScreenState {

    override var uiState: BookshelfEditScreenUiState by mutableStateOf(
        BookshelfEditScreenUiState.Loading(navArgs.editMode)
    )
        private set

    override val event = MutableSharedFlow<BookshelfEditScreenEvent>()

    init {
        logcat { "navArgs=$navArgs" }
        when (navArgs.editMode) {
            is BookshelfEditMode.Edit -> fetch(navArgs.editMode.bookshelfId)
            is BookshelfEditMode.Register ->
                uiState = when (navArgs.editMode.bookshelfType) {
                    BookshelfType.DEVICE ->
                        InternalStorageEditScreenUiState(
                            form = InternalStorageEditScreenForm(),
                            editMode = navArgs.editMode
                        )

                    BookshelfType.SMB ->
                        SmbEditScreenUiState(
                            form = SmbEditScreenForm(),
                            editMode = navArgs.editMode
                        )
                }
        }
    }

    fun fetch(bookshelfId: BookshelfId) {
        scope.launch {
            delay(2000)
            getBookshelfInfoUseCase(GetBookshelfInfoUseCase.Request(bookshelfId)).first().fold(
                onSuccess = {
                    when (val bookshelf = it.bookshelf) {
                        is InternalStorage -> {
                            uiState = InternalStorageEditScreenUiState(
                                form = InternalStorageEditScreenForm(
                                    displayName = bookshelf.displayName,
                                    path = it.folder.path.toUri()
                                ),
                                editMode = navArgs.editMode
                            )
                        }

                        is SmbServer ->
                            uiState = SmbEditScreenUiState(
                                form = SmbEditScreenForm(
                                    displayName = bookshelf.displayName,
                                    host = bookshelf.host,
                                    port = bookshelf.port,
                                    path = it.folder.path,
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
                                editMode = navArgs.editMode
                            )

                        ShareContents -> Unit
                    }
                },
                onError = {
                }
            )
        }
    }

    override suspend fun onSubmit(form: BookshelfEditForm) {
        logcat { "onSubmit(form: $form)" }
        delay(2000)
        val (bookshelf, path) = when (form) {
            is InternalStorageEditScreenForm ->
                when (navArgs.editMode) {
                    is BookshelfEditMode.Edit -> InternalStorage(
                        navArgs.editMode.bookshelfId,
                        form.displayName
                    ) to form.path!!.toString()

                    is BookshelfEditMode.Register -> InternalStorage(form.displayName) to form.path!!.toString()
                }

            is SmbEditScreenForm -> {
                val path = "/${form.path}/".replace("(/+)".toRegex(), "/")
                val auth = when (form.auth) {
                    SmbEditScreenForm.Auth.Guest -> SmbServer.Auth.Guest
                    SmbEditScreenForm.Auth.UserPass -> SmbServer.Auth.UsernamePassword(
                        domain = form.domain,
                        username = form.username,
                        password = form.password
                    )
                }
                when (navArgs.editMode) {
                    is BookshelfEditMode.Edit -> SmbServer(
                        navArgs.editMode.bookshelfId,
                        displayName = form.displayName,
                        host = form.host,
                        port = form.port,
                        auth = auth,
                    ) to path

                    is BookshelfEditMode.Register -> SmbServer(
                        displayName = form.displayName,
                        host = form.host,
                        port = form.port,
                        auth = auth,
                    ) to path
                }
            }
        }
        registerBookshelfUseCase(RegisterBookshelfUseCase.Request(bookshelf, path)).fold(
            onSuccess = {
                sendEvent(BookshelfEditScreenEvent.Complete)
            },
            onError = {
                scope.launch {
                    when (it) {
                        RegisterBookshelfUseCase.Error.Auth -> {
                            snackbarHostState.showSnackbar(context.getString(R.string.bookshelf_edit_error_bad_auth))
                        }

                        RegisterBookshelfUseCase.Error.Host -> {
                            snackbarHostState.showSnackbar(context.getString(R.string.bookshelf_edit_error_bad_host))
                        }

                        RegisterBookshelfUseCase.Error.Network -> {
                            snackbarHostState.showSnackbar(context.getString(R.string.bookshelf_edit_error_bad_network))
                        }

                        RegisterBookshelfUseCase.Error.Path -> {
                            snackbarHostState.showSnackbar(context.getString(R.string.bookshelf_edit_error_bad_path))
                        }

                        RegisterBookshelfUseCase.Error.System -> {
                            snackbarHostState.showSnackbar("?????????????")
                        }
                    }
                }
            }
        )
    }
}
