/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.file

import com.sorrowblue.comicviewer.domain.model.bookshelf.ShareContents
import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.model.file.BookFile
import com.sorrowblue.comicviewer.domain.repository.storage.RemoteStorageClient
import com.sorrowblue.comicviewer.domain.usecase.UseCase
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import logcat.logcat

@Inject
class GetIntentBookUseCase(private val remoteStorageClientFactory: RemoteStorageClient.Factory) :
    UseCase<GetIntentBookUseCase.Request, BookFile, GetIntentBookUseCase.Error>() {

    data class Request(val data: String) : UseCase.Request

    enum class Error : Resource.AppError {
        System,
    }

    override fun run(request: Request): Flow<Resource<BookFile, Error>> {
        val bookshelf = ShareContents
        val remoteStorageClient = remoteStorageClientFactory.create(bookshelf)
        return flow {
            logcat { "request.data=${request.data}" }
            var book = remoteStorageClient.file(request.data)
            logcat { "book=$book" }
            book = book as BookFile
            book = book.copy(totalPageCount = remoteStorageClient.pageCount(book))
            val resource = Resource.Success(book)
            emit(resource)
        }
    }
}
