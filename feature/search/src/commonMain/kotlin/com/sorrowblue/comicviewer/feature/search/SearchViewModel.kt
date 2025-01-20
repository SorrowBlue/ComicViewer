package com.sorrowblue.comicviewer.feature.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavType
import androidx.navigation.toRoute
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.sorrowblue.comicviewer.domain.model.SearchCondition
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingQueryFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageFolderDisplaySettingsUseCase
import kotlin.reflect.KType
import org.koin.android.annotation.KoinViewModel


@KoinViewModel
internal class SearchViewModel(
    savedStateHandle: SavedStateHandle,
    typeMap: Map<KType, NavType<*>>,
    pagingQueryFileUseCase: PagingQueryFileUseCase,
    val manageFolderDisplaySettingsUseCase: ManageFolderDisplaySettingsUseCase,
) : ViewModel() {

    private val args = savedStateHandle.toRoute<Search>(typeMap)

    var searchCondition: SearchCondition =
        SearchCondition(
            "",
            SearchCondition.Range.Bookshelf,
            SearchCondition.Period.None,
            SortType.Name(true)
        )
    val pagingDataFlow = pagingQueryFileUseCase(
        PagingQueryFileUseCase.Request(PagingConfig(100), args.bookshelfId) { searchCondition }
    ).cachedIn(viewModelScope)
}
