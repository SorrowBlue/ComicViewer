package com.sorrowblue.comicviewer.feature.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.sorrowblue.comicviewer.domain.usecase.file.ClearAllHistoryUseCase
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingHistoryBookUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class HistoryViewModel @Inject constructor(
    pagingHistoryBookUseCase: PagingHistoryBookUseCase,
    val clearAllHistoryUseCase: ClearAllHistoryUseCase,
) : ViewModel() {

    val pagingDataFlow = pagingHistoryBookUseCase
        .execute(PagingHistoryBookUseCase.Request(PagingConfig(20)))
        .cachedIn(viewModelScope)
}
