/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.bookshelf

import androidx.paging.PagingConfig
import com.sorrowblue.comicviewer.domain.BaseRequest
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfFolder
import com.sorrowblue.comicviewer.domain.repository.BookshelfRepository
import com.sorrowblue.comicviewer.domain.usecase.PagingUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

fun interface PagingBookshelfFolderUseCase :
    PagingUseCase<PagingBookshelfFolderUseCase.Request, BookshelfFolder> {

    data class Request(val pagingConfig: PagingConfig) : BaseRequest
}

@Inject
@ContributesBinding(AppScope::class)
internal class PagingBookshelfFolderUseCaseImpl(
    private val bookshelfRepository: BookshelfRepository,
) : PagingBookshelfFolderUseCase {

    override fun invoke(request: PagingBookshelfFolderUseCase.Request) =
        bookshelfRepository.pagingSource(request.pagingConfig)
}
