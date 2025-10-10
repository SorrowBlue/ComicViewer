package com.sorrowblue.comicviewer.feature.collection.editor.basic

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.sorrowblue.comicviewer.domain.model.collection.BasicCollection
import com.sorrowblue.comicviewer.domain.model.collection.CollectionFile
import com.sorrowblue.comicviewer.domain.model.dataOrNull
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.fold
import com.sorrowblue.comicviewer.domain.usecase.collection.GetCollectionUseCase
import com.sorrowblue.comicviewer.domain.usecase.collection.RemoveCollectionFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.collection.UpdateCollectionUseCase
import com.sorrowblue.comicviewer.framework.ui.EventFlow
import com.sorrowblue.comicviewer.framework.ui.kSerializableSaver
import io.github.takahirom.rin.rememberRetained
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
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
    route: BasicCollectionEdit,
    scope: CoroutineScope = rememberCoroutineScope(),
    getCollectionUseCase: GetCollectionUseCase = koinInject(),
    updateCollectionUseCase: UpdateCollectionUseCase = koinInject(),
    removeCollectionFileUseCase: RemoveCollectionFileUseCase = koinInject(),
    viewModel: BasicCollectionEditViewModel = koinViewModel { parametersOf(route) },
): BasicCollectionEditScreenState = rememberRetained {
    BasicCollectionEditScreenStateImpl(
        scope = scope,
        route = route,
        getCollectionUseCase = getCollectionUseCase,
        updateCollectionUseCase = updateCollectionUseCase,
        removeCollectionFileUseCase = removeCollectionFileUseCase,
    )
}.apply {
    this.formState =
        rememberFormState(initialValue = BasicCollectionForm(), saver = kSerializableSaver())
    this.form = rememberForm(state = this.formState, onSubmit = ::onSubmit)
    this.scope = scope
    this.lazyPagingItems = viewModel.pagingDataFlow.collectAsLazyPagingItems()
}

@OptIn(SavedStateHandleSaveableApi::class)
@Stable
private class BasicCollectionEditScreenStateImpl(
    var scope: CoroutineScope,
    private val route: BasicCollectionEdit,
    private val getCollectionUseCase: GetCollectionUseCase,
    private val updateCollectionUseCase: UpdateCollectionUseCase,
    private val removeCollectionFileUseCase: RemoveCollectionFileUseCase,
) : BasicCollectionEditScreenState {

    override var uiState by mutableStateOf(BasicCollectionEditScreenUiState())
        private set
    private var initialForm = BasicCollectionForm()
    lateinit var formState: FormState<BasicCollectionForm>
    override lateinit var form: Form<BasicCollectionForm>
    override lateinit var lazyPagingItems: LazyPagingItems<File>

    override val events = EventFlow<BasicCollectionEditScreenStateEvent>()

    init {
        scope.launch {
            uiState = uiState.copy(isLoading = true)
            getCollectionUseCase(GetCollectionUseCase.Request(route.id))
                .mapNotNull { it.dataOrNull() as? BasicCollection }
                .first().let {
                    initialForm = BasicCollectionForm(name = it.name)
                    formState.reset(initialForm)
                    uiState = uiState.copy(isLoading = false)
                }
        }
    }

    override fun onDeleteClick(file: File) {
        scope.launch {
            removeCollectionFileUseCase(
                RemoveCollectionFileUseCase.Request(
                    CollectionFile(
                        route.id,
                        file.bookshelfId,
                        file.path
                    )
                )
            )
        }
    }

    override fun onSubmit(formData: BasicCollectionForm) {
        scope.launch {
            uiState = uiState.copy(isLoading = true)
            val collection = getCollectionUseCase(GetCollectionUseCase.Request(route.id))
                .mapNotNull { it.dataOrNull() as? BasicCollection }
                .first()
            updateCollectionUseCase(UpdateCollectionUseCase.Request(collection.copy(name = formData.name))).fold(
                onSuccess = {},
                onError = {}
            )
            events.tryEmit(BasicCollectionEditScreenStateEvent.EditComplete)
        }
    }
}
