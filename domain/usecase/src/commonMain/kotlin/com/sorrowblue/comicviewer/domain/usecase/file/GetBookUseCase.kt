/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.file

import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.model.common.fold
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.BookFile
import com.sorrowblue.comicviewer.domain.model.file.BookFolder
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.domain.repository.BookshelfRepository
import com.sorrowblue.comicviewer.domain.repository.FileRepository
import com.sorrowblue.comicviewer.domain.repository.FileRepositoryQueryError
import com.sorrowblue.comicviewer.domain.repository.storage.RemoteStorageClient
import com.sorrowblue.comicviewer.domain.usecase.UseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageFolderSettingsUseCase
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import logcat.asLog
import logcat.logcat

@Inject
class GetBookUseCase(
    private val bookshelfRepository: BookshelfRepository,
    private val fileRepository: FileRepository,
    private val remoteStorageClientFactory: RemoteStorageClient.Factory,
    private val manageFolderSettingsUseCase: ManageFolderSettingsUseCase,
) : UseCase<GetBookUseCase.Request, Book, GetBookUseCase.Error>() {

    data class Request(val bookshelfId: BookshelfId, val path: String) : UseCase.Request

    sealed interface Error : Resource.IError {
        data object NotFound : Error

        data object ReportedSystemError : Error
    }

    override fun run(request: Request): Flow<Resource<Book, Error>> =
        bookshelfRepository.flow(request.bookshelfId).map {
            if (it != null) {
                fetch(it, request.path)
            } else {
                Resource.Error(Error.NotFound)
            }
        }

    private suspend fun fetch(bookshelf: Bookshelf, path: String): Resource<Book, Error> {
        logcat { "fetch(): bookshelf=$bookshelf, path=$path" }
        val remoteStorageClient = remoteStorageClientFactory.create(bookshelf)
        val resolveImageFolder =
            manageFolderSettingsUseCase.settings.first().resolveImageFolder
        val localFile =
            runCatching {
                when (val file = remoteStorageClient.file(path, resolveImageFolder)) {
                    is BookFile -> fileRepository.updateSimple(file)
                    is BookFolder -> fileRepository.updateSimple(file)
                    is Folder -> return Resource.Error(Error.NotFound)
                }
            }.getOrElse {
                return Resource.Error(Error.NotFound)
            }
        return localFile.fold(
            onSuccess = {
                when (it) {
                    is BookFile -> updateTotalPageCount(remoteStorageClient, it)
                    is BookFolder -> updateTotalPageCount(remoteStorageClient, it)
                    is Folder -> Resource.Error(Error.NotFound)
                }
            },
            onError = {
                when (it) {
                    FileRepositoryQueryError.NotFound -> Resource.Error(Error.NotFound)

                    is FileRepositoryQueryError.SystemError -> {
                        // TODO Report Error
                        Resource.Error(Error.ReportedSystemError)
                    }
                }
            },
        )
    }

    private suspend fun updateTotalPageCount(
        remoteStorageClient: RemoteStorageClient,
        book: Book,
    ): Resource<Book, Error> {
        logcat { "updateTotalPageCount()" }
        return kotlin
            .runCatching {
                remoteStorageClient.pageCount(book).let { totalPageCount ->
                    logcat { "totalPageCount: $totalPageCount" }
                    when (book) {
                        is BookFile -> book.copy(totalPageCount = totalPageCount)
                        is BookFolder -> book.copy(totalPageCount = totalPageCount)
                    }.also {
                        fileRepository.addUpdate(it)
                    }
                }
            }.fold(
                onSuccess = {
                    Resource.Success(it)
                },
                onFailure = {
                    logcat { it.asLog() }
                    // TODO Report Error
                    Resource.Error(Error.ReportedSystemError)
                },
            )
    }
}
