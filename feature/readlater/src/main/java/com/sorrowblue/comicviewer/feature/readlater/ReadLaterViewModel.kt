package com.sorrowblue.comicviewer.feature.readlater

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.usecase.AddReadLaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.DeleteAllReadLaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingReadLaterFileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@HiltViewModel
internal class ReadLaterViewModel @Inject constructor(
    private val addReadLaterUseCase: AddReadLaterUseCase,
    private val deleteAllReadLaterUseCase: DeleteAllReadLaterUseCase,
    pagingReadLaterFileUseCase: PagingReadLaterFileUseCase,
) : ViewModel() {

    val pagingDataFlow = pagingReadLaterFileUseCase
        .execute(PagingReadLaterFileUseCase.Request(PagingConfig(20)))
        .cachedIn(viewModelScope)

    fun addToReadLater(file: File) {
        viewModelScope.launch {
            addReadLaterUseCase.execute(AddReadLaterUseCase.Request(file.bookshelfId, file.path))
                .first()
        }
    }

    fun clearAll() {
        viewModelScope.launch {
            deleteAllReadLaterUseCase.execute(DeleteAllReadLaterUseCase.Request).first()
        }
    }
}
