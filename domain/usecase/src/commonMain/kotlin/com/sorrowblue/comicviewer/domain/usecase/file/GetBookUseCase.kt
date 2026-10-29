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
import com.sorrowblue.comicviewer.domain.model.storage.RemoteException
import com.sorrowblue.comicviewer.domain.repository.BookshelfRepository
import com.sorrowblue.comicviewer.domain.repository.FileRepository
import com.sorrowblue.comicviewer.domain.repository.FileRepositoryQueryError
import com.sorrowblue.comicviewer.domain.repository.SettingsRepository
import com.sorrowblue.comicviewer.domain.repository.storage.RemoteStorageClient
import com.sorrowblue.comicviewer.domain.usecase.UseCase
import com.sorrowblue.comicviewer.domain.usecase.runCatchingCancellable
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import logcat.LogPriority
import logcat.asLog
import logcat.logcat

/**
 * Retrieves a [Book] from remote storage, resolves its page count, and updates the local database.
 */
abstract class GetBookUseCase : UseCase<GetBookUseCase.Request, Book, GetBookUseCase.Error>() {

    /**
     * Request parameters for [GetBookUseCase].
     *
     * @property bookshelfId The ID of the bookshelf containing the book.
     * @property path The path of the book.
     */
    data class Request(val bookshelfId: BookshelfId, val path: String)

    /**
     * Errors that can occur when retrieving a book.
     */
    sealed interface Error : Resource.AppError {
        /**
         * Indicates that the bookshelf or book was not found.
         */
        data object NotFound : Error

        /**
         * Indicates that a system or remote error occurred and was logged.
         */
        data object ReportedSystemError : Error
    }
}

@Inject
@ContributesBinding(AppScope::class)
internal class GetBookUseCaseImpl(
    private val bookshelfRepository: BookshelfRepository,
    private val fileRepository: FileRepository,
    private val remoteStorageClientFactory: RemoteStorageClient.Factory,
    private val settingsRepository: SettingsRepository,
) : GetBookUseCase() {

    override fun run(request: Request): Flow<Resource<Book, Error>> =
        bookshelfRepository.flow(request.bookshelfId)
            .distinctUntilChanged()
            .map<Bookshelf?, Resource<Book, Error>> {
                if (it != null) {
                    fetch(it, request.path)
                } else {
                    Resource.Error(Error.NotFound)
                }
            }.catch { e ->
                if (e is CancellationException) throw e
                logcat(LogPriority.ERROR) { e.asLog() }
                emit(Resource.Error(Error.ReportedSystemError))
            }

    private suspend fun fetch(bookshelf: Bookshelf, path: String): Resource<Book, Error> {
        logcat { "fetch(): bookshelf=$bookshelf, path=$path" }
        val remoteStorageClient = remoteStorageClientFactory.create(bookshelf)
        val resolveImageFolder =
            settingsRepository.folderSettings.first().resolveImageFolder
        val remoteBook = when (
            val result = fetchRemoteBook(remoteStorageClient, path, resolveImageFolder)
        ) {
            is Resource.Success -> result.data
            is Resource.Error -> return result
        }
        val totalPageCount = runCatchingCancellable {
            remoteStorageClient.pageCount(remoteBook)
        }.getOrElse { error ->
            return when (error) {
                is RemoteException.NotFound -> Resource.Error(Error.NotFound)

                else -> {
                    logcat(LogPriority.ERROR) { error.asLog() }
                    // TODO(SorrowBlue): Report Error
                    Resource.Error(Error.ReportedSystemError)
                }
            }
        }
        logcat { "totalPageCount: $totalPageCount" }
        val updatedBook = when (remoteBook) {
            is BookFile -> remoteBook.copy(totalPageCount = totalPageCount)
            is BookFolder -> remoteBook.copy(totalPageCount = totalPageCount)
        }
        return fileRepository.updateSimple(updatedBook).fold(
            onSuccess = {
                when (it) {
                    is Book -> Resource.Success(it)
                    is Folder -> Resource.Error(Error.NotFound)
                }
            },
            onError = {
                when (it) {
                    FileRepositoryQueryError.NotFound -> Resource.Error(Error.NotFound)

                    is FileRepositoryQueryError.SystemError -> {
                        if (it.throwable is CancellationException) throw it.throwable
                        logcat(LogPriority.ERROR) { it.throwable.asLog() }
                        // TODO(SorrowBlue): Report Error
                        Resource.Error(Error.ReportedSystemError)
                    }
                }
            },
        )
    }

    private suspend fun fetchRemoteBook(
        remoteStorageClient: RemoteStorageClient,
        path: String,
        resolveImageFolder: Boolean,
    ): Resource<Book, Error> = runCatchingCancellable {
        remoteStorageClient.file(path, resolveImageFolder) as? Book
    }.fold(
        onSuccess = { book ->
            if (book != null) {
                Resource.Success(book)
            } else {
                Resource.Error(Error.NotFound)
            }
        },
        onFailure = { error ->
            when (error) {
                is RemoteException.NotFound -> Resource.Error(Error.NotFound)

                else -> {
                    logcat(LogPriority.ERROR) { error.asLog() }
                    Resource.Error(Error.ReportedSystemError)
                }
            }
        },
    )
}
