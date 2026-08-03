/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.bookshelf.edit.section

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfType
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.RegisterBookshelfUseCase
import com.sorrowblue.comicviewer.feature.bookshelf.edit.BookshelfEditForm
import com.sorrowblue.comicviewer.feature.bookshelf.edit.BookshelfEditType
import com.sorrowblue.comicviewer.feature.bookshelf.edit.BookshelfEditViewModel
import com.sorrowblue.comicviewer.feature.bookshelf.edit.BookshelfEditViewModelEvent
import com.sorrowblue.comicviewer.feature.bookshelf.edit.DeviceEditForm
import com.sorrowblue.comicviewer.feature.bookshelf.edit.SmbEditForm
import com.sorrowblue.comicviewer.feature.bookshelf.edit.component.AuthField
import com.sorrowblue.comicviewer.feature.bookshelf.edit.component.FolderSelectFieldName
import com.sorrowblue.comicviewer.feature.bookshelf.edit.component.FolderSelectFieldState
import com.sorrowblue.comicviewer.feature.bookshelf.edit.component.HostField
import com.sorrowblue.comicviewer.feature.bookshelf.edit.component.PathFieldName
import com.sorrowblue.comicviewer.feature.bookshelf.edit.component.PortField
import com.sorrowblue.comicviewer.feature.bookshelf.edit.component.rememberFolderSelectFieldState
import com.sorrowblue.comicviewer.framework.permission.localnetwork.LocalNetworkPermissionRequester
import com.sorrowblue.comicviewer.framework.permission.localnetwork.rememberLocalNetworkPermissionRequester
import com.sorrowblue.comicviewer.framework.ui.EventFlow
import com.sorrowblue.comicviewer.framework.ui.kSerializableSaver
import comicviewer.feature.bookshelf.edit.generated.resources.Res
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_error_bad_auth
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_error_bad_host
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_error_bad_network
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_error_bad_path
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_error_bad_port
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_msg_cancelled_folder_selection
import dev.zacsweers.metrox.viewmodel.assistedMetroViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
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
    val permissionRequester: LocalNetworkPermissionRequester

    fun onPermissionConfirmClick()
}

internal interface LocalEditScreenState : BookshelfEditScreenState {
    fun onOpenDocumentTreeCancel()

    val folderSelectFieldState: FolderSelectFieldState
    override val formState: FormState<DeviceEditForm>
    override val form: Form<DeviceEditForm>
}

@Composable
internal fun rememberBookshelfEditScreenState(
    editType: BookshelfEditType,
    viewModel: BookshelfEditViewModel =
        assistedMetroViewModel<BookshelfEditViewModel, BookshelfEditViewModel.Factory> {
            create(editType)
        },
): BookshelfEditScreenState {
    val coroutineScope = rememberCoroutineScope()
    val permissionRequester = rememberLocalNetworkPermissionRequester()
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
            rememberSaveable(
                saver = SmbEditScreenStateImpl.saver(
                    coroutineScope = coroutineScope,
                    formState = formState,
                    initialForm = SmbEditForm(),
                    eventFlow = viewModel.event,
                    bookshelfEditFormFlow = viewModel.formFlow,
                    submit = viewModel::submit,
                ),
            ) {
                SmbEditScreenStateImpl(
                    coroutineScope = coroutineScope,
                    formState = formState,
                    initialForm = SmbEditForm(),
                    eventFlow = viewModel.event,
                    bookshelfEditFormFlow = viewModel.formFlow,
                    submit = viewModel::submit,
                )
            }.apply {
                form = rememberForm(state = formState, onSubmit = ::onSubmit)
                this.permissionRequester = permissionRequester
            }
        }

        BookshelfType.DEVICE -> {
            val formState = rememberFormState(DeviceEditForm(), kSerializableSaver())

            rememberSaveable(
                saver = LocalEditScreenStateImpl.saver(
                    coroutineScope = coroutineScope,
                    formState = formState,
                    initialForm = SmbEditForm(),
                    eventFlow = viewModel.event,
                    bookshelfEditFormFlow = viewModel.formFlow,
                    submit = viewModel::submit,
                ),
            ) {
                LocalEditScreenStateImpl(
                    coroutineScope = coroutineScope,
                    formState = formState,
                    initialForm = DeviceEditForm(),
                    eventFlow = viewModel.event,
                    bookshelfEditFormFlow = viewModel.formFlow,
                    submit = viewModel::submit,
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
    isInitialized: Boolean = false,
    coroutineScope: CoroutineScope,
    override val formState: FormState<SmbEditForm>,
    initialForm: BookshelfEditForm,
    eventFlow: SharedFlow<BookshelfEditViewModelEvent>,
    bookshelfEditFormFlow: SharedFlow<BookshelfEditForm?>,
    submit: (BookshelfEditForm) -> Unit,
) : BookshelfEditScreenStateImpl(
    isInitialized,
    coroutineScope,
    formState,
    initialForm,
    eventFlow,
    bookshelfEditFormFlow,
    submit,
),
    SmbEditScreenState {
    override lateinit var form: Form<SmbEditForm>

    override lateinit var permissionRequester: LocalNetworkPermissionRequester

    override fun onPermissionConfirmClick() {
        permissionRequester.onPermissionConfirmClick()
    }

    companion object {
        fun saver(
            coroutineScope: CoroutineScope,
            formState: FormState<SmbEditForm>,
            initialForm: BookshelfEditForm,
            eventFlow: SharedFlow<BookshelfEditViewModelEvent>,
            bookshelfEditFormFlow: SharedFlow<BookshelfEditForm?>,
            submit: (BookshelfEditForm) -> Unit,
        ): Saver<SmbEditScreenStateImpl, Boolean> = Saver(
            save = { it.isInitialized },
            restore = { isInitialized ->
                SmbEditScreenStateImpl(
                    isInitialized = isInitialized,
                    coroutineScope = coroutineScope,
                    formState = formState,
                    initialForm = initialForm,
                    eventFlow = eventFlow,
                    bookshelfEditFormFlow = bookshelfEditFormFlow,
                    submit = submit,
                )
            },
        )
    }
}

private class LocalEditScreenStateImpl(
    isInitialized: Boolean = false,
    coroutineScope: CoroutineScope,
    override val formState: FormState<DeviceEditForm>,
    initialForm: BookshelfEditForm,
    eventFlow: SharedFlow<BookshelfEditViewModelEvent>,
    bookshelfEditFormFlow: SharedFlow<BookshelfEditForm?>,
    submit: (BookshelfEditForm) -> Unit,
) : BookshelfEditScreenStateImpl(
    isInitialized,
    coroutineScope,
    formState,
    initialForm,
    eventFlow,
    bookshelfEditFormFlow,
    submit,
),
    LocalEditScreenState {
    override lateinit var form: Form<DeviceEditForm>
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

    companion object {
        fun saver(
            coroutineScope: CoroutineScope,
            formState: FormState<DeviceEditForm>,
            initialForm: BookshelfEditForm,
            eventFlow: SharedFlow<BookshelfEditViewModelEvent>,
            bookshelfEditFormFlow: SharedFlow<BookshelfEditForm?>,
            submit: (BookshelfEditForm) -> Unit,
        ): Saver<LocalEditScreenStateImpl, Boolean> = Saver(
            save = { it.isInitialized },
            restore = { isInitialized ->
                LocalEditScreenStateImpl(
                    isInitialized = isInitialized,
                    coroutineScope = coroutineScope,
                    formState = formState,
                    initialForm = initialForm,
                    eventFlow = eventFlow,
                    bookshelfEditFormFlow = bookshelfEditFormFlow,
                    submit = submit,
                )
            },
        )
    }
}

private abstract class BookshelfEditScreenStateImpl(
    protected var isInitialized: Boolean,
    var coroutineScope: CoroutineScope,
    override val formState: FormState<out BookshelfEditForm>,
    override var initialForm: BookshelfEditForm,
    eventFlow: SharedFlow<BookshelfEditViewModelEvent>,
    bookshelfEditFormFlow: SharedFlow<BookshelfEditForm?>,
    private val submit: (BookshelfEditForm) -> Unit,
) : IBookshelfEditScreenState {
    override val events: EventFlow<BookshelfEditScreenEvent> = EventFlow()

    override var uiState by mutableStateOf(BookshelfEditScreenUiState())

    init {
        if (isInitialized) {
            uiState = uiState.copy(progress = false)
        } else {
            uiState = uiState.copy(progress = true)
            bookshelfEditFormFlow.onEach { form ->
                if (form != null) {
                    isInitialized = true
                    initialForm = form
                    @Suppress("UNCHECKED_CAST")
                    (formState as FormState<BookshelfEditForm>).reset(form)
                }
                uiState = uiState.copy(progress = false)
            }.launchIn(coroutineScope)
        }
        eventFlow.onEach { event ->
            when (event) {
                BookshelfEditViewModelEvent.Complete -> {
                    events.tryEmit(BookshelfEditScreenEvent.Complete)
                }

                is BookshelfEditViewModelEvent.RegisterError -> {
                    uiState = uiState.copy(progress = false)
                    when (event.error) {
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
                }
            }
        }.launchIn(coroutineScope)
    }

    override fun onSubmit(form: BookshelfEditForm) {
        submit(form)
    }
}
