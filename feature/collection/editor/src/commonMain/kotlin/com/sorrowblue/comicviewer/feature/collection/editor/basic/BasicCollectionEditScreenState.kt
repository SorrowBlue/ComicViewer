package com.sorrowblue.comicviewer.feature.collection.editor.basic

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.collection.BasicCollection
import com.sorrowblue.comicviewer.domain.model.collection.CollectionFile
import com.sorrowblue.comicviewer.domain.model.dataOrNull
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.usecase.collection.FlowCollectionUseCase
import com.sorrowblue.comicviewer.domain.usecase.collection.RemoveCollectionFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.collection.UpdateCollectionUseCase
import com.sorrowblue.comicviewer.framework.ui.EventFlow
import com.sorrowblue.comicviewer.framework.ui.SaveableScreenState
import com.sorrowblue.comicviewer.framework.ui.rememberSaveableScreenState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

internal sealed interface BasicCollectionEditScreenStateEvent {
    data object EditComplete : BasicCollectionEditScreenStateEvent
}

internal interface BasicCollectionEditScreenState : SaveableScreenState {

    val uiState: BasicCollectionEditScreenUiState
    val events: EventFlow<BasicCollectionEditScreenStateEvent>
    val pagingDataFlow: Flow<PagingData<File>>

    suspend fun onSubmit(formData: BasicCollectionEditorFormData)
    fun onDeleteClick(file: File)
}

@Composable
internal fun rememberBasicCollectionEditScreenState(
    route: BasicCollectionEdit,
    scope: CoroutineScope = rememberCoroutineScope(),
    flowCollectionUseCase: FlowCollectionUseCase = koinInject(),
    updateCollectionUseCase: UpdateCollectionUseCase = koinInject(),
    removeCollectionFileUseCase: RemoveCollectionFileUseCase = koinInject(),
    viewModel: BasicCollectionEditViewModel = koinViewModel { parametersOf(route) },
): BasicCollectionEditScreenState = rememberSaveableScreenState {
    BasicCollectionEditScreenStateImpl(
        savedStateHandle = it,
        scope = scope,
        route = route,
        flowCollectionUseCase = flowCollectionUseCase,
        updateCollectionUseCase = updateCollectionUseCase,
        removeCollectionFileUseCase = removeCollectionFileUseCase,
        pagingDataFlow = viewModel.pagingDataFlow
    )
}

@OptIn(SavedStateHandleSaveableApi::class)
@Stable
private class BasicCollectionEditScreenStateImpl(
    override val savedStateHandle: SavedStateHandle,
    override val pagingDataFlow: Flow<PagingData<File>>,
    private val scope: CoroutineScope,
    private val route: BasicCollectionEdit,
    private val flowCollectionUseCase: FlowCollectionUseCase,
    private val updateCollectionUseCase: UpdateCollectionUseCase,
    private val removeCollectionFileUseCase: RemoveCollectionFileUseCase,
) : BasicCollectionEditScreenState {

    override var uiState by savedStateHandle.saveable {
        mutableStateOf(BasicCollectionEditScreenUiState())
    }
        private set

    override val events = EventFlow<BasicCollectionEditScreenStateEvent>()

    init {
        scope.launch {
            flowCollectionUseCase(FlowCollectionUseCase.Request(route.id))
                .mapNotNull { it.dataOrNull() as? BasicCollection }
                .first().let {
                    uiState = uiState.copy(formData = uiState.formData.copy(name = it.name))
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
            ).first()
        }
    }

    override suspend fun onSubmit(formData: BasicCollectionEditorFormData) {
        scope.launch {
            val collection = flowCollectionUseCase(FlowCollectionUseCase.Request(route.id))
                .mapNotNull { it.dataOrNull() as? BasicCollection }
                .first()
            updateCollectionUseCase(UpdateCollectionUseCase.Request(collection.copy(name = formData.name)))
                .collect()
            events.tryEmit(BasicCollectionEditScreenStateEvent.EditComplete)
        }
    }
}
