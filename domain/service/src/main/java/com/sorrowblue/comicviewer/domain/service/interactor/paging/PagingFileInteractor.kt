package com.sorrowblue.comicviewer.domain.service.interactor.paging

import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.IFolder
import com.sorrowblue.comicviewer.domain.service.repository.BookshelfRepository
import com.sorrowblue.comicviewer.domain.service.repository.FileRepository
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingFileUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

internal class PagingFileInteractor @Inject constructor(
    private val bookshelfRepository: BookshelfRepository,
    private val fileRepository: FileRepository,
) : PagingFileUseCase() {
    override fun run(request: Request): Flow<Resource<Flow<PagingData<File>>, Error>> {
        return bookshelfRepository.find(request.bookshelfId).combine(
            fileRepository.find(
                request.bookshelfId,
                request.path
            )
        ) { bookshelfResource, file ->
            val bookshelf = when (bookshelfResource) {
                is Resource.Error -> return@combine Resource.Error(Error.NOT_FOUND)
                is Resource.Success -> bookshelfResource.data
            }
            val folder: IFolder = when (file) {
                is Resource.Error -> return@combine Resource.Error(Error.NOT_FOUND)
                is Resource.Success ->
                    file.data as? IFolder
                        ?: return@combine Resource.Error(Error.NOT_FOUND)
            }
            Resource.Success(fileRepository.pagingDataFlow(request.pagingConfig, bookshelf, folder))
        }
    }
}
