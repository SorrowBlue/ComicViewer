/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.service.interactor.bookshelf

import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfFolder
import com.sorrowblue.comicviewer.domain.repository.BookshelfRepository
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.PagingBookshelfFolderUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import kotlinx.coroutines.flow.Flow

@ContributesBinding(AppScope::class)
internal class PagingBookshelfFolderInteractor(
    private val bookshelfRepository: BookshelfRepository,
) : PagingBookshelfFolderUseCase() {
    override fun run(request: Request): Flow<PagingData<BookshelfFolder>> =
        bookshelfRepository.pagingSource(request.pagingConfig)
}
