package com.sorrowblue.comicviewer.feature.collection.add

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.paging.PagingConfig
import androidx.paging.compose.LazyPagingItems
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.collection.Collection
import com.sorrowblue.comicviewer.domain.model.collection.CollectionFile
import com.sorrowblue.comicviewer.domain.model.collection.CollectionType
import com.sorrowblue.comicviewer.domain.usecase.collection.AddCollectionFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.collection.PagingCollectionExistUseCase
import com.sorrowblue.comicviewer.domain.usecase.collection.RemoveCollectionFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.CollectionSettingsUseCase
import com.sorrowblue.comicviewer.feature.collection.add.component.CollectionSort
import com.sorrowblue.comicviewer.framework.ui.paging.rememberPagingItems
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

internal interface BasicCollectionAddScreenState {
    val uiState: BasicCollectionAddScreenUiState
    val lazyPagingItems: LazyPagingItems<Pair<Collection, Boolean>>
    val lazyListState: LazyListState

    fun onCollectionClick(collection: Collection, exist: Boolean)

    fun onClickCollectionSort(sort: CollectionSort)
}

@Composable
context(context: BasicCollectionAddScreenContext)
internal fun rememberBasicCollectionAddScreenState(
    bookshelfId: BookshelfId,
    path: String,
): BasicCollectionAddScreenState {
    val coroutineScope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()
    val lazyPagingItems = rememberPagingItems {
        context.pagingCollectionExistUseCase(
            PagingCollectionExistUseCase.Request(
                pagingConfig = PagingConfig(PageSize),
                bookshelfId = bookshelfId,
                path = path,
                collectionType = CollectionType.Basic,
            ),
        )
    }
    return remember(bookshelfId, path) {
        BasicCollectionAddScreenStateImpl(
            bookshelfId = bookshelfId,
            path = path,
            coroutineScope = coroutineScope,
            removeCollectionFileUseCase = context.removeCollectionFileUseCase,
            addCollectionFileUseCase = context.addCollectionFileUseCase,
            collectionSettingsUseCase = context.collectionSettingsUseCase,
            lazyListState = lazyListState,
            lazyPagingItems = lazyPagingItems,
        )
    }
}

private const val PageSize = 20

private class BasicCollectionAddScreenStateImpl(
    private val bookshelfId: BookshelfId,
    private val path: String,
    private val coroutineScope: CoroutineScope,
    private val removeCollectionFileUseCase: RemoveCollectionFileUseCase,
    private val addCollectionFileUseCase: AddCollectionFileUseCase,
    private val collectionSettingsUseCase: CollectionSettingsUseCase,
    override val lazyListState: LazyListState,
    override val lazyPagingItems: LazyPagingItems<Pair<Collection, Boolean>>,
) : BasicCollectionAddScreenState {
    override var uiState by mutableStateOf(BasicCollectionAddScreenUiState())
        private set

    init {
        collectionSettingsUseCase.settings
            .map { it.recent }
            .distinctUntilChanged()
            .onEach {
                uiState =
                    uiState.copy(
                        collectionSort = if (it) CollectionSort.Recent else CollectionSort.Created,
                    )
            }.launchIn(coroutineScope)
    }

    override fun onClickCollectionSort(sort: CollectionSort) {
        coroutineScope.launch {
            collectionSettingsUseCase.edit {
                it.copy(recent = sort == CollectionSort.Recent)
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
                            bookshelfId,
                            path,
                        ),
                    ),
                )
            } else {
                addCollectionFileUseCase(
                    AddCollectionFileUseCase.Request(
                        CollectionFile(
                            collection.id,
                            bookshelfId,
                            path,
                        ),
                    ),
                )
            }
        }
    }
}
