/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.collection.editor.basic

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.framework.ui.AppState
import com.sorrowblue.comicviewer.framework.ui.EventFlow
import com.sorrowblue.comicviewer.framework.ui.LocalAppState
import com.sorrowblue.comicviewer.framework.ui.kSerializableSaver
import comicviewer.feature.collection.editor.generated.resources.Res
import comicviewer.feature.collection.editor.generated.resources.collection_editor_msg_success_create
import comicviewer.feature.collection.editor.generated.resources.collection_editor_msg_success_create_add
import dev.zacsweers.metrox.viewmodel.assistedMetroViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.jetbrains.compose.resources.getString
import soil.form.compose.Form
import soil.form.compose.rememberForm
import soil.form.compose.rememberFormState

internal sealed interface BasicCollectionCreateScreenStateEvent {
    data object CreateComplete : BasicCollectionCreateScreenStateEvent
}

internal interface BasicCollectionCreateScreenState {
    val uiState: BasicCollectionsCreateScreenUiState
    val form: Form<BasicCollectionForm>
    val event: EventFlow<BasicCollectionCreateScreenStateEvent>

    fun onSubmit(formData: BasicCollectionForm)
}

@Composable
internal fun rememberBasicCollectionCreateScreenState(
    bookshelfId: BookshelfId,
    path: String,
    viewModel: BasicCollectionCreateViewModel =
        assistedMetroViewModel<BasicCollectionCreateViewModel, BasicCollectionCreateViewModel.Factory> {
            create(bookshelfId, path)
        },
): BasicCollectionCreateScreenState {
    val appState = LocalAppState.current
    val coroutineScope = rememberCoroutineScope()
    val formState =
        rememberFormState(initialValue = BasicCollectionForm(), saver = kSerializableSaver())
    return remember(viewModel) {
        BasicCollectionCreateScreenStateImpl(
            appState = appState,
            coroutineScope = coroutineScope,
            eventFlow = viewModel.event,
            submitForm = viewModel::submitForm,
        )
    }.apply {
        form = rememberForm(state = formState, onSubmit = ::onSubmit)
    }
}

private class BasicCollectionCreateScreenStateImpl(
    private val appState: AppState,
    coroutineScope: CoroutineScope,
    eventFlow: SharedFlow<BasicCollectionCreateViewModelEvent>,
    private val submitForm: (BasicCollectionForm) -> Unit,
) : BasicCollectionCreateScreenState {
    override lateinit var form: Form<BasicCollectionForm>

    override val event = EventFlow<BasicCollectionCreateScreenStateEvent>()

    override val uiState by mutableStateOf(BasicCollectionsCreateScreenUiState())

    init {
        eventFlow.onEach {
            when (it) {
                is BasicCollectionCreateViewModelEvent.CreateSuccess -> {
                    appState.snackbarHostState.showSnackbar(
                        getString(
                            Res.string.collection_editor_msg_success_create,
                            it.name,
                        ),
                    )
                    event.tryEmit(BasicCollectionCreateScreenStateEvent.CreateComplete)
                }

                is BasicCollectionCreateViewModelEvent.CreateAddSuccess -> {
                    event.tryEmit(BasicCollectionCreateScreenStateEvent.CreateComplete)
                    appState.snackbarHostState.showSnackbar(
                        getString(
                            Res.string.collection_editor_msg_success_create_add,
                            it.name,
                        ),
                    )
                }
            }
        }
            .launchIn(coroutineScope)
    }

    override fun onSubmit(formData: BasicCollectionForm) {
        submitForm(formData)
    }
}
