package com.sorrowblue.comicviewer.feature.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.sorrowblue.comicviewer.domain.model.SearchCondition
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingQueryFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageFolderDisplaySettingsUseCase
import com.sorrowblue.comicviewer.feature.search.destinations.SearchScreenDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class SearchViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    pagingQueryFileUseCase: PagingQueryFileUseCase,
    val manageFolderDisplaySettingsUseCase: ManageFolderDisplaySettingsUseCase,
) : ViewModel() {

    private val args = SearchScreenDestination.argsFrom(savedStateHandle)

    var searchCondition: SearchCondition =
        SearchCondition(
            "",
            SearchCondition.Range.Bookshelf,
            SearchCondition.Period.None,
            SortType.Name(true)
        )
    val pagingDataFlow = pagingQueryFileUseCase.execute(
        PagingQueryFileUseCase.Request(PagingConfig(100), args.bookshelfId) { searchCondition }
    ).cachedIn(viewModelScope)
}
