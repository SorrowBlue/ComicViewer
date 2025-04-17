package com.sorrowblue.comicviewer.feature.collection.editor.smart

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.sorrowblue.comicviewer.domain.EmptyRequest
import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.collection.SmartCollection
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.FlowBookshelfListUseCase
import com.sorrowblue.comicviewer.domain.usecase.collection.CreateCollectionUseCase
import com.sorrowblue.comicviewer.feature.collection.editor.smart.section.SmartCollectionEditorFormData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.compose.koinInject

@Composable
internal fun rememberSmartCollectionCreateScreenState(
    route: SmartCollectionCreate,
    scope: CoroutineScope = rememberCoroutineScope(),
    flowBookshelfListUseCase: FlowBookshelfListUseCase = koinInject(),
    createCollectionUseCase: CreateCollectionUseCase = koinInject(),
): SmartCollectionEditorScreenState {
    return remember {
        SmartCollectionCreateScreenStateImpl(
            route = route,
            scope = scope,
            flowBookshelfListUseCase = flowBookshelfListUseCase,
            createCollectionUseCase = createCollectionUseCase
        )
    }
}

private class SmartCollectionCreateScreenStateImpl(
    route: SmartCollectionCreate,
    scope: CoroutineScope,
    flowBookshelfListUseCase: FlowBookshelfListUseCase,
    private val createCollectionUseCase: CreateCollectionUseCase,
) : SmartCollectionEditorScreenState {

    override var uiState by mutableStateOf(
        SmartCollectionEditorScreenUiState(
            formData = SmartCollectionEditorFormData(
                bookshelfId = if (route.bookshelfId == BookshelfId()) null else route.bookshelfId,
                searchCondition = route.searchCondition
            )
        )
    )

    init {
        flowBookshelfListUseCase(EmptyRequest).filterIsInstance<Resource.Success<List<Bookshelf>>>()
            .onEach {
                uiState = uiState.copy(bookshelfList = it.data)
            }.launchIn(scope)
    }

    override suspend fun onSubmit(formData: SmartCollectionEditorFormData) {
        delay(1000)
        createCollectionUseCase(
            CreateCollectionUseCase.Request(
                SmartCollection(
                    formData.name,
                    formData.bookshelfId,
                    formData.searchCondition
                )
            )
        ).first()
    }
}
