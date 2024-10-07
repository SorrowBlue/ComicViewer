package com.sorrowblue.comicviewer.feature.readlater

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.sorrowblue.comicviewer.domain.usecase.readlater.DeleteAllReadLaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.readlater.PagingReadLaterFileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class ReadLaterViewModel @Inject constructor(
    pagingReadLaterFileUseCase: PagingReadLaterFileUseCase,
    val deleteAllReadLaterUseCase: DeleteAllReadLaterUseCase,
) : ViewModel() {

    val pagingDataFlow = pagingReadLaterFileUseCase
        .execute(PagingReadLaterFileUseCase.Request(PagingConfig(20)))
        .cachedIn(viewModelScope)
}
