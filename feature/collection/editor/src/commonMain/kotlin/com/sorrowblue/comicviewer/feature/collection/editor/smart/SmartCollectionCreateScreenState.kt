/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.collection.editor.smart

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.sorrowblue.comicviewer.domain.model.SearchCondition
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.feature.collection.editor.smart.component.BookshelfField
import com.sorrowblue.comicviewer.feature.collection.editor.smart.section.SmartCollectionForm
import com.sorrowblue.comicviewer.framework.ui.EventFlow
import com.sorrowblue.comicviewer.framework.ui.kSerializableSaver
import comicviewer.feature.collection.editor.generated.resources.Res
import comicviewer.feature.collection.editor.generated.resources.collection_editor_error_not_get_bookshelf
import comicviewer.feature.collection.editor.generated.resources.collection_editor_label_all_bookshelf
import dev.zacsweers.metrox.viewmodel.metroViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.jetbrains.compose.resources.getString
import soil.form.FieldError
import soil.form.FormOptions
import soil.form.compose.Form
import soil.form.compose.FormPolicy
import soil.form.compose.FormState
import soil.form.compose.rememberForm
import soil.form.compose.rememberFormState

@Composable
internal fun rememberSmartCollectionCreateScreenState(
    bookshelfId: BookshelfId?,
    searchCondition: SearchCondition,
    viewModel: SmartCollectionCreateViewModel = metroViewModel(),
): SmartCollectionEditorScreenState {
    val coroutineScope = rememberCoroutineScope()
    val formState = rememberFormState(
        initialValue = SmartCollectionForm(
            bookshelfId = bookshelfId,
            searchCondition = searchCondition,
        ),
        saver = kSerializableSaver(),
        policy = FormPolicy(FormOptions(false)),
    )
    return remember(viewModel) {
        SmartCollectionCreateScreenStateImpl(
            coroutineScope = coroutineScope,
            formState = formState,
            bookshelfListFlow = viewModel.bookshelfListFlow,
            eventFlow = viewModel.event,
            submit = viewModel::onSubmit,
        )
    }.apply {
        form = rememberForm(state = formState, onSubmit = ::onSubmit)
    }
}

private class SmartCollectionCreateScreenStateImpl(
    coroutineScope: CoroutineScope,
    private val formState: FormState<SmartCollectionForm>,
    bookshelfListFlow: SharedFlow<List<Bookshelf>?>,
    eventFlow: SharedFlow<SmartCollectionCreateViewModelEvent>,
    private val submit: (SmartCollectionForm) -> Unit,
) : SmartCollectionEditorScreenState {

    override lateinit var form: Form<SmartCollectionForm>

    override val event = EventFlow<SmartCollectionEditorScreenStateEvent>()

    override var uiState by mutableStateOf(SmartCollectionEditorScreenUiState())

    init {
        uiState = uiState.copy(enabledForm = false)
        bookshelfListFlow.onEach { list ->
            if (list.isNullOrEmpty()) {
                formState.setError(
                    BookshelfField to FieldError(
                        getString(Res.string.collection_editor_error_not_get_bookshelf),
                    ),
                )
                uiState = uiState.copy(enabledForm = true)
            } else {
                uiState = uiState.copy(
                    bookshelf = buildMap {
                        put(null, getString(Res.string.collection_editor_label_all_bookshelf))
                        putAll(list.map { it.id to it.displayName })
                    },
                )
            }
            uiState = uiState.copy(enabledForm = true)
        }.launchIn(coroutineScope)
        eventFlow.onEach {
            when (it) {
                SmartCollectionCreateViewModelEvent.Complete -> {
                    event.emit(SmartCollectionEditorScreenStateEvent.Complete)
                }
            }
        }
            .launchIn(coroutineScope)
    }

    override fun onSubmit(formData: SmartCollectionForm) {
        submit(formData)
    }
}
