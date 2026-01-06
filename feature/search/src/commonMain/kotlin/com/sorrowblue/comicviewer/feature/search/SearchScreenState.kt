package com.sorrowblue.comicviewer.feature.search

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.paging.PagingConfig
import androidx.paging.compose.LazyPagingItems
import com.sorrowblue.comicviewer.domain.model.SearchCondition
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.settings.folder.FileListDisplay
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
import com.sorrowblue.comicviewer.domain.usecase.file.PagingQueryFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageFolderDisplaySettingsUseCase
import com.sorrowblue.comicviewer.framework.ui.paging.rememberPagingItems
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@Stable
internal interface SearchScreenState {
    val uiState: SearchScreenUiState
    val lazyGridState: LazyGridState
    val lazyPagingItems: LazyPagingItems<File>

    var isSkipFirstRefresh: Boolean
    var isScrollableTop: Boolean

    fun onQueryChange(query: String)

    fun onRangeClick(range: SearchCondition.Range)

    fun onPeriodClick(period: SearchCondition.Period)

    fun onSortTypeClick(sortType: SortType)

    fun onShowHiddenClick()
}

@Composable
context(context: SearchScreenContext)
internal fun rememberSearchScreenState(bookshelfId: BookshelfId, path: String): SearchScreenState {
    val coroutineScope = rememberCoroutineScope()
    val lazyGridState = rememberLazyGridState()
    
    // Create a reference that will hold the state holder
    // This allows lazyPagingItems to access the state holder's uiState
    val stateHolderRef = remember { mutableStateOf<SearchScreenStateImpl?>(null) }
    
    // Create lazyPagingItems that accesses stateHolder via the reference
    // The lambda is evaluated lazily, so stateHolderRef will be set by then
    val lazyPagingItems = rememberPagingItems {
        context.pagingQueryFileUseCase(
            PagingQueryFileUseCase.Request(PagingConfig(100), bookshelfId) {
                stateHolderRef.value?.uiState?.searchCondition ?: SearchCondition()
            },
        )
    }
    
    // Create state holder with all dependencies
    return remember(lazyGridState, lazyPagingItems) {
        SearchScreenStateImpl(
            path = path,
            lazyGridState = lazyGridState,
            lazyPagingItems = lazyPagingItems,
            coroutineScope = coroutineScope,
            manageFolderDisplaySettingsUseCase = context.manageFolderDisplaySettingsUseCase,
        ).also {
            stateHolderRef.value = it
        }
    }
}

@OptIn(SavedStateHandleSaveableApi::class)
private class SearchScreenStateImpl(
    private val path: String,
    override val lazyGridState: LazyGridState,
    override val lazyPagingItems: LazyPagingItems<File>,
    private val coroutineScope: CoroutineScope,
    manageFolderDisplaySettingsUseCase: ManageFolderDisplaySettingsUseCase,
) : SearchScreenState {

    override var uiState by mutableStateOf(SearchScreenUiState())

    override var isScrollableTop by mutableStateOf(false)
    override var isSkipFirstRefresh by mutableStateOf(true)

    init {
        manageFolderDisplaySettingsUseCase.settings
            .onEach {
                uiState = uiState.copy(
                    searchContentsUiState = uiState.searchContentsUiState.copy(
                        fileLazyVerticalGridUiState = uiState.searchContentsUiState.fileLazyVerticalGridUiState
                            .copy(
                                fileListDisplay = FileListDisplay.List,
                                showThumbnails = it.showThumbnails,
                                fontSize = it.fontSize,
                                imageScale = it.imageScale,
                                imageFilterQuality = it.imageFilterQuality,
                            ),
                    ),
                )
            }.launchIn(coroutineScope)
    }

    override fun onPeriodClick(period: SearchCondition.Period) {
        uiState = copySearchCondition { it.copy(period = period) }
        update()
    }

    override fun onQueryChange(query: String) {
        uiState = uiState.copy(
            searchCondition = uiState.searchCondition.copy(query = query),
            searchContentsUiState = uiState.searchContentsUiState.copy(query = query),
        )
        update()
    }

    override fun onRangeClick(range: SearchCondition.Range) {
        uiState = copySearchCondition {
            it.copy(
                range = when (range) {
                    SearchCondition.Range.Bookshelf -> SearchCondition.Range.Bookshelf
                    is SearchCondition.Range.InFolder -> SearchCondition.Range.InFolder(path)
                    is SearchCondition.Range.SubFolder -> SearchCondition.Range.SubFolder(path)
                },
            )
        }
        update()
    }

    override fun onSortTypeClick(sortType: SortType) {
        uiState = copySearchCondition { it.copy(sortType = sortType) }
        update()
    }

    override fun onShowHiddenClick() {
        uiState = copySearchCondition { it.copy(showHidden = !it.showHidden) }
        update()
    }

    private fun copySearchCondition(
        action: (SearchCondition) -> SearchCondition,
    ): SearchScreenUiState = uiState.copy(searchCondition = action(uiState.searchCondition))

    private fun update() {
        isScrollableTop = true
        if (isSkipFirstRefresh) {
            isSkipFirstRefresh = false
        }
    }
}
