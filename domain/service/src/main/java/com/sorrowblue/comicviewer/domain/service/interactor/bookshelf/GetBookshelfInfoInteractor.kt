package com.sorrowblue.comicviewer.domain.service.interactor.bookshelf

import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.domain.service.repository.BookshelfRepository
import com.sorrowblue.comicviewer.domain.service.repository.FileRepository
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.GetBookshelfInfoUseCase
import com.sorrowblue.comicviewer.framework.Resource
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

internal class GetBookshelfInfoInteractor @Inject constructor(
    private val bookshelfRepository: BookshelfRepository,
    private val fileRepository: FileRepository,
) : GetBookshelfInfoUseCase() {

    override fun run(request: Request): Flow<Resource<BookshelfFolder, Error>> {
        return bookshelfRepository.find(request.bookshelfId).flatMapLatestFold({ bookshelf ->
            fileRepository.findByParent(bookshelf.id, "").mapFold({
                if (it is Folder) {
                    Resource.Success(BookshelfFolder(bookshelf to it))
                } else {
                    Resource.Error(Error.NotFound)
                }
            }, {
                Resource.Error(Error.System)
            })
        }, {
            flowOf(
                when (it) {
                    BookshelfRepository.Error.Network -> Resource.Error(Error.System)
                    BookshelfRepository.Error.NotFound -> Resource.Error(Error.System)
                    BookshelfRepository.Error.System -> Resource.Error(Error.System)
                }
            )
        })
    }
}

private fun <D, E : Resource.AppError, DR, ER : Resource.AppError> Flow<Resource<D, E>>.flatMapLatestFold(
    onSuccess: suspend (D) -> Flow<Resource<DR, ER>>,
    onError: suspend (E) -> Flow<Resource<DR, ER>>
): Flow<Resource<DR, ER>> {
    return flatMapLatest {
        when (it) {
            is Resource.Error -> onError(it.error)
            is Resource.Success -> onSuccess(it.data)
        }
    }
}


private fun <D, E : Resource.AppError, DR, ER : Resource.AppError> Flow<Resource<D, E>>.mapFold(
    onSuccess: suspend (D) -> Resource<DR, ER>,
    onError: suspend (E) -> Resource<DR, ER>
): Flow<Resource<DR, ER>> {
    return map {
        when (it) {
            is Resource.Error -> onError(it.error)
            is Resource.Success -> onSuccess(it.data)
        }
    }
}
