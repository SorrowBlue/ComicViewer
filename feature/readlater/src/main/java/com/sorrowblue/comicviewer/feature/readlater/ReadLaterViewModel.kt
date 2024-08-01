package com.sorrowblue.comicviewer.feature.readlater

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.sorrowblue.comicviewer.domain.usecase.file.AddReadLaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.DeleteAllReadLaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.DeleteReadLaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.ExistsReadlaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.GetFileAttributeUseCase
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingReadLaterFileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class ReadLaterViewModel @Inject constructor(
    pagingReadLaterFileUseCase: PagingReadLaterFileUseCase,
    val addReadLaterUseCase: AddReadLaterUseCase,
    val deleteReadLaterUseCase: DeleteReadLaterUseCase,
    val deleteAllReadLaterUseCase: DeleteAllReadLaterUseCase,
    val existsReadlaterUseCase: ExistsReadlaterUseCase,
    val getFileAttributeUseCase: GetFileAttributeUseCase,
) : ViewModel() {

    val pagingDataFlow = pagingReadLaterFileUseCase
        .execute(PagingReadLaterFileUseCase.Request(PagingConfig(20)))
        .cachedIn(viewModelScope)
}
