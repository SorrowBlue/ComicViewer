package com.sorrowblue.comicviewer.domain.service.interactor.paging

import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.service.datasource.FileLocalDataSource
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingHistoryBookUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

internal class PagingHistoryBookInteractor @Inject constructor(
    private val fileLocalDataSource: FileLocalDataSource,
) : PagingHistoryBookUseCase() {

    override fun run(request: Request): Flow<PagingData<Book>> {
        return fileLocalDataSource.pagingHistoryBookSource(request.pagingConfig)
    }
}
