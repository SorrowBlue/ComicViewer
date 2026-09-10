/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.service.interactor.file

import com.sorrowblue.comicviewer.domain.model.bookshelf.ShareContents
import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.model.file.BookFile
import com.sorrowblue.comicviewer.domain.service.storage.RemoteStorageClient
import com.sorrowblue.comicviewer.domain.usecase.file.GetIntentBookUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import logcat.logcat

@ContributesBinding(AppScope::class)
internal class GetIntentBookInteractor(
    private val remoteStorageClientFactory: RemoteStorageClient.Factory,
) : GetIntentBookUseCase() {
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
