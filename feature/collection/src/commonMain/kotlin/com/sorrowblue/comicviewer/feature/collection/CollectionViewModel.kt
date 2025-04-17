package com.sorrowblue.comicviewer.feature.collection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.sorrowblue.comicviewer.domain.usecase.collection.PagingCollectionFileUseCase
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.InjectedParam

@KoinViewModel
internal class CollectionViewModel(
    @InjectedParam route: Collection,
    pagingCollectionFileUseCase: PagingCollectionFileUseCase,
) : ViewModel() {

    val pagingDataFlow = pagingCollectionFileUseCase(
        PagingCollectionFileUseCase.Request(PagingConfig(20), route.id)
    ).cachedIn(viewModelScope)
}
