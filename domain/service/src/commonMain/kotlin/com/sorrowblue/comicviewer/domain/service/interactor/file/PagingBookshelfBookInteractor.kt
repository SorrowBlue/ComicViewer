package com.sorrowblue.comicviewer.domain.service.interactor.file

import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.file.BookThumbnail
import com.sorrowblue.comicviewer.domain.service.datasource.FileLocalDataSource
import com.sorrowblue.comicviewer.domain.usecase.file.PagingBookshelfBookUseCase
import com.sorrowblue.comicviewer.framework.common.scope.DataScope
import dev.zacsweers.metro.ContributesBinding
import kotlinx.coroutines.flow.Flow

@ContributesBinding(DataScope::class)
internal class PagingBookshelfBookInteractor(private val fileLocalDataSource: FileLocalDataSource) :
    PagingBookshelfBookUseCase() {
    override fun run(request: Request): Flow<PagingData<BookThumbnail>> =
        fileLocalDataSource.pagingSource(request.bookshelfId, request.pagingConfig)
}
