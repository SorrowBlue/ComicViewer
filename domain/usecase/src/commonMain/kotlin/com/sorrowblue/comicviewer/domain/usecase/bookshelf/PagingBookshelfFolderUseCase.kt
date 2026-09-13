/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.bookshelf

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.BaseRequest
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfFolder
import com.sorrowblue.comicviewer.domain.repository.BookshelfRepository
import com.sorrowblue.comicviewer.domain.usecase.PagingUseCase
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow

@Inject
class PagingBookshelfFolderUseCase(private val bookshelfRepository: BookshelfRepository) :
    PagingUseCase<PagingBookshelfFolderUseCase.Request, BookshelfFolder>() {

    class Request(val pagingConfig: PagingConfig) : BaseRequest

    override fun run(request: Request): Flow<PagingData<BookshelfFolder>> =
        bookshelfRepository.pagingSource(request.pagingConfig)
}
