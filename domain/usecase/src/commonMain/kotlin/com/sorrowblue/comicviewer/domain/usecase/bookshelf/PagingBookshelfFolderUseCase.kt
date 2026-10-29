/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.bookshelf

import androidx.paging.PagingConfig
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfFolder
import com.sorrowblue.comicviewer.domain.repository.BookshelfRepository
import com.sorrowblue.comicviewer.domain.usecase.PagingUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

/**
 * Observes paginated [BookshelfFolder] items representing registered bookshelves and their root
 * folders.
 */
fun interface PagingBookshelfFolderUseCase :
    PagingUseCase<PagingBookshelfFolderUseCase.Request, BookshelfFolder> {

    /**
     * Request parameters for [PagingBookshelfFolderUseCase].
     *
     * @property pagingConfig Configuration for pagination.
     */
    data class Request(val pagingConfig: PagingConfig)
}

@Inject
@ContributesBinding(AppScope::class)
internal class PagingBookshelfFolderUseCaseImpl(
    private val bookshelfRepository: BookshelfRepository,
) : PagingBookshelfFolderUseCase {

    override fun invoke(request: PagingBookshelfFolderUseCase.Request) =
        bookshelfRepository.pagingSource(request.pagingConfig)
}
