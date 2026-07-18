/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.collection.editor.smart

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.domain.model.collection.SmartCollection
import com.sorrowblue.comicviewer.feature.collection.editor.smart.component.BookshelfField
import com.sorrowblue.comicviewer.feature.collection.editor.smart.section.SmartCollectionForm
import com.sorrowblue.comicviewer.framework.ui.EventFlow
import com.sorrowblue.comicviewer.framework.ui.kSerializableSaver
import comicviewer.feature.collection.editor.generated.resources.Res
import comicviewer.feature.collection.editor.generated.resources.collection_editor_error_not_get_bookshelf
import comicviewer.feature.collection.editor.generated.resources.collection_editor_label_all_bookshelf
import dev.zacsweers.metrox.viewmodel.assistedMetroViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import soil.form.FieldError
import soil.form.compose.Form
import soil.form.compose.FormState
import soil.form.compose.rememberForm
import soil.form.compose.rememberFormState

@Composable
internal fun rememberSmartCollectionEditScreenState(
    collectionId: CollectionId,
    viewModel: SmartCollectionEditViewModel =
        assistedMetroViewModel<SmartCollectionEditViewModel, SmartCollectionEditViewModel.Factory> {
            create(collectionId)
        },
): SmartCollectionEditorScreenState {
    val coroutineScope = rememberCoroutineScope()
    val formState =
        rememberFormState(initialValue = SmartCollectionForm(), saver = kSerializableSaver())
    return rememberSaveable(
        saver = SmartCollectionEditScreenImpl.saver(
            coroutineScope = coroutineScope,
            formState = formState,
            collectionFlow = viewModel.collectionFlow,
            bookshelfListFlow = viewModel.bookshelfListFlow,
            eventFlow = viewModel.event,
            submit = viewModel::submit,
        ),
    ) {
        SmartCollectionEditScreenImpl(
            isDataLoaded = false,
            coroutineScope = coroutineScope,
            formState = formState,
            collectionFlow = viewModel.collectionFlow,
            bookshelfListFlow = viewModel.bookshelfListFlow,
            eventFlow = viewModel.event,
            submit = viewModel::submit,
        )
    }.apply {
        form = rememberForm(formState, ::onSubmit)
    }
}

private class SmartCollectionEditScreenImpl(
    var isDataLoaded: Boolean,
    private val coroutineScope: CoroutineScope,
    private val formState: FormState<SmartCollectionForm>,
    collectionFlow: SharedFlow<SmartCollection?>,
    bookshelfListFlow: SharedFlow<List<Bookshelf>?>,
    eventFlow: SharedFlow<SmartCollectionEditViewModelEvent>,
    private val submit: (SmartCollectionForm) -> Unit,
) : SmartCollectionEditorScreenState {
    companion object {
        fun saver(
            coroutineScope: CoroutineScope,
            formState: FormState<SmartCollectionForm>,
            collectionFlow: SharedFlow<SmartCollection?>,
            bookshelfListFlow: SharedFlow<List<Bookshelf>?>,
            eventFlow: SharedFlow<SmartCollectionEditViewModelEvent>,
            submit: (SmartCollectionForm) -> Unit,
        ) = androidx.compose.runtime.saveable.Saver<SmartCollectionEditScreenImpl, Boolean>(
            save = { it.isDataLoaded },
            restore = {
                SmartCollectionEditScreenImpl(
                    isDataLoaded = it,
                    coroutineScope = coroutineScope,
                    formState = formState,
                    collectionFlow = collectionFlow,
                    bookshelfListFlow = bookshelfListFlow,
                    eventFlow = eventFlow,
                    submit = submit,
                )
            },
        )
    }

    override lateinit var form: Form<SmartCollectionForm>

    override val event = EventFlow<SmartCollectionEditorScreenStateEvent>()

    override var uiState by mutableStateOf(SmartCollectionEditorScreenUiState())

    init {
        if (!isDataLoaded) {
            uiState = uiState.copy(enabledForm = false)
            coroutineScope.launch {
                collectionFlow.first()?.let { collection ->
                    isDataLoaded = true
                    formState.reset(
                        SmartCollectionForm(
                            name = collection.name,
                            bookshelfId = collection.bookshelfId ?: BookshelfId(),
                            searchCondition = collection.searchCondition,
                        ),
                    )
                }
                bookshelfListFlow.first()?.let { list ->
                    uiState = uiState.copy(
                        bookshelf = buildMap {
                            put(
                                null,
                                getString(Res.string.collection_editor_label_all_bookshelf),
                            )
                            putAll(list.map { it.id to it.displayName })
                        },
                    )
                } ?: run {
                    formState.setError(
                        BookshelfField to FieldError(
                            getString(Res.string.collection_editor_error_not_get_bookshelf),
                        ),
                    )
                }
                uiState = uiState.copy(enabledForm = true)
            }
        }
        eventFlow.onEach { event ->
            when (event) {
                SmartCollectionEditViewModelEvent.Complete -> {
                    this.event.emit(SmartCollectionEditorScreenStateEvent.Complete)
                }
            }
        }.launchIn(coroutineScope)
    }

    override fun onSubmit(formData: SmartCollectionForm) {
        uiState = uiState.copy(enabledForm = false)
        submit(formData)
    }
}
