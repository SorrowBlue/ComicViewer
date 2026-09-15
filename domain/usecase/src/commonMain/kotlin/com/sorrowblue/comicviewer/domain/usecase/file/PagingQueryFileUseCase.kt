/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.file

import androidx.paging.PagingConfig
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.search.SearchCondition
import com.sorrowblue.comicviewer.domain.repository.BookshelfRepository
import com.sorrowblue.comicviewer.domain.repository.FileRepository
import com.sorrowblue.comicviewer.domain.usecase.PagingUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest

fun interface PagingQueryFileUseCase : PagingUseCase<PagingQueryFileUseCase.Request, File> {

    class Request(
        val bookshelfId: BookshelfId,
        val searchCondition: () -> SearchCondition,
        val pagingConfig: PagingConfig,
    )
}

@Inject
@ContributesBinding(AppScope::class)
internal class PagingQueryFileUseCaseImpl(
    private val bookshelfRepository: BookshelfRepository,
    private val fileRepository: FileRepository,
) : PagingQueryFileUseCase {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun invoke(request: PagingQueryFileUseCase.Request) =
        bookshelfRepository.flow(request.bookshelfId).flatMapLatest {
            fileRepository.pagingDataFlow(
                request.pagingConfig,
                request.bookshelfId,
                request.searchCondition,
            )
        }
}
