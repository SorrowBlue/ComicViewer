package com.sorrowblue.comicviewer.feature.collection.editor.basic

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.paging.PagingConfig
import androidx.paging.compose.LazyPagingItems
import com.sorrowblue.comicviewer.domain.model.collection.BasicCollection
import com.sorrowblue.comicviewer.domain.model.collection.CollectionFile
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.domain.model.dataOrNull
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.fold
import com.sorrowblue.comicviewer.domain.usecase.collection.GetCollectionUseCase
import com.sorrowblue.comicviewer.domain.usecase.collection.PagingCollectionFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.collection.RemoveCollectionFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.collection.UpdateCollectionUseCase
import com.sorrowblue.comicviewer.framework.ui.EventFlow
import com.sorrowblue.comicviewer.framework.ui.kSerializableSaver
import com.sorrowblue.comicviewer.framework.ui.paging.rememberPagingItems
import io.github.takahirom.rin.rememberRetained
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
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
context(context: BasicCollectionEditScreenContext)
internal fun rememberBasicCollectionEditScreenState(
    collectionId: CollectionId,
): BasicCollectionEditScreenState {
    val coroutineScope = rememberCoroutineScope()
    val formState =
        rememberFormState(initialValue = BasicCollectionForm(), saver = kSerializableSaver())
    val lazyPagingItems = rememberPagingItems {
        context.pagingCollectionFileUseCase(
            PagingCollectionFileUseCase.Request(PagingConfig(PageSize), collectionId),
        )
    }
    return rememberRetained {
        BasicCollectionEditScreenStateImpl(
            collectionId = collectionId,
            getCollectionUseCase = context.getCollectionUseCase,
            updateCollectionUseCase = context.updateCollectionUseCase,
            removeCollectionFileUseCase = context.removeCollectionFileUseCase,
            formState = formState,
            coroutineScope = coroutineScope,
            lazyPagingItems = lazyPagingItems,
        )
    }.apply {
        this.form = rememberForm(state = formState, onSubmit = ::onSubmit)
    }
}

private const val PageSize = 20

@Stable
private class BasicCollectionEditScreenStateImpl(
    private val collectionId: CollectionId,
    private val getCollectionUseCase: GetCollectionUseCase,
    private val updateCollectionUseCase: UpdateCollectionUseCase,
    private val removeCollectionFileUseCase: RemoveCollectionFileUseCase,
    private val formState: FormState<BasicCollectionForm>,
    private val coroutineScope: CoroutineScope,
    override val lazyPagingItems: LazyPagingItems<File>,
) : BasicCollectionEditScreenState {
    override var uiState by mutableStateOf(BasicCollectionEditScreenUiState())
        private set
    override lateinit var form: Form<BasicCollectionForm>

    override val events = EventFlow<BasicCollectionEditScreenStateEvent>()

    init {
        coroutineScope.launch {
            uiState = uiState.copy(isLoading = true)
            getCollectionUseCase(GetCollectionUseCase.Request(collectionId))
                .mapNotNull { it.dataOrNull() as? BasicCollection }
                .first()
                .let {
                    formState.reset(BasicCollectionForm(name = it.name))
                    uiState = uiState.copy(isLoading = false)
                }
        }
    }

    override fun onDeleteClick(file: File) {
        coroutineScope.launch {
            removeCollectionFileUseCase(
                RemoveCollectionFileUseCase.Request(
                    CollectionFile(
                        collectionId,
                        file.bookshelfId,
                        file.path,
                    ),
                ),
            )
        }
    }

    override fun onSubmit(formData: BasicCollectionForm) {
        coroutineScope.launch {
            uiState = uiState.copy(isLoading = true)
            val collection = getCollectionUseCase(GetCollectionUseCase.Request(collectionId))
                .mapNotNull { it.dataOrNull() as? BasicCollection }
                .first()
            updateCollectionUseCase(
                UpdateCollectionUseCase.Request(collection.copy(name = formData.name)),
            ).fold(
                onSuccess = {},
                onError = {},
            )
            events.tryEmit(BasicCollectionEditScreenStateEvent.EditComplete)
        }
    }
}
