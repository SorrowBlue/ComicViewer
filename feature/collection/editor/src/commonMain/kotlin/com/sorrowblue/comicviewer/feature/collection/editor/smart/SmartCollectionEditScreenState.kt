package com.sorrowblue.comicviewer.feature.collection.editor.smart

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.sorrowblue.comicviewer.domain.EmptyRequest
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.collection.SmartCollection
import com.sorrowblue.comicviewer.domain.model.dataOrNull
import com.sorrowblue.comicviewer.domain.model.fold
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.FlowBookshelfListUseCase
import com.sorrowblue.comicviewer.domain.usecase.collection.GetCollectionUseCase
import com.sorrowblue.comicviewer.domain.usecase.collection.UpdateCollectionUseCase
import com.sorrowblue.comicviewer.feature.collection.editor.smart.component.BookshelfField
import com.sorrowblue.comicviewer.feature.collection.editor.smart.section.SmartCollectionForm
import com.sorrowblue.comicviewer.framework.ui.EventFlow
import com.sorrowblue.comicviewer.framework.ui.kSerializableSaver
import comicviewer.feature.collection.editor.generated.resources.Res
import comicviewer.feature.collection.editor.generated.resources.collection_editor_error_not_get_bookshelf
import comicviewer.feature.collection.editor.generated.resources.collection_editor_label_all_bookshelf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.koin.compose.koinInject
import soil.form.FieldError
import soil.form.compose.Form
import soil.form.compose.FormState
import soil.form.compose.rememberForm
import soil.form.compose.rememberFormState

@Composable
internal fun rememberSmartCollectionEditScreenState(
    route: SmartCollectionEdit,
    scope: CoroutineScope = rememberCoroutineScope(),
    flowBookshelfListUseCase: FlowBookshelfListUseCase = koinInject(),
    updateCollectionUseCase: UpdateCollectionUseCase = koinInject(),
    getCollectionUseCase: GetCollectionUseCase = koinInject(),
): SmartCollectionEditorScreenState {
    val formState =
        rememberFormState(initialValue = SmartCollectionForm(), saver = kSerializableSaver())
    return rememberSaveable(
        saver = SmartCollectionEditScreenImpl.saver(
            route = route,
            scope = scope,
            formState = formState,
            flowBookshelfListUseCase = flowBookshelfListUseCase,
            getCollectionUseCase = getCollectionUseCase,
            updateCollectionUseCase = updateCollectionUseCase
        )
    ) {
        SmartCollectionEditScreenImpl(
            isDataLoaded = false,
            route = route,
            coroutineScope = scope,
            formState = formState,
            flowBookshelfListUseCase = flowBookshelfListUseCase,
            updateCollectionUseCase = updateCollectionUseCase,
            getCollectionUseCase = getCollectionUseCase,
        )
    }.apply {
        this.formState = formState
        this.form = rememberForm(formState, ::onSubmit)
        this.coroutineScope = scope
    }
}

private class SmartCollectionEditScreenImpl(
    var isDataLoaded: Boolean,
    private val route: SmartCollectionEdit,
    flowBookshelfListUseCase: FlowBookshelfListUseCase,
    private val getCollectionUseCase: GetCollectionUseCase,
    var coroutineScope: CoroutineScope,
    var formState: FormState<SmartCollectionForm>,
    private val updateCollectionUseCase: UpdateCollectionUseCase,
) : SmartCollectionEditorScreenState {
    companion object {

        fun saver(
            route: SmartCollectionEdit,
            scope: CoroutineScope,
            formState: FormState<SmartCollectionForm>,
            flowBookshelfListUseCase: FlowBookshelfListUseCase,
            getCollectionUseCase: GetCollectionUseCase,
            updateCollectionUseCase: UpdateCollectionUseCase,
        ) = androidx.compose.runtime.saveable.Saver<SmartCollectionEditScreenImpl, Boolean>(
            save = { it.isDataLoaded },
            restore = {
                SmartCollectionEditScreenImpl(
                    isDataLoaded = it,
                    route = route,
                    coroutineScope = scope,
                    flowBookshelfListUseCase = flowBookshelfListUseCase,
                    getCollectionUseCase = getCollectionUseCase,
                    updateCollectionUseCase = updateCollectionUseCase,
                    formState = formState
                )
            }
        )
    }

    override lateinit var form: Form<SmartCollectionForm>

    override val event = EventFlow<SmartCollectionEditorScreenStateEvent>()

    override var uiState by mutableStateOf(SmartCollectionEditorScreenUiState())

    init {
        if (!isDataLoaded) {
            coroutineScope.launch {
                uiState = uiState.copy(enabledForm = false)
                getCollectionUseCase(GetCollectionUseCase.Request(route.collectionId))
                    .first().fold(
                        onSuccess = { collection ->
                            require(collection is SmartCollection)
                            isDataLoaded = true
                            formState.reset(
                                SmartCollectionForm(
                                    name = collection.name,
                                    bookshelfId = collection.bookshelfId ?: BookshelfId(),
                                    searchCondition = collection.searchCondition,
                                )
                            )
                        },
                        onError = {}
                    )
                flowBookshelfListUseCase(EmptyRequest).first().fold(
                    onSuccess = { list ->
                        uiState = uiState.copy(
                            bookshelf = buildMap {
                                put(
                                    null,
                                    getString(Res.string.collection_editor_label_all_bookshelf)
                                )
                                putAll(list.map { it.id to it.displayName })
                            }
                        )
                    },
                    onError = {
                        formState.setError(
                            BookshelfField to FieldError(getString(Res.string.collection_editor_error_not_get_bookshelf))
                        )
                    }
                )
                uiState = uiState.copy(enabledForm = true)
            }
        }
    }

    override fun onSubmit(formData: SmartCollectionForm) {
        coroutineScope.launch {
            uiState = uiState.copy(enabledForm = false)
            delay(1000)
            val collection =
                getCollectionUseCase(GetCollectionUseCase.Request(route.collectionId)).first()
                    .dataOrNull() as SmartCollection
            updateCollectionUseCase(
                UpdateCollectionUseCase.Request(
                    collection.copy(
                        name = formData.name,
                        bookshelfId = if (formData.bookshelfId == BookshelfId()) null else formData.bookshelfId,
                        searchCondition = formData.searchCondition
                    )
                )
            ).fold(
                onSuccess = {
                    event.emit(SmartCollectionEditorScreenStateEvent.Complete)
                },
                onError = {}
            )
        }
    }
}
