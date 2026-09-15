/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.file

import com.sorrowblue.comicviewer.domain.model.bookshelf.ShareContents
import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.model.file.BookFile
import com.sorrowblue.comicviewer.domain.repository.storage.RemoteStorageClient
import com.sorrowblue.comicviewer.domain.usecase.OneShotUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

abstract class GetIntentBookUseCase : OneShotUseCase<String, BookFile, Unit>()

@Inject
@ContributesBinding(AppScope::class)
internal class GetIntentBookUseCaseImpl(
    private val remoteStorageClientFactory: RemoteStorageClient.Factory,
) : GetIntentBookUseCase() {

    override suspend fun run(request: String): Resource<BookFile, Unit> {
        val bookshelf = ShareContents
        val remoteStorageClient = remoteStorageClientFactory.create(bookshelf)
        var book = remoteStorageClient.file(request)
        book = book as BookFile
        book = book.copy(totalPageCount = remoteStorageClient.pageCount(book))
        return Resource.Success(book)
    }
}
