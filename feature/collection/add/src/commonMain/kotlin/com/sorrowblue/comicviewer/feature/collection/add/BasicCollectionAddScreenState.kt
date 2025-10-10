package com.sorrowblue.comicviewer.feature.collection.add

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.sorrowblue.comicviewer.domain.model.collection.Collection
import com.sorrowblue.comicviewer.domain.model.collection.CollectionFile
import com.sorrowblue.comicviewer.domain.usecase.collection.AddCollectionFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.collection.RemoveCollectionFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.CollectionSettingsUseCase
import com.sorrowblue.comicviewer.feature.collection.add.component.CollectionSort
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

internal interface BasicCollectionAddScreenState {

    val uiState: BasicCollectionAddScreenUiState
    val lazyPagingItems: LazyPagingItems<Pair<Collection, Boolean>>
    val lazyListState: LazyListState

    fun onCollectionClick(collection: Collection, exist: Boolean)
    fun onClickCollectionSort(sort: CollectionSort)
}

@Composable
internal fun rememberBasicCollectionAddScreenState(
    route: BasicCollectionAdd,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    lazyListState: LazyListState = rememberLazyListState(),
    removeCollectionFileUseCase: RemoveCollectionFileUseCase = koinInject(),
    addCollectionFileUseCase: AddCollectionFileUseCase = koinInject(),
    collectionSettingsUseCase: CollectionSettingsUseCase = koinInject(),
    viewModel: BasicCollectionAddViewModel = koinViewModel { parametersOf(route) },
): BasicCollectionAddScreenState {
    return remember {
        BasicCollectionAddScreenStateImpl(
            route = route,
            coroutineScope = coroutineScope,
            removeCollectionFileUseCase = removeCollectionFileUseCase,
            addCollectionFileUseCase = addCollectionFileUseCase,
            collectionSettingsUseCase = collectionSettingsUseCase,
        )
    }.apply {
        this.coroutineScope = coroutineScope
        this.lazyListState = lazyListState
        this.lazyPagingItems = viewModel.pagingDataFlow.collectAsLazyPagingItems()
    }
}

private class BasicCollectionAddScreenStateImpl(
    private val route: BasicCollectionAdd,
    var coroutineScope: CoroutineScope,
    private val removeCollectionFileUseCase: RemoveCollectionFileUseCase,
    private val addCollectionFileUseCase: AddCollectionFileUseCase,
    private val collectionSettingsUseCase: CollectionSettingsUseCase,
) : BasicCollectionAddScreenState {

    override lateinit var lazyPagingItems: LazyPagingItems<Pair<Collection, Boolean>>
    override lateinit var lazyListState: LazyListState
    override var uiState by mutableStateOf(BasicCollectionAddScreenUiState())
        private set

    init {
        collectionSettingsUseCase.settings.map { it.recent }.distinctUntilChanged().onEach {
            uiState =
                uiState.copy(collectionSort = if (it) CollectionSort.Recent else CollectionSort.Created)
        }.launchIn(coroutineScope)
    }

    override fun onClickCollectionSort(sort: CollectionSort) {
        coroutineScope.launch {
            collectionSettingsUseCase.edit {
                it.copy(sort == CollectionSort.Recent)
            }
            lazyPagingItems.refresh()
        }
    }

    override fun onCollectionClick(collection: Collection, exist: Boolean) {
        coroutineScope.launch {
            if (exist) {
                removeCollectionFileUseCase(
                    RemoveCollectionFileUseCase.Request(
                        CollectionFile(
                            collection.id,
                            route.bookshelfId,
                            route.path
                        )
                    )
                )
            } else {
                addCollectionFileUseCase(
                    AddCollectionFileUseCase.Request(
                        CollectionFile(
                            collection.id,
                            route.bookshelfId,
                            route.path
                        )
                    )
                )
            }
        }
    }
}
