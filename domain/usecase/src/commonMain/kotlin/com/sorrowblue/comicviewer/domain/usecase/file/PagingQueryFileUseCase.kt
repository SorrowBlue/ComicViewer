/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.file

import androidx.paging.PagingConfig
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.search.SearchCondition
import com.sorrowblue.comicviewer.domain.repository.FileRepository
import com.sorrowblue.comicviewer.domain.usecase.PagingUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

/**
 * Observes paginated [File] items matching a [SearchCondition] on a bookshelf.
 */
fun interface PagingQueryFileUseCase : PagingUseCase<PagingQueryFileUseCase.Request, File> {

    /**
     * Request parameters for [PagingQueryFileUseCase].
     *
     * @property bookshelfId The ID of the bookshelf to search.
     * @property searchCondition Provider for the search condition.
     * @property pagingConfig Configuration for pagination.
     */
    class Request(
        val bookshelfId: BookshelfId,
        val searchCondition: () -> SearchCondition,
        val pagingConfig: PagingConfig,
    )
}

@Inject
@ContributesBinding(AppScope::class)
internal class PagingQueryFileUseCaseImpl(private val fileRepository: FileRepository) :
    PagingQueryFileUseCase {

    override fun invoke(request: PagingQueryFileUseCase.Request) = fileRepository.pagingDataFlow(
        request.pagingConfig,
        request.bookshelfId,
        request.searchCondition,
    )
}
