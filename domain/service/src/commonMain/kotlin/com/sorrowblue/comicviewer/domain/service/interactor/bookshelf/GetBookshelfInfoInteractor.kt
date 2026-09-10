/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.service.interactor.bookshelf

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.repository.BookshelfRepository
import com.sorrowblue.comicviewer.domain.repository.FileRepository
import com.sorrowblue.comicviewer.domain.usecase.SendFatalErrorUseCase
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.GetBookshelfInfoUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@ContributesBinding(AppScope::class)
internal class GetBookshelfInfoInteractor(
    private val bookshelfRepository: BookshelfRepository,
    private val fileRepository: FileRepository,
    private val sendFatalErrorUseCase: SendFatalErrorUseCase,
) : GetBookshelfInfoUseCase() {
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
