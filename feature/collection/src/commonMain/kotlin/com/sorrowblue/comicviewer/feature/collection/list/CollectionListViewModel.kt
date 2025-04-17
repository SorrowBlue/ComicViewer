package com.sorrowblue.comicviewer.feature.collection.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.sorrowblue.comicviewer.domain.usecase.collection.PagingCollectionUseCase
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
internal class CollectionListViewModel(
    pagingCollectionUseCase: PagingCollectionUseCase,
) : ViewModel() {

    val pagingDataFlow = pagingCollectionUseCase(PagingCollectionUseCase.Request(PagingConfig(20)))
        .cachedIn(viewModelScope)
}
