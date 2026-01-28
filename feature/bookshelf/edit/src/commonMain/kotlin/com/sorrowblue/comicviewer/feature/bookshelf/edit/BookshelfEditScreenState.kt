package com.sorrowblue.comicviewer.feature.bookshelf.edit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfType
import com.sorrowblue.comicviewer.domain.model.bookshelf.InternalStorage
import com.sorrowblue.comicviewer.domain.model.bookshelf.ShareContents
import com.sorrowblue.comicviewer.domain.model.bookshelf.SmbServer
import com.sorrowblue.comicviewer.domain.model.dataOrNull
import com.sorrowblue.comicviewer.domain.model.fold
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.GetBookshelfInfoUseCase
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.RegisterBookshelfUseCase
import com.sorrowblue.comicviewer.feature.bookshelf.edit.component.AuthField
import com.sorrowblue.comicviewer.feature.bookshelf.edit.component.FolderSelectFieldName
import com.sorrowblue.comicviewer.feature.bookshelf.edit.component.FolderSelectFieldState
import com.sorrowblue.comicviewer.feature.bookshelf.edit.component.HostField
import com.sorrowblue.comicviewer.feature.bookshelf.edit.component.PathFieldName
import com.sorrowblue.comicviewer.feature.bookshelf.edit.component.PortField
import com.sorrowblue.comicviewer.feature.bookshelf.edit.component.rememberFolderSelectFieldState
import com.sorrowblue.comicviewer.feature.bookshelf.edit.section.BookshelfEditScreenUiState
import com.sorrowblue.comicviewer.framework.ui.EventFlow
import com.sorrowblue.comicviewer.framework.ui.kSerializableSaver
import comicviewer.feature.bookshelf.edit.generated.resources.Res
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_error_bad_auth
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_error_bad_host
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_error_bad_network
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_error_bad_path
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_error_bad_port
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_msg_cancelled_folder_selection
import io.github.takahirom.rin.rememberRetained
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import logcat.logcat
import org.jetbrains.compose.resources.getString
import soil.form.FieldError
import soil.form.FieldOptions
import soil.form.FieldValidationMode
import soil.form.FieldValidationStrategy
import soil.form.FormOptions
import soil.form.compose.Form
import soil.form.compose.FormPolicy
import soil.form.compose.FormState
import soil.form.compose.rememberForm
import soil.form.compose.rememberFormState

internal sealed interface BookshelfEditScreenEvent {
    data object Complete : BookshelfEditScreenEvent
}

internal interface IBookshelfEditScreenState {
    val uiState: BookshelfEditScreenUiState
    val events: EventFlow<BookshelfEditScreenEvent>

    fun onSubmit(form: BookshelfEditForm)

    val formState: FormState<out BookshelfEditForm>
    val form: Form<out BookshelfEditForm>
    var initialForm: BookshelfEditForm
}

internal sealed interface BookshelfEditScreenState : IBookshelfEditScreenState

internal interface SmbEditScreenState : BookshelfEditScreenState {
    override val formState: FormState<SmbEditForm>
    override val form: Form<SmbEditForm>
}

internal interface InternalStorageEditScreenState : BookshelfEditScreenState {
    fun onOpenDocumentTreeCancel()

    val folderSelectFieldState: FolderSelectFieldState
    override val formState: FormState<InternalStorageEditForm>
    override val form: Form<InternalStorageEditForm>
}

@Composable
context(context: BookshelfEditScreenContext)
internal fun rememberBookshelfEditScreenState(
    editType: BookshelfEditType,
): BookshelfEditScreenState {
    val coroutineScope = rememberCoroutineScope()
    val state = when (editType.bookshelfType) {
        BookshelfType.SMB -> {
            val formState =
                rememberFormState(
                    SmbEditForm(),
                    kSerializableSaver(),
                    policy = FormPolicy(
                        formOptions = FormOptions(preValidation = false),
                        fieldOptions = FieldOptions(
                            validationStrategy = FieldValidationStrategy(
                                initial = FieldValidationMode.Change,
                                next = { _, _ -> FieldValidationMode.Change },
                            ),
                        ),
                    ),
                )
            rememberRetained {
                SmbEditScreenStateImpl(
                    editType = editType,
                    getBookshelfInfoUseCase = context.getBookshelfInfoUseCase,
                    registerBookshelfUseCase = context.registerBookshelfUseCase,
                    coroutineScope = coroutineScope,
                    formState = formState,
                    initialForm = SmbEditForm(),
                )
            }.apply {
                form = rememberForm(state = formState, onSubmit = ::onSubmit)
            }
        }

        BookshelfType.DEVICE -> {
            val formState = rememberFormState(InternalStorageEditForm(), kSerializableSaver())
            rememberRetained {
                InternalStorageEditScreenStateImpl(
                    editType = editType,
                    getBookshelfInfoUseCase = context.getBookshelfInfoUseCase,
                    registerBookshelfUseCase = context.registerBookshelfUseCase,
                    coroutineScope = coroutineScope,
                    formState = formState,
                    initialForm = InternalStorageEditForm(),
                )
            }.apply {
                form = rememberForm(state = formState, onSubmit = ::onSubmit)
                folderSelectFieldState = rememberFolderSelectFieldState(
                    form = form,
                    onOpenDocumentTreeCancel = ::onOpenDocumentTreeCancel,
                )
            }
        }
    }
    return state
}

private class SmbEditScreenStateImpl(
    editType: BookshelfEditType,
    getBookshelfInfoUseCase: GetBookshelfInfoUseCase,
    registerBookshelfUseCase: RegisterBookshelfUseCase,
    coroutineScope: CoroutineScope,
    override val formState: FormState<SmbEditForm>,
    initialForm: BookshelfEditForm,
) : BookshelfEditScreenStateImpl(
    editType,
    getBookshelfInfoUseCase,
    registerBookshelfUseCase,
    coroutineScope,
    formState,
    initialForm,
),
    SmbEditScreenState {
    override lateinit var form: Form<SmbEditForm>
}

private class InternalStorageEditScreenStateImpl(
    editType: BookshelfEditType,
    getBookshelfInfoUseCase: GetBookshelfInfoUseCase,
    registerBookshelfUseCase: RegisterBookshelfUseCase,
    coroutineScope: CoroutineScope,
    override val formState: FormState<InternalStorageEditForm>,
    initialForm: BookshelfEditForm,
) : BookshelfEditScreenStateImpl(
    editType,
    getBookshelfInfoUseCase,
    registerBookshelfUseCase,
    coroutineScope,
    formState,
    initialForm,
),
    InternalStorageEditScreenState {
    override lateinit var form: Form<InternalStorageEditForm>
    override lateinit var folderSelectFieldState: FolderSelectFieldState

    override fun onOpenDocumentTreeCancel() {
        coroutineScope.launch {
            formState.setError(
                FolderSelectFieldName to FieldError(
                    getString(Res.string.bookshelf_edit_msg_cancelled_folder_selection),
                ),
            )
        }
    }
}

private abstract class BookshelfEditScreenStateImpl(
    private val editType: BookshelfEditType,
    private val getBookshelfInfoUseCase: GetBookshelfInfoUseCase,
    private val registerBookshelfUseCase: RegisterBookshelfUseCase,
    var coroutineScope: CoroutineScope,
    override val formState: FormState<out BookshelfEditForm>,
    override var initialForm: BookshelfEditForm,
) : IBookshelfEditScreenState {
    override val events: EventFlow<BookshelfEditScreenEvent> = EventFlow()

    override var uiState by mutableStateOf(BookshelfEditScreenUiState())

    init {
        when (editType) {
            is BookshelfEditType.Register -> {
                uiState = uiState.copy(progress = false)
            }

            is BookshelfEditType.Edit -> {
                uiState = uiState.copy(progress = true)
                fetch(editType.bookshelfId)
            }
        }
    }

    fun fetch(bookshelfId: BookshelfId) {
        coroutineScope.launch {
            getBookshelfInfoUseCase(GetBookshelfInfoUseCase.Request(bookshelfId)).first().fold(
                onSuccess = {
                    val form: BookshelfEditForm
                    when (val bookshelf = it.bookshelf) {
                        is InternalStorage -> {
                            form = InternalStorageEditForm(
                                displayName = bookshelf.displayName,
                                path = it.folder.path,
                            )
                        }

                        is SmbServer -> {
                            form = SmbEditForm(
                                displayName = bookshelf.displayName,
                                host = bookshelf.host,
                                port = bookshelf.port,
                                path = it.folder.path
                                    .removePrefix("/")
                                    .removeSuffix("/"),
                                auth = when (bookshelf.auth) {
                                    is SmbServer.Auth.Guest -> SmbEditForm.Auth.Guest

                                    is SmbServer.Auth.UsernamePassword ->
                                        SmbEditForm.Auth.UserPass
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
                                },
                            )
                        }

                        ShareContents -> return@launch
                    }
                    initialForm = form
                    @Suppress("UNCHECKED_CAST")
                    (formState as FormState<BookshelfEditForm>).reset(form)
                    uiState = uiState.copy(progress = false)
                },
                onError = {
                },
            )
        }
    }

    override fun onSubmit(form: BookshelfEditForm) {
        coroutineScope.launch {
            logcat { "onSubmit(form: $form)" }
            uiState = uiState.copy(progress = true)
            val bookshelf: Bookshelf
            val path: String
            when (form) {
                is InternalStorageEditForm -> when (editType) {
                    is BookshelfEditType.Edit -> {
                        bookshelf = (getBookshelf(editType.bookshelfId) as InternalStorage)
                            .copy(displayName = form.displayName)
                        path = requireNotNull(form.path)
                    }

                    is BookshelfEditType.Register -> {
                        bookshelf = InternalStorage(form.displayName)
                        path = requireNotNull(form.path)
                    }
                }

                is SmbEditForm -> {
                    val paths = "/${form.path}/".replace("(/+)".toRegex(), "/")
                    val auth = when (form.auth) {
                        SmbEditForm.Auth.Guest -> SmbServer.Auth.Guest

                        SmbEditForm.Auth.UserPass -> SmbServer.Auth.UsernamePassword(
                            domain = form.domain,
                            username = form.username,
                            password = form.password,
                        )
                    }
                    when (editType) {
                        is BookshelfEditType.Edit -> {
                            bookshelf = (getBookshelf(editType.bookshelfId) as SmbServer)
                                .copy(
                                    displayName = form.displayName,
                                    host = form.host,
                                    port = form.port,
                                    auth = auth,
                                )
                            path = paths
                        }

                        is BookshelfEditType.Register -> {
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
            delay(300)
            registerBookshelfUseCase(RegisterBookshelfUseCase.Request(bookshelf, path)).fold(
                onSuccess = {
                    events.tryEmit(BookshelfEditScreenEvent.Complete)
                },
                onError = {
                    uiState = uiState.copy(progress = false)
                    when (it) {
                        RegisterBookshelfUseCase.Error.Auth -> {
                            formState.setError(
                                AuthField to FieldError(
                                    getString(Res.string.bookshelf_edit_error_bad_auth),
                                ),
                            )
                        }

                        RegisterBookshelfUseCase.Error.Host -> {
                            formState.setError(
                                HostField to FieldError(
                                    getString(Res.string.bookshelf_edit_error_bad_host),
                                ),
                            )
                            formState.setError(
                                PortField to FieldError(
                                    getString(Res.string.bookshelf_edit_error_bad_port),
                                ),
                            )
                        }

                        RegisterBookshelfUseCase.Error.Network -> {
                            formState.setError(
                                "auth" to FieldError(
                                    getString(Res.string.bookshelf_edit_error_bad_network),
                                ),
                            )
                        }

                        RegisterBookshelfUseCase.Error.Path -> {
                            formState.setError(
                                PathFieldName to FieldError(
                                    getString(Res.string.bookshelf_edit_error_bad_path),
                                ),
                            )
                        }

                        RegisterBookshelfUseCase.Error.System -> {
                            formState.setError("auth" to FieldError("unknown error"))
                        }
                    }
                },
            )
        }
    }

    suspend fun getBookshelf(id: BookshelfId): Bookshelf = requireNotNull(
        getBookshelfInfoUseCase(GetBookshelfInfoUseCase.Request(id))
            .first()
            .dataOrNull(),
    ).bookshelf
}
