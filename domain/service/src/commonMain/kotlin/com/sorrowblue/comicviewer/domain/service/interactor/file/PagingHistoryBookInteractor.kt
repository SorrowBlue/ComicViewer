package com.sorrowblue.comicviewer.domain.service.interactor.file

import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.service.datasource.FileLocalDataSource
import com.sorrowblue.comicviewer.domain.usecase.file.PagingHistoryBookUseCase
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow

@Inject
internal class PagingHistoryBookInteractor(private val fileLocalDataSource: FileLocalDataSource) :
    PagingHistoryBookUseCase() {
    override fun run(request: Request): Flow<PagingData<Book>> =
        fileLocalDataSource.pagingHistoryBookSource(request.pagingConfig)
}
