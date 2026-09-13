/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.bookshelf

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.repository.BookshelfRepository
import com.sorrowblue.comicviewer.domain.repository.FileRepository
import com.sorrowblue.comicviewer.domain.usecase.SendFatalErrorUseCase
import com.sorrowblue.comicviewer.domain.usecase.UseCase
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Inject
class GetBookshelfInfoUseCase(
    private val bookshelfRepository: BookshelfRepository,
    private val fileRepository: FileRepository,
    private val sendFatalErrorUseCase: SendFatalErrorUseCase,
) : UseCase<GetBookshelfInfoUseCase.Request, BookshelfFolder, GetBookshelfInfoUseCase.Error>() {

    data class Request(val bookshelfId: BookshelfId) : UseCase.Request

    sealed interface Error : Resource.AppError {
        data object NotFound : Error

        data object System : Error
    }

    override fun run(request: Request): Flow<Resource<BookshelfFolder, Error>> =
        bookshelfRepository.flow(request.bookshelfId).map { bookshelf ->
            if (bookshelf != null) {
                val folder = fileRepository.root(request.bookshelfId)
                if (folder != null) {
                    Resource.Success(BookshelfFolder(bookshelf, folder))
                } else {
                    sendFatalErrorUseCase(
                        SendFatalErrorUseCase.Request(RuntimeException("NotFound")),
                    )
                    Resource.Error(Error.NotFound)
                }
            } else {
                sendFatalErrorUseCase(SendFatalErrorUseCase.Request(RuntimeException("NotFound")))
                Resource.Error(Error.NotFound)
            }
        }
}
