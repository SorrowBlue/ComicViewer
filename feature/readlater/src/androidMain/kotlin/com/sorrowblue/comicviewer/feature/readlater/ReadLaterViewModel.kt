package com.sorrowblue.comicviewer.feature.readlater

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.sorrowblue.comicviewer.domain.usecase.readlater.DeleteAllReadLaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.readlater.PagingReadLaterFileUseCase
import org.koin.android.annotation.KoinViewModel


@KoinViewModel
internal class ReadLaterViewModel(
    pagingReadLaterFileUseCase: PagingReadLaterFileUseCase,
    val deleteAllReadLaterUseCase: DeleteAllReadLaterUseCase,
) : ViewModel() {

    val pagingDataFlow =
        pagingReadLaterFileUseCase(PagingReadLaterFileUseCase.Request(PagingConfig(20)))
            .cachedIn(viewModelScope)
}
