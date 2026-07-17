/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.sorrowblue.comicviewer.domain.model.SearchCondition
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.usecase.file.PagingQueryFileUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactory
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactoryKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@AssistedInject
internal class SearchViewModel(
    @Assisted val bookshelfId: BookshelfId,
    pagingQueryFileUseCase: PagingQueryFileUseCase,
) : ViewModel() {

    val searchConditionFlow: StateFlow<SearchCondition>
        field = MutableStateFlow(SearchCondition())

    val pagingDataFlow = pagingQueryFileUseCase(
        PagingQueryFileUseCase.Request(PagingConfig(20), bookshelfId) {
            searchConditionFlow.value
        },
    ).cachedIn(viewModelScope)

    fun updateSearchCondition(searchCondition: SearchCondition) {
        searchConditionFlow.value = searchCondition
    }

    @AssistedFactory
    @ManualViewModelAssistedFactoryKey
    @ContributesIntoMap(AppScope::class)
    interface Factory : ManualViewModelAssistedFactory {
        fun create(bookshelfId: BookshelfId): SearchViewModel
    }
}
