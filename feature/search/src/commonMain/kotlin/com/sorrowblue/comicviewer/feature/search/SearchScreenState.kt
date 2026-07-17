/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

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
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.sorrowblue.comicviewer.domain.model.SearchCondition
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
import dev.zacsweers.metrox.viewmodel.assistedMetroViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
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
internal fun rememberSearchScreenState(
    bookshelfId: BookshelfId,
    path: String,
    viewModel: SearchViewModel = assistedMetroViewModel<SearchViewModel, SearchViewModel.Factory> {
        create(bookshelfId)
    },
): SearchScreenState {
    val coroutineScope = rememberCoroutineScope()
    val lazyGridState = rememberLazyGridState()
    val state = remember(lazyGridState) {
        SearchScreenStateImpl(
            path = path,
            lazyGridState = lazyGridState,
            coroutineScope = coroutineScope,
            searchConditionFlow = viewModel.searchConditionFlow,
            updateSearchCondition = viewModel::updateSearchCondition,
        )
    }
    state.lazyPagingItems = viewModel.pagingDataFlow.collectAsLazyPagingItems()
    return state
}

@OptIn(SavedStateHandleSaveableApi::class)
private class SearchScreenStateImpl(
    private val path: String,
    override val lazyGridState: LazyGridState,
    coroutineScope: CoroutineScope,
    private val searchConditionFlow: StateFlow<SearchCondition>,
    private val updateSearchCondition: (SearchCondition) -> Unit,
) : SearchScreenState {

    override lateinit var lazyPagingItems: LazyPagingItems<File>

    override var uiState by mutableStateOf(SearchScreenUiState())

    override var isScrollableTop by mutableStateOf(false)
    override var isSkipFirstRefresh by mutableStateOf(true)

    init {
        searchConditionFlow.onEach {
            uiState = uiState.copy(searchCondition = it)
        }.launchIn(coroutineScope)
    }

    override fun onPeriodClick(period: SearchCondition.Period) {
        updateSearchCondition(searchConditionFlow.value.copy(period = period))
        update()
    }

    override fun onQueryChange(query: String) {
        updateSearchCondition(searchConditionFlow.value.copy(query = query))
        update()
    }

    override fun onRangeClick(range: SearchCondition.Range) {
        updateSearchCondition(
            searchConditionFlow.value.copy(
                range = when (range) {
                    SearchCondition.Range.Bookshelf -> SearchCondition.Range.Bookshelf
                    is SearchCondition.Range.InFolder -> SearchCondition.Range.InFolder(path)
                    is SearchCondition.Range.SubFolder -> SearchCondition.Range.SubFolder(path)
                },
            ),
        )
        update()
    }

    override fun onSortTypeClick(sortType: SortType) {
        updateSearchCondition(searchConditionFlow.value.copy(sortType = sortType))
        update()
    }

    override fun onShowHiddenClick() {
        updateSearchCondition(
            searchConditionFlow.value.copy(showHidden = !searchConditionFlow.value.showHidden),
        )
        update()
    }

    private fun update() {
        isScrollableTop = true
        if (isSkipFirstRefresh) {
            isSkipFirstRefresh = false
        }
    }
}
