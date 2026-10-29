/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.file

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.repository.FileRepository
import com.sorrowblue.comicviewer.domain.usecase.UseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import logcat.LogPriority
import logcat.asLog
import logcat.logcat

/**
 * Observes a [File] from the local database by its bookshelf ID and path.
 */
abstract class GetFileUseCase : UseCase<GetFileUseCase.Request, File, GetFileUseCase.Error>() {

    /**
     * Request parameters for [GetFileUseCase].
     *
     * @property bookshelfId The ID of the bookshelf containing the file.
     * @property path The path of the file.
     */
    data class Request(val bookshelfId: BookshelfId, val path: String)

    /**
     * Errors that can occur when observing a file.
     */
    enum class Error : Resource.AppError {
        /**
         * Indicates that the requested file was not found.
         */
        NotFound,

        /**
         * Indicates that a system error occurred and was logged.
         */
        ReportedSystemError,
    }
}

@Inject
@ContributesBinding(AppScope::class)
internal class GetFileUseCaseImpl(private val fileRepository: FileRepository) : GetFileUseCase() {

    override fun run(request: Request): Flow<Resource<File, Error>> =
        fileRepository.flow(request.bookshelfId, request.path)
            .distinctUntilChanged()
            .map<File?, Resource<File, Error>> { file ->
                if (file != null) {
                    Resource.Success(file)
                } else {
                    Resource.Error(Error.NotFound)
                }
            }.catch { e ->
                if (e is CancellationException) throw e
                logcat(LogPriority.ERROR) { e.asLog() }
                emit(Resource.Error(Error.ReportedSystemError))
            }
}
