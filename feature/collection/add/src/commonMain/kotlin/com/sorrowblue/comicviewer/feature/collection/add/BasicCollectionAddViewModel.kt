package com.sorrowblue.comicviewer.feature.collection.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.sorrowblue.comicviewer.domain.usecase.collection.PagingCollectionExistUseCase
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.InjectedParam

@KoinViewModel
internal class BasicCollectionAddViewModel(
    @InjectedParam route: BasicCollectionAdd,
    pagingCollectionExistUseCase: PagingCollectionExistUseCase,
) : ViewModel() {

    val pagingDataFlow = pagingCollectionExistUseCase(
        PagingCollectionExistUseCase.Request(PagingConfig(20), route.bookshelfId, route.path, false)
    ).cachedIn(viewModelScope)

    val recentPagingDataFlow = pagingCollectionExistUseCase(
        PagingCollectionExistUseCase.Request(PagingConfig(20), route.bookshelfId, route.path, true)
    ).cachedIn(viewModelScope)
}
