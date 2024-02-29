package com.sorrowblue.comicviewer.feature.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.usecase.file.AddReadLaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingHistoryBookUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@HiltViewModel
internal class HistoryViewModel @Inject constructor(
    pagingHistoryBookUseCase: PagingHistoryBookUseCase,
    private val addReadLaterUseCase: AddReadLaterUseCase,
) : ViewModel() {

    val pagingDataFlow = pagingHistoryBookUseCase
        .execute(PagingHistoryBookUseCase.Request(PagingConfig(20)))
        .cachedIn(viewModelScope)

    fun addToReadLater(file: File) {
        viewModelScope.launch {
            addReadLaterUseCase.execute(AddReadLaterUseCase.Request(file.bookshelfId, file.path))
                .first()
        }
    }
}
