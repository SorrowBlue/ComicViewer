package com.sorrowblue.comicviewer.feature.collection.editor.basic

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.collection.BasicCollection
import com.sorrowblue.comicviewer.domain.model.collection.Collection
import com.sorrowblue.comicviewer.domain.model.collection.CollectionFile
import com.sorrowblue.comicviewer.domain.model.fold
import com.sorrowblue.comicviewer.domain.usecase.collection.AddCollectionFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.collection.CreateCollectionUseCase
import com.sorrowblue.comicviewer.framework.ui.AppState
import com.sorrowblue.comicviewer.framework.ui.EventFlow
import com.sorrowblue.comicviewer.framework.ui.LocalAppState
import com.sorrowblue.comicviewer.framework.ui.kSerializableSaver
import comicviewer.feature.collection.editor.generated.resources.Res
import comicviewer.feature.collection.editor.generated.resources.collection_editor_msg_success_create
import comicviewer.feature.collection.editor.generated.resources.collection_editor_msg_success_create_add
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
context(context: BasicCollectionCreateScreenContext)
internal fun rememberBasicCollectionCreateScreenState(
    bookshelfId: BookshelfId,
    path: String,
): BasicCollectionCreateScreenState {
    val appState = LocalAppState.current
    val coroutineScope = rememberCoroutineScope()
    val formState =
        rememberFormState(initialValue = BasicCollectionForm(), saver = kSerializableSaver())
    return remember {
        BasicCollectionCreateScreenStateImpl(
            bookshelfId = bookshelfId,
            path = path,
            createCollectionUseCase = context.createCollectionUseCase,
            addCollectionFileUseCase = context.addCollectionFileUseCase,
            appState = appState,
            coroutineScope = coroutineScope,
        )
    }.apply {
        form = rememberForm(state = formState, onSubmit = ::onSubmit)
    }
}

private class BasicCollectionCreateScreenStateImpl(
    private val bookshelfId: BookshelfId,
    private val path: String,
    private val createCollectionUseCase: CreateCollectionUseCase,
    private val addCollectionFileUseCase: AddCollectionFileUseCase,
    private val appState: AppState,
    private val coroutineScope: CoroutineScope,
) : BasicCollectionCreateScreenState {
    override lateinit var form: Form<BasicCollectionForm>

    override val event = EventFlow<BasicCollectionCreateScreenStateEvent>()

    override val uiState by mutableStateOf(BasicCollectionsCreateScreenUiState())

    override fun onSubmit(formData: BasicCollectionForm) {
        coroutineScope.launch {
            createCollectionUseCase(CreateCollectionUseCase.Request(BasicCollection(formData.name)))
                .fold(
                    onSuccess = { collection ->
                        if (bookshelfId != BookshelfId() && path.isNotEmpty()) {
                            addCollectionFile(
                                collection = collection,
                                bookshelfId = bookshelfId,
                                path = path,
                            )
                        } else {
                            appState.snackbarHostState.showSnackbar(
                                getString(
                                    Res.string.collection_editor_msg_success_create,
                                    collection.name,
                                ),
                            )
                            event.tryEmit(BasicCollectionCreateScreenStateEvent.CreateComplete)
                        }
                    },
                    onError = {},
                )
        }
    }

    private suspend fun addCollectionFile(
        collection: Collection,
        bookshelfId: BookshelfId,
        path: String,
    ) {
        addCollectionFileUseCase(
            AddCollectionFileUseCase.Request(CollectionFile(collection.id, bookshelfId, path)),
        ).fold(
            onSuccess = {
                event.tryEmit(BasicCollectionCreateScreenStateEvent.CreateComplete)
                withContext(Dispatchers.Main) {
                    appState.snackbarHostState.showSnackbar(
                        getString(
                            Res.string.collection_editor_msg_success_create_add,
                            collection.name,
                        ),
                    )
                }
            },
            onError = {},
        )
    }
}
