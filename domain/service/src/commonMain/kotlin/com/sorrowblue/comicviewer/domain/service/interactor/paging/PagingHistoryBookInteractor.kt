package com.sorrowblue.comicviewer.domain.service.interactor.paging

import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.service.datasource.FileLocalDataSource
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingHistoryBookUseCase
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Singleton

@Singleton
internal class PagingHistoryBookInteractor(
    private val fileLocalDataSource: FileLocalDataSource,
) : PagingHistoryBookUseCase() {

    override fun run(request: Request): Flow<PagingData<Book>> {
        return fileLocalDataSource.pagingHistoryBookSource(request.pagingConfig)
    }
}
