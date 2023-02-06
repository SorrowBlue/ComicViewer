package com.sorrowblue.comicviewer.bookshelf

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.sorrowblue.comicviewer.domain.entity.BookshelfFolder
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingBookshelfFolderUseCase
import com.sorrowblue.comicviewer.framework.ui.fragment.PagingViewModel
import com.sorrowblue.comicviewer.framework.ui.navigation.SupportSafeArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class BookshelfListViewModel @Inject constructor(
    pagingBookshelfFolderUseCase: PagingBookshelfFolderUseCase,
    override val savedStateHandle: SavedStateHandle
) : PagingViewModel<BookshelfFolder>(), SupportSafeArgs {

    override val transitionName = null
    override val pagingDataFlow =
        pagingBookshelfFolderUseCase.execute(PagingBookshelfFolderUseCase.Request(PagingConfig(10)))
            .cachedIn(viewModelScope)
}
