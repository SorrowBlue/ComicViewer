/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.bookshelf

import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.common.IoDispatcher
import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.model.file.FileThumbnail
import com.sorrowblue.comicviewer.domain.repository.BookshelfRepository
import com.sorrowblue.comicviewer.domain.repository.FileRepository
import com.sorrowblue.comicviewer.domain.repository.ThumbnailRepository
import com.sorrowblue.comicviewer.domain.usecase.OneShotUseCase
import com.sorrowblue.comicviewer.domain.usecase.limitedCoroutineScope
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

private const val MaxParallelCoroutines = 6

abstract class RegenerateThumbnailsUseCase :
    OneShotUseCase<RegenerateThumbnailsUseCase.Request, Unit, RegenerateThumbnailsUseCase.Error>() {

    data class Request(
        val bookshelfId: BookshelfId,
        val process: suspend (Bookshelf, progress: Long, max: Long) -> Unit,
    )

    enum class Error : Resource.AppError {
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

    override suspend fun run(request: Request): Resource<Unit, Error> {
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
        return Resource.Success(Unit)
    }
}
