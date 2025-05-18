package com.sorrowblue.comicviewer.feature.collection.editor.smart

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.sorrowblue.comicviewer.domain.EmptyRequest
import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.bookshelf.InternalStorage
import com.sorrowblue.comicviewer.domain.model.collection.SmartCollection
import com.sorrowblue.comicviewer.domain.model.dataOrNull
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.FlowBookshelfListUseCase
import com.sorrowblue.comicviewer.domain.usecase.collection.GetCollectionUseCase
import com.sorrowblue.comicviewer.domain.usecase.collection.UpdateCollectionUseCase
import com.sorrowblue.comicviewer.feature.collection.editor.smart.section.SmartCollectionEditorFormData
import com.sorrowblue.comicviewer.framework.ui.EventFlow
import comicviewer.feature.collection.editor.generated.resources.Res
import comicviewer.feature.collection.editor.generated.resources.collection_editor_label_all_bookshelf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import org.jetbrains.compose.resources.getString
import org.koin.compose.koinInject

@Composable
internal fun rememberSmartCollectionEditScreenState(
    route: SmartCollectionEdit,
    scope: CoroutineScope = rememberCoroutineScope(),
    flowBookshelfListUseCase: FlowBookshelfListUseCase = koinInject(),
    updateCollectionUseCase: UpdateCollectionUseCase = koinInject(),
    getCollectionUseCase: GetCollectionUseCase = koinInject(),
): SmartCollectionEditorScreenState {
    return SmartCollectionEditScreenImpl(
        route = route,
        scope = scope,
        flowBookshelfListUseCase = flowBookshelfListUseCase,
        updateCollectionUseCase = updateCollectionUseCase,
        getCollectionUseCase = getCollectionUseCase
    )
}

private class SmartCollectionEditScreenImpl(
    route: SmartCollectionEdit,
    scope: CoroutineScope,
    flowBookshelfListUseCase: FlowBookshelfListUseCase,
    getCollectionUseCase: GetCollectionUseCase,
    private val updateCollectionUseCase: UpdateCollectionUseCase,
) : SmartCollectionEditorScreenState {

    override val event = EventFlow<SmartCollectionEditorScreenStateEvent>()

    override var uiState by mutableStateOf(SmartCollectionEditorScreenUiState())

    init {
        getCollectionUseCase(GetCollectionUseCase.Request(route.collectionId))
            .mapNotNull { it.dataOrNull() as? SmartCollection }
            .onEach {
                uiState = uiState.copy(
                    formData = SmartCollectionEditorFormData(
                        name = it.name,
                        bookshelfId = it.bookshelfId ?: BookshelfId(),
                        searchCondition = it.searchCondition,
                    )
                )
            }.launchIn(scope)
        flowBookshelfListUseCase(EmptyRequest).filterIsInstance<Resource.Success<List<Bookshelf>>>()
            .onEach {
                uiState = uiState.copy(
                    bookshelfList = buildList {
                        add(allBookshelf())
                        addAll(it.data)
                    }
                )
            }.launchIn(scope)
    }

    override suspend fun onSubmit(formData: SmartCollectionEditorFormData) {
        updateCollectionUseCase(
            UpdateCollectionUseCase.Request(
                SmartCollection(
                    formData.name,
                    formData.bookshelfId,
                    formData.searchCondition
                )
            )
        )
        event.emit(SmartCollectionEditorScreenStateEvent.Complete)
    }

    private suspend fun allBookshelf(): Bookshelf {
        return InternalStorage(
            BookshelfId(),
            getString(Res.string.collection_editor_label_all_bookshelf),
            0
        )
    }
}
