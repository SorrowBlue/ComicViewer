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
import com.sorrowblue.comicviewer.framework.ui.EventFlow
import com.sorrowblue.comicviewer.framework.ui.NotificationManager
import com.sorrowblue.comicviewer.framework.ui.kSerializableSaver
import comicviewer.feature.collection.editor.generated.resources.Res
import comicviewer.feature.collection.editor.generated.resources.collection_editor_msg_success_create
import comicviewer.feature.collection.editor.generated.resources.collection_editor_msg_success_create_add
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.koin.compose.koinInject
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
    route: BasicCollectionCreate,
    createCollectionUseCase: CreateCollectionUseCase = koinInject(),
    addCollectionFileUseCase: AddCollectionFileUseCase = koinInject(),
    notificationManager: NotificationManager = koinInject(),
    scope: CoroutineScope = rememberCoroutineScope(),
): BasicCollectionCreateScreenState {
    return remember {
        BasicCollectionCreateScreenStateImpl(
            route = route,
            createCollectionUseCase = createCollectionUseCase,
            addCollectionFileUseCase = addCollectionFileUseCase,
            notificationManager = notificationManager,
            scope = scope
        )
    }.apply {
        val formState =
            rememberFormState(initialValue = BasicCollectionForm(), saver = kSerializableSaver())
        this.form = rememberForm(state = formState, onSubmit = ::onSubmit)
        this.scope = scope
    }
}

private class BasicCollectionCreateScreenStateImpl(
    var scope: CoroutineScope,
    private val route: BasicCollectionCreate,
    private val createCollectionUseCase: CreateCollectionUseCase,
    private val addCollectionFileUseCase: AddCollectionFileUseCase,
    private val notificationManager: NotificationManager,
) : BasicCollectionCreateScreenState {

    override lateinit var form: Form<BasicCollectionForm>

    override val event = EventFlow<BasicCollectionCreateScreenStateEvent>()

    override val uiState by mutableStateOf(BasicCollectionsCreateScreenUiState())

    override fun onSubmit(formData: BasicCollectionForm) {
        scope.launch {
            delay(300)
            createCollectionUseCase(CreateCollectionUseCase.Request(BasicCollection(formData.name)))
                .fold(
                    onSuccess = { collection ->
                        if (route.bookshelfId != BookshelfId() && route.path.isNotEmpty()) {
                            addCollectionFile(
                                collection = collection,
                                bookshelfId = route.bookshelfId,
                                path = route.path
                            )
                        } else {
                            notificationManager.toast(
                                getString(
                                    Res.string.collection_editor_msg_success_create,
                                    collection.name
                                ),
                                NotificationManager.LENGTH_SHORT
                            )
                            event.tryEmit(BasicCollectionCreateScreenStateEvent.CreateComplete)
                        }
                    },
                    onError = {}
                )
        }
    }

    private suspend fun addCollectionFile(
        collection: Collection,
        bookshelfId: BookshelfId,
        path: String,
    ) {
        addCollectionFileUseCase(
            AddCollectionFileUseCase.Request(CollectionFile(collection.id, bookshelfId, path))
        ).fold(
            onSuccess = {
                event.tryEmit(BasicCollectionCreateScreenStateEvent.CreateComplete)
                notificationManager.toast(
                    getString(
                        Res.string.collection_editor_msg_success_create_add,
                        collection.name
                    ),
                    NotificationManager.LENGTH_SHORT
                )
            },
            onError = {}
        )
    }
}
