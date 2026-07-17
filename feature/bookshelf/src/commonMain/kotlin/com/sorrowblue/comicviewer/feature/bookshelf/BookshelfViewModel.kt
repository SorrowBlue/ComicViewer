/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.bookshelf

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.PagingBookshelfFolderUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ViewModelKey

private const val PageSize = 20

@ViewModelKey
@ContributesIntoMap(AppScope::class)
internal class BookshelfViewModel(pagingBookshelfFolderUseCase: PagingBookshelfFolderUseCase) :
    ViewModel() {

    val bookshelfPagingFlow =
        pagingBookshelfFolderUseCase(PagingBookshelfFolderUseCase.Request(PagingConfig(PageSize)))
            .cachedIn(viewModelScope)
}
