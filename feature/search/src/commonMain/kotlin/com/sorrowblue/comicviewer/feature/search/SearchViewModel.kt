package com.sorrowblue.comicviewer.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.sorrowblue.comicviewer.domain.model.SearchCondition
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
import com.sorrowblue.comicviewer.domain.usecase.file.PagingQueryFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageFolderDisplaySettingsUseCase
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.InjectedParam

@KoinViewModel
internal class SearchViewModel(
    @InjectedParam route: Search,
    pagingQueryFileUseCase: PagingQueryFileUseCase,
    val manageFolderDisplaySettingsUseCase: ManageFolderDisplaySettingsUseCase,
) : ViewModel() {

    var searchCondition = SearchCondition(
        "",
        SearchCondition.Range.Bookshelf,
        SearchCondition.Period.None,
        SortType.Name(true)
    )

    val pagingDataFlow = pagingQueryFileUseCase(
        PagingQueryFileUseCase.Request(PagingConfig(100), route.bookshelfId) { searchCondition }
    ).cachedIn(viewModelScope)
}
