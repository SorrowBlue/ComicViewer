package com.sorrowblue.comicviewer.feature.collection.editor.smart

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.sorrowblue.comicviewer.domain.EmptyRequest
import com.sorrowblue.comicviewer.domain.model.SearchCondition
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.collection.SmartCollection
import com.sorrowblue.comicviewer.domain.model.fold
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.FlowBookshelfListUseCase
import com.sorrowblue.comicviewer.domain.usecase.collection.CreateCollectionUseCase
import com.sorrowblue.comicviewer.feature.collection.editor.smart.component.BookshelfField
import com.sorrowblue.comicviewer.feature.collection.editor.smart.section.SmartCollectionForm
import com.sorrowblue.comicviewer.framework.ui.EventFlow
import com.sorrowblue.comicviewer.framework.ui.kSerializableSaver
import comicviewer.feature.collection.editor.generated.resources.Res
import comicviewer.feature.collection.editor.generated.resources.collection_editor_error_not_get_bookshelf
import comicviewer.feature.collection.editor.generated.resources.collection_editor_label_all_bookshelf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import soil.form.FieldError
import soil.form.FormOptions
import soil.form.compose.Form
import soil.form.compose.FormPolicy
import soil.form.compose.FormState
import soil.form.compose.rememberForm
import soil.form.compose.rememberFormState

@Composable
context(context: SmartCollectionCreateScreenContext)
internal fun rememberSmartCollectionCreateScreenState(
    bookshelfId: BookshelfId?,
    searchCondition: SearchCondition,
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
    return remember {
        SmartCollectionCreateScreenStateImpl(
            flowBookshelfListUseCase = context.flowBookshelfListUseCase,
            createCollectionUseCase = context.createCollectionUseCase,
            formState = formState,
            coroutineScope = coroutineScope,
        )
    }.apply {
        form = rememberForm(state = formState, onSubmit = ::onSubmit)
    }
}

private class SmartCollectionCreateScreenStateImpl(
    flowBookshelfListUseCase: FlowBookshelfListUseCase,
    private val createCollectionUseCase: CreateCollectionUseCase,
    private val formState: FormState<SmartCollectionForm>,
    private val coroutineScope: CoroutineScope,
) : SmartCollectionEditorScreenState {
    override lateinit var form: Form<SmartCollectionForm>
    override val event = EventFlow<SmartCollectionEditorScreenStateEvent>()
    override var uiState by mutableStateOf(SmartCollectionEditorScreenUiState())

    init {
        coroutineScope.launch {
            uiState = uiState.copy(enabledForm = false)
            flowBookshelfListUseCase(EmptyRequest).first().fold(
                onSuccess = { list ->
                    uiState = uiState.copy(
                        bookshelf = buildMap {
                            put(null, getString(Res.string.collection_editor_label_all_bookshelf))
                            putAll(list.map { it.id to it.displayName })
                        },
                    )
                },
                onError = {
                    formState.setError(
                        BookshelfField to FieldError(
                            getString(Res.string.collection_editor_error_not_get_bookshelf),
                        ),
                    )
                },
            )
            uiState = uiState.copy(enabledForm = true)
        }
    }

    override fun onSubmit(formData: SmartCollectionForm) {
        coroutineScope.launch {
            createCollectionUseCase(
                CreateCollectionUseCase.Request(
                    SmartCollection(
                        formData.name,
                        formData.bookshelfId,
                        formData.searchCondition,
                    ),
                ),
            )
            event.emit(SmartCollectionEditorScreenStateEvent.Complete)
        }
    }
}
