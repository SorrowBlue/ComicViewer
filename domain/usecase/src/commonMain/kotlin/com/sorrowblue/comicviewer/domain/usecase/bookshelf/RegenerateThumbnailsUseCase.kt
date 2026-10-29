/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.bookshelf

import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.common.IoDispatcher
import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.model.common.onError
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.FileThumbnail
import com.sorrowblue.comicviewer.domain.repository.BookshelfRepository
import com.sorrowblue.comicviewer.domain.repository.FileRepository
import com.sorrowblue.comicviewer.domain.repository.ThumbnailRepository
import com.sorrowblue.comicviewer.domain.usecase.OneShotUseCase
import com.sorrowblue.comicviewer.domain.usecase.limitedCoroutineScope
import com.sorrowblue.comicviewer.domain.usecase.runCatchingCancellable
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import logcat.LogPriority
import logcat.asLog
import logcat.logcat

private const val MaxParallelCoroutines = 6
private const val BatchSize = 50

/**
 * Regenerates thumbnails for all files in a bookshelf while reporting progress.
 */
abstract class RegenerateThumbnailsUseCase :
    OneShotUseCase<RegenerateThumbnailsUseCase.Request, Unit, RegenerateThumbnailsUseCase.Error>() {

    /**
     * Request parameters for [RegenerateThumbnailsUseCase].
     *
     * @property bookshelfId The ID of the bookshelf whose thumbnails should be regenerated.
     * @property process Callback invoked as each thumbnail is processed with current progress and
     * total count.
     */
    class Request(
        val bookshelfId: BookshelfId,
        val process: suspend (Bookshelf, progress: Long, max: Long) -> Unit,
    )

    /**
     * Errors that can occur during thumbnail regeneration.
     */
    enum class Error : Resource.AppError {
        /**
         * Indicates that a system error occurred.
         */
        System,
    }
}

@Inject
@ContributesBinding(AppScope::class)
internal class RegenerateThumbnailsUseCaseImpl(
    private val bookshelfRepository: BookshelfRepository,
    private val fileRepository: FileRepository,
    private val thumbnailRepository: ThumbnailRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
) : RegenerateThumbnailsUseCase() {

    override suspend fun run(request: Request): Resource<Unit, Error> = runCatchingCancellable {
        val bookshelf = bookshelfRepository.flow(request.bookshelfId).first()
            ?: return@runCatchingCancellable
        val totalCount = fileRepository.count(request.bookshelfId)
        if (totalCount > 0) {
            regenerateThumbnails(bookshelf, totalCount, request.process)
        }
    }.fold(
        onSuccess = { Resource.Success(Unit) },
        onFailure = {
            logcat(LogPriority.ERROR) { it.asLog() }
            Resource.Error(Error.System)
        },
    )

    private suspend fun regenerateThumbnails(
        bookshelf: Bookshelf,
        totalCount: Long,
        onProgress: suspend (Bookshelf, progress: Long, max: Long) -> Unit,
    ) {
        val mutex = Mutex()
        var completedCount = 0L
        var offset = 0L

        limitedCoroutineScope(MaxParallelCoroutines, context = dispatcher) {
            while (offset < totalCount) {
                val batch = fileRepository.fileList(
                    bookshelf.id,
                    limit = BatchSize,
                    offset = offset,
                )
                if (batch.isEmpty()) break
                offset += batch.size

                batch.mapParallel { file ->
                    loadThumbnail(file)
                    mutex.withLock {
                        onProgress(bookshelf, ++completedCount, totalCount)
                    }
                }
            }
        }
    }

    private suspend fun loadThumbnail(file: File) {
        runCatchingCancellable {
            thumbnailRepository.load(FileThumbnail.from(file)).onError {
                if (it.throwable is CancellationException) throw it.throwable
                logcat(LogPriority.WARN) { it.throwable.asLog() }
            }
        }.onFailure {
            logcat(LogPriority.WARN) { it.asLog() }
        }
    }
}
