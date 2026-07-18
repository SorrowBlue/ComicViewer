/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.collection.editor.basic

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.sorrowblue.comicviewer.domain.model.collection.BasicCollection
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.framework.ui.EventFlow
import com.sorrowblue.comicviewer.framework.ui.kSerializableSaver
import dev.zacsweers.metrox.viewmodel.assistedMetroViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import soil.form.compose.Form
import soil.form.compose.FormState
import soil.form.compose.rememberForm
import soil.form.compose.rememberFormState

internal sealed interface BasicCollectionEditScreenStateEvent {
    data object EditComplete : BasicCollectionEditScreenStateEvent
}

internal interface BasicCollectionEditScreenState {
    val uiState: BasicCollectionEditScreenUiState
    val form: Form<BasicCollectionForm>
    val events: EventFlow<BasicCollectionEditScreenStateEvent>
    val lazyPagingItems: LazyPagingItems<File>

    fun onSubmit(formData: BasicCollectionForm)

    fun onDeleteClick(file: File)
}

@Composable
internal fun rememberBasicCollectionEditScreenState(
    collectionId: CollectionId,
    viewModel: BasicCollectionEditViewModel =
        assistedMetroViewModel<BasicCollectionEditViewModel, BasicCollectionEditViewModel.Factory> {
            create(collectionId)
        },
): BasicCollectionEditScreenState {
    val coroutineScope = rememberCoroutineScope()
    val formState =
        rememberFormState(initialValue = BasicCollectionForm(), saver = kSerializableSaver())
    return remember(viewModel) {
        BasicCollectionEditScreenStateImpl(
            coroutineScope = coroutineScope,
            formState = formState,
            collectionFlow = viewModel.collectionFlow,
            eventFlow = viewModel.event,
            delete = viewModel::onDeleteClick,
            submit = viewModel::onSubmit,
        )
    }.apply {
        lazyPagingItems = viewModel.pagingDataFlow.collectAsLazyPagingItems()
        form = rememberForm(state = formState, onSubmit = ::onSubmit)
    }
}

@Stable
private class BasicCollectionEditScreenStateImpl(
    coroutineScope: CoroutineScope,
    private val formState: FormState<BasicCollectionForm>,
    collectionFlow: SharedFlow<BasicCollection>,
    eventFlow: SharedFlow<BasicCollectionEditViewModelEvent>,
    private val delete: (File) -> Unit,
    private val submit: (BasicCollectionForm) -> Unit,
) : BasicCollectionEditScreenState {
    override var uiState by mutableStateOf(BasicCollectionEditScreenUiState())
        private set
    override lateinit var form: Form<BasicCollectionForm>
    override lateinit var lazyPagingItems: LazyPagingItems<File>

    override val events = EventFlow<BasicCollectionEditScreenStateEvent>()

    init {
        uiState = uiState.copy(isLoading = true)
        collectionFlow.onEach { collection ->
            formState.reset(BasicCollectionForm(name = collection.name))
            uiState = uiState.copy(isLoading = false)
        }.launchIn(coroutineScope)
        eventFlow.onEach { event ->
            when (event) {
                BasicCollectionEditViewModelEvent.EditComplete -> {
                    events.tryEmit(BasicCollectionEditScreenStateEvent.EditComplete)
                }
            }
        }
            .launchIn(coroutineScope)
    }

    override fun onDeleteClick(file: File) {
        delete(file)
    }

    override fun onSubmit(formData: BasicCollectionForm) {
        uiState = uiState.copy(isLoading = true)
        submit(formData)
    }
}
