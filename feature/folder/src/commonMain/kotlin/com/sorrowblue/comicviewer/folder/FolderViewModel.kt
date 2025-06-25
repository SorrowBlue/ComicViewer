package com.sorrowblue.comicviewer.folder

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.usecase.file.PagingFileUseCase
import org.koin.android.annotation.KoinViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.InjectedParam
import org.koin.core.parameter.parametersOf

@KoinViewModel
internal class FolderViewModel(
    @InjectedParam bookshelfId: BookshelfId,
    @InjectedParam path: String,
    pagingFileUseCase: PagingFileUseCase,
) : ViewModel() {

    companion object {
        @Composable
        fun koinViewModel(bookshelfId: BookshelfId, path: String) = koinViewModel<FolderViewModel> {
            parametersOf(
                bookshelfId,
                path
            )
        }
    }

    val pagingDataFlow =
        pagingFileUseCase(PagingFileUseCase.Request(PagingConfig(40), bookshelfId, path))
            .cachedIn(viewModelScope)
}
