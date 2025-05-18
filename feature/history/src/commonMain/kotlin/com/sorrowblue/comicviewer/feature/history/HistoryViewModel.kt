package com.sorrowblue.comicviewer.feature.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.sorrowblue.comicviewer.domain.usecase.file.PagingHistoryBookUseCase
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
internal class HistoryViewModel(pagingHistoryBookUseCase: PagingHistoryBookUseCase) :
    ViewModel() {

    val pagingDataFlow =
        pagingHistoryBookUseCase(PagingHistoryBookUseCase.Request(PagingConfig(20)))
            .cachedIn(viewModelScope)
}
