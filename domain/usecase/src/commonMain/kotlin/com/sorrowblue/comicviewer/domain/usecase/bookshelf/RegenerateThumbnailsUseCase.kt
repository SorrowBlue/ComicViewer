/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.bookshelf

import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.model.file.FileThumbnail
import com.sorrowblue.comicviewer.domain.repository.BookshelfRepository
import com.sorrowblue.comicviewer.domain.repository.FileRepository
import com.sorrowblue.comicviewer.domain.repository.ThumbnailRepository
import com.sorrowblue.comicviewer.domain.usecase.OneShotUseCase
import com.sorrowblue.comicviewer.domain.usecase.limitedCoroutineScope
import com.sorrowblue.comicviewer.framework.common.IoDispatcher
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

private const val MaxParallelCoroutines = 6

class RegenerateThumbnailsUseCase(private val action: suspend (Request) -> Resource<Unit, Error>) :
    OneShotUseCase<RegenerateThumbnailsUseCase.Request, Unit, RegenerateThumbnailsUseCase.Error>() {

    @Inject
    constructor(
        bookshelfRepository: BookshelfRepository,
        fileRepository: FileRepository,
        thumbnailRepository: ThumbnailRepository,
        @IoDispatcher dispatcher: CoroutineDispatcher,
    ) : this({ request ->
        val bookshelf = bookshelfRepository.flow(request.bookshelfId).first()
        if (bookshelf != null) {
            val mutex = Mutex()
            val limit = 1
            var offset = 0L
            val count = fileRepository.count(request.bookshelfId)
            limitedCoroutineScope(MaxParallelCoroutines, context = dispatcher) {
                List(count.toInt()) {
                    async {
                        val list = mutex.withLock {
                            fileRepository
                                .fileList(
                                    request.bookshelfId,
                                    limit = limit,
                                    offset = offset,
                                ).also {
                                    offset += it.size
                                }
                        }
                        if (list.isNotEmpty()) {
                            thumbnailRepository
                                .load(FileThumbnail.from(list.first()))
                                .await()
                            request.process(bookshelf, offset, count)
                        }
                    }
                }.awaitAll()
            }
        }
        Resource.Success(Unit)
    })

    data class Request(
        val bookshelfId: BookshelfId,
        val process: suspend (Bookshelf, progress: Long, max: Long) -> Unit,
    ) : OneShotUseCase.Request

    enum class Error : Resource.AppError {
        System,
    }

    override suspend fun run(request: Request): Resource<Unit, Error> = action(request)
}
