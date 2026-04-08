package com.sorrowblue.comicviewer.domain.service.interactor.file

import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.service.datasource.FileLocalDataSource
import com.sorrowblue.comicviewer.domain.usecase.file.PagingHistoryBookUseCase
import com.sorrowblue.comicviewer.framework.common.scope.DataScope
import dev.zacsweers.metro.ContributesBinding
import kotlinx.coroutines.flow.Flow

@ContributesBinding(DataScope::class)
internal class PagingHistoryBookInteractor(private val fileLocalDataSource: FileLocalDataSource) :
    PagingHistoryBookUseCase() {
    override fun run(request: Request): Flow<PagingData<Book>> =
        fileLocalDataSource.pagingHistoryBookSource(request.pagingConfig)
}
