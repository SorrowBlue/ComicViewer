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
import com.sorrowblue.comicviewer.domain.usecase.runCatchingCancellable
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageFolderSettingsUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import logcat.LogPriority
import logcat.asLog
import logcat.logcat

abstract class GetBookUseCase : UseCase<GetBookUseCase.Request, Book, GetBookUseCase.Error>() {

    data class Request(val bookshelfId: BookshelfId, val path: String)

    sealed interface Error : Resource.IError {
        data object NotFound : Error

        data object ReportedSystemError : Error
    }
}

@Inject
@ContributesBinding(AppScope::class)
internal class GetBookUseCaseImpl(
    private val bookshelfRepository: BookshelfRepository,
    private val fileRepository: FileRepository,
    private val remoteStorageClientFactory: RemoteStorageClient.Factory,
    private val manageFolderSettingsUseCase: ManageFolderSettingsUseCase,
) : GetBookUseCase() {

    override fun run(request: Request): Flow<Resource<Book, Error>> =
        bookshelfRepository.flow(request.bookshelfId).map<Bookshelf?, Resource<Book, Error>> {
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
            manageFolderSettingsUseCase.settings.first().resolveImageFolder
        val remoteBook = runCatchingCancellable {
            remoteStorageClient.file(path, resolveImageFolder) as? Book
        }.getOrNull() ?: return Resource.Error(Error.NotFound)
        val totalPageCount = runCatchingCancellable {
            remoteStorageClient.pageCount(remoteBook)
        }.getOrElse {
            logcat { it.asLog() }
            // TODO Report Error
            return Resource.Error(Error.ReportedSystemError)
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
                        // TODO Report Error
                        Resource.Error(Error.ReportedSystemError)
                    }
                }
            },
        )
    }
}
