package com.sorrowblue.comicviewer.domain.service.interactor.file

import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.file.FileAttribute
import com.sorrowblue.comicviewer.domain.model.fold
import com.sorrowblue.comicviewer.domain.service.repository.BookshelfRepository
import com.sorrowblue.comicviewer.domain.usecase.file.GetFileAttributeUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

internal class GetFileAttributeInteractor @Inject constructor(
    private val fileRepository: BookshelfRepository,
) : GetFileAttributeUseCase() {
    override fun run(request: Request): Flow<Resource<FileAttribute?, Error>> {
        return fileRepository.get(request.bookshelfId).flatMapLatest {
            it.dataOrNull?.let {
                fileRepository.getAttribute(it, request.path).map { result ->
                    result.fold({
                        it?.let { Resource.Success(it) } ?: Resource.Error(Error.NotFound)
                    }, {
                        Resource.Error(Error.NotFound)
                    })
                }
            } ?: flowOf(Resource.Error(Error.NotFound))
        }
    }
}
