/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.bookshelf.edit.editor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfType
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.RegisterBookshelfUseCase
import com.sorrowblue.comicviewer.feature.bookshelf.edit.BookshelfEditType
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

internal sealed interface BookshelfEditorScreenEvent {
    data object Complete : BookshelfEditorScreenEvent
}

@Composable
internal fun rememberBookshelfEditorScreenState(
    editType: BookshelfEditType,
    viewModel: BookshelfEditorViewModel =
        assistedMetroViewModel<BookshelfEditorViewModel, BookshelfEditorViewModel.Factory> {
            create(editType)
        },
): BookshelfEditorScreenState {
    val coroutineScope = rememberCoroutineScope()
    val permissionRequester = rememberLocalNetworkPermissionRequester()
    val state = when (editType.bookshelfType) {
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
                                next = { _, _ -> FieldValidationMode.Change },
                            ),
                        ),
                    ),
                )
            rememberSaveable(
                saver = SmbEditorScreenStateImpl.saver(
                    coroutineScope = coroutineScope,
                    formState = formState,
                    initialForm = SmbEditorForm(),
                    eventFlow = viewModel.event,
                    bookshelfEditorFormFlow = viewModel.formFlow,
                    submit = viewModel::submit,
                ),
            ) {
                SmbEditorScreenStateImpl(
                    coroutineScope = coroutineScope,
                    formState = formState,
                    initialForm = SmbEditorForm(),
                    eventFlow = viewModel.event,
                    bookshelfEditorFormFlow = viewModel.formFlow,
                    submit = viewModel::submit,
                )
            }.apply {
                form = rememberForm(state = formState, onSubmit = ::onSubmit)
                this.permissionRequester = permissionRequester
            }
        }

        BookshelfType.DEVICE -> {
            val formState = rememberFormState(DeviceEditorForm(), kSerializableSaver())

            rememberSaveable(
                saver = LocalEditorScreenStateImpl.saver(
                    coroutineScope = coroutineScope,
                    formState = formState,
                    initialForm = SmbEditorForm(),
                    eventFlow = viewModel.event,
                    bookshelfEditorFormFlow = viewModel.formFlow,
                    submit = viewModel::submit,
                ),
            ) {
                LocalEditorScreenStateImpl(
                    coroutineScope = coroutineScope,
                    formState = formState,
                    initialForm = DeviceEditorForm(),
                    eventFlow = viewModel.event,
                    bookshelfEditorFormFlow = viewModel.formFlow,
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

internal interface IBookshelfEditorScreenState {
    val uiState: BookshelfEditorScreenUiState
    val events: EventFlow<BookshelfEditorScreenEvent>

    fun onSubmit(form: BookshelfEditorForm)

    val formState: FormState<out BookshelfEditorForm>
    val form: Form<out BookshelfEditorForm>
    var initialForm: BookshelfEditorForm
}

internal sealed interface BookshelfEditorScreenState : IBookshelfEditorScreenState

internal interface SmbEditorScreenState : BookshelfEditorScreenState {
    override val formState: FormState<SmbEditorForm>
    override val form: Form<SmbEditorForm>
    val permissionRequester: LocalNetworkPermissionRequester

    fun onPermissionConfirmClick()
}

private class SmbEditorScreenStateImpl(
    isInitialized: Boolean = false,
    coroutineScope: CoroutineScope,
    override val formState: FormState<SmbEditorForm>,
    initialForm: BookshelfEditorForm,
    eventFlow: SharedFlow<BookshelfEditorViewModelEvent>,
    bookshelfEditorFormFlow: SharedFlow<BookshelfEditorForm?>,
    submit: (BookshelfEditorForm) -> Unit,
) : BookshelfEditorScreenStateImpl(
    isInitialized,
    coroutineScope,
    formState,
    initialForm,
    eventFlow,
    bookshelfEditorFormFlow,
    submit,
),
    SmbEditorScreenState {
    override lateinit var form: Form<SmbEditorForm>

    override lateinit var permissionRequester: LocalNetworkPermissionRequester

    override fun onPermissionConfirmClick() {
        permissionRequester.onPermissionConfirmClick()
    }

    companion object {
        fun saver(
            coroutineScope: CoroutineScope,
            formState: FormState<SmbEditorForm>,
            initialForm: BookshelfEditorForm,
            eventFlow: SharedFlow<BookshelfEditorViewModelEvent>,
            bookshelfEditorFormFlow: SharedFlow<BookshelfEditorForm?>,
            submit: (BookshelfEditorForm) -> Unit,
        ): Saver<SmbEditorScreenStateImpl, Boolean> = Saver(
            save = { it.isInitialized },
            restore = { isInitialized ->
                SmbEditorScreenStateImpl(
                    isInitialized = isInitialized,
                    coroutineScope = coroutineScope,
                    formState = formState,
                    initialForm = initialForm,
                    eventFlow = eventFlow,
                    bookshelfEditorFormFlow = bookshelfEditorFormFlow,
                    submit = submit,
                )
            },
        )
    }
}

internal interface LocalEditorScreenState : BookshelfEditorScreenState {
    fun onOpenDocumentTreeCancel()

    val folderSelectFieldState: FolderSelectFieldState
    override val formState: FormState<DeviceEditorForm>
    override val form: Form<DeviceEditorForm>
}

private class LocalEditorScreenStateImpl(
    isInitialized: Boolean = false,
    coroutineScope: CoroutineScope,
    override val formState: FormState<DeviceEditorForm>,
    initialForm: BookshelfEditorForm,
    eventFlow: SharedFlow<BookshelfEditorViewModelEvent>,
    bookshelfEditorFormFlow: SharedFlow<BookshelfEditorForm?>,
    submit: (BookshelfEditorForm) -> Unit,
) : BookshelfEditorScreenStateImpl(
    isInitialized,
    coroutineScope,
    formState,
    initialForm,
    eventFlow,
    bookshelfEditorFormFlow,
    submit,
),
    LocalEditorScreenState {
    override lateinit var form: Form<DeviceEditorForm>
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
            formState: FormState<DeviceEditorForm>,
            initialForm: BookshelfEditorForm,
            eventFlow: SharedFlow<BookshelfEditorViewModelEvent>,
            bookshelfEditorFormFlow: SharedFlow<BookshelfEditorForm?>,
            submit: (BookshelfEditorForm) -> Unit,
        ): Saver<LocalEditorScreenStateImpl, Boolean> = Saver(
            save = { it.isInitialized },
            restore = { isInitialized ->
                LocalEditorScreenStateImpl(
                    isInitialized = isInitialized,
                    coroutineScope = coroutineScope,
                    formState = formState,
                    initialForm = initialForm,
                    eventFlow = eventFlow,
                    bookshelfEditorFormFlow = bookshelfEditorFormFlow,
                    submit = submit,
                )
            },
        )
    }
}

private abstract class BookshelfEditorScreenStateImpl(
    protected var isInitialized: Boolean,
    var coroutineScope: CoroutineScope,
    override val formState: FormState<out BookshelfEditorForm>,
    override var initialForm: BookshelfEditorForm,
    eventFlow: SharedFlow<BookshelfEditorViewModelEvent>,
    bookshelfEditorFormFlow: SharedFlow<BookshelfEditorForm?>,
    private val submit: (BookshelfEditorForm) -> Unit,
) : IBookshelfEditorScreenState {
    override val events: EventFlow<BookshelfEditorScreenEvent> = EventFlow()

    override var uiState by mutableStateOf(BookshelfEditorScreenUiState())

    init {
        if (isInitialized) {
            uiState = uiState.copy(progress = false)
        } else {
            uiState = uiState.copy(progress = true)
            bookshelfEditorFormFlow.onEach { form ->
                if (form != null) {
                    isInitialized = true
                    initialForm = form
                    @Suppress("UNCHECKED_CAST")
                    (formState as FormState<BookshelfEditorForm>).reset(form)
                }
                uiState = uiState.copy(progress = false)
            }.launchIn(coroutineScope)
        }
        eventFlow.onEach { event ->
            when (event) {
                BookshelfEditorViewModelEvent.Complete -> {
                    events.tryEmit(BookshelfEditorScreenEvent.Complete)
                }

                is BookshelfEditorViewModelEvent.RegisterError -> {
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

    override fun onSubmit(form: BookshelfEditorForm) {
        uiState = uiState.copy(progress = true)
        submit(form)
    }
}
