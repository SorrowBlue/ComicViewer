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
import com.sorrowblue.comicviewer.feature.bookshelf.edit.section.BookshelfEditorScreenUiState
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
import org.koin.compose.koinInject
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

internal interface IBookshelfEditorScreenState {

    val uiState: BookshelfEditorScreenUiState
    val events: EventFlow<BookshelfEditScreenEvent>
    fun onSubmit(form: BookshelfEditorForm)
    val formState: FormState<out BookshelfEditorForm>
    val form: Form<out BookshelfEditorForm>
    var initialForm: BookshelfEditorForm
}

internal sealed interface BookshelfEditorScreenState : IBookshelfEditorScreenState

internal interface SmbEditorScreenState : BookshelfEditorScreenState {
    override val formState: FormState<SmbEditorForm>
    override val form: Form<SmbEditorForm>
}

internal interface InternalStorageEditorScreenState : BookshelfEditorScreenState {
    fun onOpenDocumentTreeCancel()

    val folderSelectFieldState: FolderSelectFieldState
    override val formState: FormState<InternalStorageEditorForm>
    override val form: Form<InternalStorageEditorForm>
}

@Composable
internal fun rememberBookshelfEditorScreenState(
    editorType: BookshelfEditorType,
    getBookshelfInfoUseCase: GetBookshelfInfoUseCase = koinInject(),
    registerBookshelfUseCase: RegisterBookshelfUseCase = koinInject(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
): BookshelfEditorScreenState {
    val state = when (editorType.bookshelfType) {
        BookshelfType.SMB -> {
            val formState =
                rememberFormState(
                    SmbEditorForm(),
                    kSerializableSaver(),
                    policy = FormPolicy(
                        formOptions = FormOptions(preValidation = false),
                        fieldOptions = FieldOptions(
                            validationStrategy = FieldValidationStrategy(
                                initial = FieldValidationMode.Change,
                                next = { _, _ -> FieldValidationMode.Change }
                            ),
                        )
                    )
                )
            rememberRetained {
                SmbEditScreenStateImpl(
                    editorType = editorType,
                    getBookshelfInfoUseCase = getBookshelfInfoUseCase,
                    registerBookshelfUseCase = registerBookshelfUseCase,
                    coroutineScope = coroutineScope,
                    formState = formState
                ).apply {
                    initialForm = SmbEditorForm()
                }
            }.apply {
                this.coroutineScope = coroutineScope
                this.form = rememberForm(state = formState, onSubmit = ::onSubmit)
            }
        }

        BookshelfType.DEVICE -> {
            val formState = rememberFormState(InternalStorageEditorForm(), kSerializableSaver())
            rememberRetained {
                InternalStorageEditScreenStateImpl(
                    editorType = editorType,
                    getBookshelfInfoUseCase = getBookshelfInfoUseCase,
                    registerBookshelfUseCase = registerBookshelfUseCase,
                    coroutineScope = coroutineScope,
                    formState = formState
                ).apply {
                    initialForm = InternalStorageEditorForm()
                }
            }.apply {
                this.coroutineScope = coroutineScope
                this.form = rememberForm(state = formState, onSubmit = ::onSubmit)
                this.folderSelectFieldState = rememberFolderSelectFieldState(
                    form = form,
                    onOpenDocumentTreeCancel = ::onOpenDocumentTreeCancel
                )
            }
        }
    }
    return state
}

private class SmbEditScreenStateImpl(
    editorType: BookshelfEditorType,
    getBookshelfInfoUseCase: GetBookshelfInfoUseCase,
    registerBookshelfUseCase: RegisterBookshelfUseCase,
    coroutineScope: CoroutineScope,
    override val formState: FormState<SmbEditorForm>,
) : BookshelfEditScreenStateImpl(
    editorType,
    getBookshelfInfoUseCase,
    registerBookshelfUseCase,
    coroutineScope,
    formState
),
    SmbEditorScreenState {
    override lateinit var form: Form<SmbEditorForm>
}

private class InternalStorageEditScreenStateImpl(
    editorType: BookshelfEditorType,
    getBookshelfInfoUseCase: GetBookshelfInfoUseCase,
    registerBookshelfUseCase: RegisterBookshelfUseCase,
    coroutineScope: CoroutineScope,
    override val formState: FormState<InternalStorageEditorForm>,
) : BookshelfEditScreenStateImpl(
    editorType,
    getBookshelfInfoUseCase,
    registerBookshelfUseCase,
    coroutineScope,
    formState
),
    InternalStorageEditorScreenState {
    override lateinit var form: Form<InternalStorageEditorForm>
    override lateinit var folderSelectFieldState: FolderSelectFieldState
    override fun onOpenDocumentTreeCancel() {
        coroutineScope.launch {
            formState.setError(
                FolderSelectFieldName to FieldError(getString(Res.string.bookshelf_edit_msg_cancelled_folder_selection))
            )
        }
    }
}

private abstract class BookshelfEditScreenStateImpl(
    private val editorType: BookshelfEditorType,
    private val getBookshelfInfoUseCase: GetBookshelfInfoUseCase,
    private val registerBookshelfUseCase: RegisterBookshelfUseCase,
    var coroutineScope: CoroutineScope,
    override val formState: FormState<out BookshelfEditorForm>,
) : IBookshelfEditorScreenState {

    override lateinit var initialForm: BookshelfEditorForm

    override val events: EventFlow<BookshelfEditScreenEvent> = EventFlow()

    override var uiState by mutableStateOf(BookshelfEditorScreenUiState())

    init {
        when (editorType) {
            is BookshelfEditorType.Register -> {
                uiState = uiState.copy(progress = false)
            }

            is BookshelfEditorType.Edit -> {
                uiState = uiState.copy(progress = true)
                fetch(editorType.bookshelfId)
            }
        }
    }

    fun fetch(bookshelfId: BookshelfId) {
        coroutineScope.launch {
            getBookshelfInfoUseCase(GetBookshelfInfoUseCase.Request(bookshelfId)).first().fold(
                onSuccess = {
                    val form: BookshelfEditorForm
                    when (val bookshelf = it.bookshelf) {
                        is InternalStorage -> {
                            form = InternalStorageEditorForm(
                                displayName = bookshelf.displayName,
                                path = it.folder.path
                            )
                        }

                        is SmbServer -> {
                            form = SmbEditorForm(
                                displayName = bookshelf.displayName,
                                host = bookshelf.host,
                                port = bookshelf.port,
                                path = it.folder.path.removePrefix("/").removeSuffix("/"),
                                auth = when (bookshelf.auth) {
                                    is SmbServer.Auth.Guest -> SmbEditorForm.Auth.Guest
                                    is SmbServer.Auth.UsernamePassword ->
                                        SmbEditorForm.Auth.UserPass
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
                            )
                        }

                        ShareContents -> return@launch
                    }
                    initialForm = form
                    (formState as FormState<BookshelfEditorForm>).reset(form)
                    uiState = uiState.copy(progress = false)
                },
                onError = {
                }
            )
        }
    }

    override fun onSubmit(form: BookshelfEditorForm) {
        coroutineScope.launch {
            logcat { "onSubmit(form: $form)" }
            uiState = uiState.copy(progress = true)
            val bookshelf: Bookshelf
            val path: String
            when (form) {
                is InternalStorageEditorForm -> when (editorType) {
                    is BookshelfEditorType.Edit -> {
                        bookshelf = (getBookshelf(editorType.bookshelfId) as InternalStorage)
                            .copy(displayName = form.displayName)
                        path = form.path!!
                    }

                    is BookshelfEditorType.Register -> {
                        bookshelf = InternalStorage(form.displayName)
                        path = form.path!!
                    }
                }

                is SmbEditorForm -> {
                    val paths = "/${form.path}/".replace("(/+)".toRegex(), "/")
                    val auth = when (form.auth) {
                        SmbEditorForm.Auth.Guest -> SmbServer.Auth.Guest
                        SmbEditorForm.Auth.UserPass -> SmbServer.Auth.UsernamePassword(
                            domain = form.domain,
                            username = form.username,
                            password = form.password
                        )
                    }
                    when (editorType) {
                        is BookshelfEditorType.Edit -> {
                            bookshelf = (getBookshelf(editorType.bookshelfId) as SmbServer)
                                .copy(
                                    displayName = form.displayName,
                                    host = form.host,
                                    port = form.port,
                                    auth = auth,
                                )
                            path = paths
                        }

                        is BookshelfEditorType.Register -> {
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
                                AuthField to FieldError(getString(Res.string.bookshelf_edit_error_bad_auth))
                            )
                        }

                        RegisterBookshelfUseCase.Error.Host -> {
                            formState.setError(
                                HostField to FieldError(getString(Res.string.bookshelf_edit_error_bad_host))
                            )
                            formState.setError(
                                PortField to FieldError(getString(Res.string.bookshelf_edit_error_bad_port))
                            )
                        }

                        RegisterBookshelfUseCase.Error.Network -> {
                            formState.setError(
                                "auth" to FieldError(getString(Res.string.bookshelf_edit_error_bad_network))
                            )
                        }

                        RegisterBookshelfUseCase.Error.Path -> {
                            formState.setError(
                                PathFieldName to FieldError(getString(Res.string.bookshelf_edit_error_bad_path))
                            )
                        }

                        RegisterBookshelfUseCase.Error.System -> {
                            formState.setError("auth" to FieldError("unknown error"))
                        }
                    }
                }
            )
        }
    }

    suspend fun getBookshelf(id: BookshelfId): Bookshelf {
        return getBookshelfInfoUseCase(GetBookshelfInfoUseCase.Request(id)).first()
            .dataOrNull()!!.bookshelf
    }
}
