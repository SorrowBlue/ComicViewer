package com.sorrowblue.comicviewer.bookshelf

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.Scan
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.file.IFolder
import com.sorrowblue.comicviewer.domain.usecase.ScanBookshelfUseCase
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.RemoveBookshelfUseCase
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingBookshelfFolderUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@HiltViewModel
internal class BookshelfViewModel @Inject constructor(
    pagingBookshelfFolderUseCase: PagingBookshelfFolderUseCase,
    private val removeBookshelfUseCase: RemoveBookshelfUseCase,
    private val scanBookshelfUseCase: ScanBookshelfUseCase,
) : ViewModel() {

    fun scan(folder: IFolder) {
        viewModelScope.launch {
            scanBookshelfUseCase.execute(ScanBookshelfUseCase.Request(folder, Scan.ALL)).first()
        }
    }

    val pagingDataFlow: Flow<PagingData<BookshelfFolder>> =
        pagingBookshelfFolderUseCase.execute(PagingBookshelfFolderUseCase.Request(PagingConfig(20)))
            .cachedIn(viewModelScope)

    fun remove(bookshelf: Bookshelf) {
        viewModelScope.launch {
            removeBookshelfUseCase.execute(RemoveBookshelfUseCase.Request(bookshelf))
        }
    }
}
