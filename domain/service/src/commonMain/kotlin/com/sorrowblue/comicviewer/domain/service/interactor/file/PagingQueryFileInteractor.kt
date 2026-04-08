package com.sorrowblue.comicviewer.domain.service.interactor.file

import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.service.datasource.BookshelfLocalDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.FileLocalDataSource
import com.sorrowblue.comicviewer.domain.usecase.file.PagingQueryFileUseCase
import com.sorrowblue.comicviewer.framework.common.scope.DataScope
import dev.zacsweers.metro.ContributesBinding
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest

@ContributesBinding(DataScope::class)
internal class PagingQueryFileInteractor(
    private val bookshelfLocalDataSource: BookshelfLocalDataSource,
    private val fileLocalDataSource: FileLocalDataSource,
) : PagingQueryFileUseCase() {
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun run(request: Request): Flow<PagingData<File>> =
        bookshelfLocalDataSource.flow(request.bookshelfId).flatMapLatest {
            fileLocalDataSource.pagingDataFlow(
                request.pagingConfig,
                request.bookshelfId,
                request.searchCondition,
            )
        }
}
