package com.sorrowblue.comicviewer.domain.service.interactor.paging

import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.service.repository.BookshelfRepository
import com.sorrowblue.comicviewer.domain.service.repository.FileRepository
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingQueryFileUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

internal class PagingQueryFileInteractor @Inject constructor(
    private val bookshelfRepository: BookshelfRepository,
    private val fileRepository: FileRepository,
) : PagingQueryFileUseCase() {

    override fun run(request: Request): Flow<PagingData<File>> {
        val bookshelf = runBlocking {
            (bookshelfRepository.find(request.bookshelfId).first() as Resource.Success).data
        }
        return fileRepository.pagingDataFlow(
            request.pagingConfig,
            bookshelf,
            request.searchCondition
        )
    }
}
