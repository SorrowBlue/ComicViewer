package com.sorrowblue.comicviewer.feature.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.sorrowblue.comicviewer.domain.model.SearchCondition
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.usecase.file.AddReadLaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingQueryFileUseCase
import com.sorrowblue.comicviewer.feature.search.destinations.SearchScreenDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@HiltViewModel
internal class SearchViewModel @Inject constructor(
    pagingQueryFileUseCase: PagingQueryFileUseCase,
    private val addReadLaterUseCase: AddReadLaterUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val args = SearchScreenDestination.argsFrom(savedStateHandle)

    var searchCondition: () -> SearchCondition = {
        SearchCondition(
            "",
            SearchCondition.Range.BOOKSHELF,
            SearchCondition.Period.NONE,
            SearchCondition.Order.NAME,
            SearchCondition.Sort.ASC
        )
    }

    val pagingDataFlow = pagingQueryFileUseCase.execute(
        PagingQueryFileUseCase.Request(PagingConfig(100), args.bookshelfId) { searchCondition() }
    ).cachedIn(viewModelScope)

    fun addReadLater(file: File) {
        viewModelScope.launch {
            addReadLaterUseCase.execute(AddReadLaterUseCase.Request(file.bookshelfId, file.path))
                .first()
        }
    }
}
