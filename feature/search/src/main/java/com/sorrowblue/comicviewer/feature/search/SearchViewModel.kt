package com.sorrowblue.comicviewer.feature.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.sorrowblue.comicviewer.domain.model.SearchCondition
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingQueryFileUseCase
import com.sorrowblue.comicviewer.feature.search.navigation.SearchArgs
import com.sorrowblue.comicviewer.feature.search.section.SearchConditionSheetUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
internal class SearchViewModel @Inject constructor(
    pagingQueryFileUseCase: PagingQueryFileUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val args = SearchArgs(savedStateHandle)

    private val bookshelfId = args.bookshelfId
    private val path = args.path
    private val _uiState = MutableStateFlow(SearchScreenUiState())

    val uiState: StateFlow<SearchScreenUiState> = _uiState.asStateFlow()

    val pagingDataFlow = pagingQueryFileUseCase.execute(
        PagingQueryFileUseCase.Request(PagingConfig(100), bookshelfId) {
            val searchConditionSheetUiState = uiState.value.searchConditionSheetUiState
            SearchCondition(
                uiState.value.searchQuery,
                when (searchConditionSheetUiState.range) {
                    SearchConditionSheetUiState.Range.BOOKSHELF -> SearchCondition.Range.BOOKSHELF
                    SearchConditionSheetUiState.Range.IN_FOLDER -> SearchCondition.Range.InFolder(
                        path
                    )

                    SearchConditionSheetUiState.Range.FOLDER_BELOW -> SearchCondition.Range.SubFolder(
                        path
                    )
                },
                when (searchConditionSheetUiState.period) {
                    SearchConditionSheetUiState.Period.NONE -> SearchCondition.Period.NONE
                    SearchConditionSheetUiState.Period.HOUR_24 -> SearchCondition.Period.HOUR_24
                    SearchConditionSheetUiState.Period.WEEK_1 -> SearchCondition.Period.WEEK_1
                    SearchConditionSheetUiState.Period.MONTH_1 -> SearchCondition.Period.MONTH_1
                },
                when (searchConditionSheetUiState.order) {
                    SearchConditionSheetUiState.Order.NAME -> SearchCondition.Order.NAME
                    SearchConditionSheetUiState.Order.TIMESTAMP -> SearchCondition.Order.DATE
                    SearchConditionSheetUiState.Order.SIZE -> SearchCondition.Order.SIZE
                },
                when (searchConditionSheetUiState.sort) {
                    SearchConditionSheetUiState.Sort.ASC -> SearchCondition.Sort.ASC
                    SearchConditionSheetUiState.Sort.DESC -> SearchCondition.Sort.DESC
                }
            )
        }
    ).cachedIn(viewModelScope)

    fun onSearchQueryChange(query: String) {
        update()
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun updateRange(range: SearchConditionSheetUiState.Range) {
        update()
        val uiState = _uiState.value
        _uiState.value = uiState.copy(
            searchConditionSheetUiState = uiState.searchConditionSheetUiState.copy(range = range)
        )
    }

    fun updatePeriod(period: SearchConditionSheetUiState.Period) {
        update()
        val uiState = _uiState.value
        _uiState.value = uiState.copy(
            searchConditionSheetUiState = uiState.searchConditionSheetUiState.copy(period = period)
        )
    }
    fun updateSort(sort: SearchConditionSheetUiState.Sort) {
        update()
        val uiState = _uiState.value
        _uiState.value = uiState.copy(
            searchConditionSheetUiState = uiState.searchConditionSheetUiState.copy(sort = sort)
        )
    }
    fun updateOrder(order: SearchConditionSheetUiState.Order) {
        update()
        val uiState = _uiState.value
        _uiState.value = uiState.copy(
            searchConditionSheetUiState = uiState.searchConditionSheetUiState.copy(order = order)
        )
    }

    var isScrollableTop = false
    var isSkipFirstRefresh = true
    private fun update() {
        isScrollableTop = true
        if (isSkipFirstRefresh) isSkipFirstRefresh = false
    }

    fun toggleSearchFilter() {
        val uiState = _uiState.value
        _uiState.value = uiState.copy(
            searchConditionSheetUiState = uiState.searchConditionSheetUiState.copy(
                isVisible = !uiState.searchConditionSheetUiState.isVisible
            )
        )
    }

}
