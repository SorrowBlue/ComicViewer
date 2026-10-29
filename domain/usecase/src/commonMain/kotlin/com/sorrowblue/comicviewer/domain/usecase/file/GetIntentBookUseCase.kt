/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.file

import com.sorrowblue.comicviewer.domain.model.bookshelf.ShareContents
import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.model.file.BookFile
import com.sorrowblue.comicviewer.domain.repository.storage.RemoteStorageClient
import com.sorrowblue.comicviewer.domain.usecase.OneShotUseCase
import com.sorrowblue.comicviewer.domain.usecase.runCatchingCancellable
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

/**
 * Resolves a [BookFile] and its total page count from an external intent URI or path.
 */
abstract class GetIntentBookUseCase : OneShotUseCase<String, BookFile, Unit>()

@Inject
@ContributesBinding(AppScope::class)
internal class GetIntentBookUseCaseImpl(
    private val remoteStorageClientFactory: RemoteStorageClient.Factory,
) : GetIntentBookUseCase() {

    override suspend fun run(request: String): Resource<BookFile, Unit> {
        val bookshelf = ShareContents
        val remoteStorageClient = remoteStorageClientFactory.create(bookshelf)
        return runCatchingCancellable {
            val file = remoteStorageClient.file(request)
            if (file is BookFile) {
                val totalPageCount = remoteStorageClient.pageCount(file)
                Resource.Success(file.copy(totalPageCount = totalPageCount))
            } else {
                Resource.Error(Unit)
            }
        }.getOrElse {
            Resource.Error(Unit)
        }
    }
}
