/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.service.interactor.file

import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.repository.BookshelfRepository
import com.sorrowblue.comicviewer.domain.service.datasource.FileLocalDataSource
import com.sorrowblue.comicviewer.domain.usecase.file.PagingQueryFileUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest

@ContributesBinding(AppScope::class)
internal class PagingQueryFileInteractor(
    private val bookshelfRepository: BookshelfRepository,
    private val fileLocalDataSource: FileLocalDataSource,
) : PagingQueryFileUseCase() {
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun run(request: Request): Flow<PagingData<File>> =
        bookshelfRepository.flow(request.bookshelfId).flatMapLatest {
            fileLocalDataSource.pagingDataFlow(
                request.pagingConfig,
                request.bookshelfId,
                request.searchCondition,
            )
        }
}
