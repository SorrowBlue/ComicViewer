/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.sorrowblue.comicviewer.domain.usecase.file.ClearAllHistoryUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.PagingHistoryBookUseCase
import com.sorrowblue.comicviewer.domain.usecase.invoke
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.launch

@ViewModelKey
@ContributesIntoMap(AppScope::class)
internal class HistoryViewModel(
    pagingHistoryBookUseCase: PagingHistoryBookUseCase,
    private val clearAllHistoryUseCase: ClearAllHistoryUseCase,
) : ViewModel() {

    val pagingDataFlow = pagingHistoryBookUseCase(
        PagingConfig(
            pageSize = PageSize,
            prefetchDistance = PrefetchDistance,
            initialLoadSize = InitialLoadSize,
            jumpThreshold = JumpThreshold,
        ),
    ).cachedIn(viewModelScope)

    fun clearAll() {
        viewModelScope.launch {
            clearAllHistoryUseCase()
        }
    }
}

private const val PageSize = 60
private const val PrefetchDistance = 30
private const val InitialLoadSize = 60
private const val JumpThreshold = PageSize * 3
