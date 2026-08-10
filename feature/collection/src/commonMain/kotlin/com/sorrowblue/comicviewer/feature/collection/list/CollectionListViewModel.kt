/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.collection.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.sorrowblue.comicviewer.domain.usecase.collection.PagingCollectionUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ViewModelKey

@ViewModelKey
@ContributesIntoMap(AppScope::class)
internal class CollectionListViewModel(pagingCollectionUseCase: PagingCollectionUseCase) :
    ViewModel() {

    val pagingDataFlow = pagingCollectionUseCase(
        PagingCollectionUseCase.Request(PagingConfig(PageSize)),
    ).cachedIn(viewModelScope)
}

private const val PageSize = 20
