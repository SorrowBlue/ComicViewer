package com.sorrowblue.comicviewer.domain.service.interactor.paging

import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.service.datasource.BookshelfLocalDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.FileLocalDataSource
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingQueryFileUseCase
import di.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import org.koin.core.annotation.Singleton

@Singleton
internal class PagingQueryFileInteractor @Inject constructor(
    private val bookshelfLocalDataSource: BookshelfLocalDataSource,
    private val fileLocalDataSource: FileLocalDataSource,
) : PagingQueryFileUseCase() {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun run(request: Request): Flow<PagingData<File>> {
        return bookshelfLocalDataSource.flow(request.bookshelfId).flatMapLatest {
            fileLocalDataSource.pagingSource(
                request.pagingConfig,
                request.bookshelfId,
                request.searchCondition
            )
        }
    }
}
