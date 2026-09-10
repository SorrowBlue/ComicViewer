/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.service.interactor.bookshelf

import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.model.file.FileThumbnail
import com.sorrowblue.comicviewer.domain.repository.BookshelfRepository
import com.sorrowblue.comicviewer.domain.repository.FileRepository
import com.sorrowblue.comicviewer.domain.repository.ThumbnailRepository
import com.sorrowblue.comicviewer.domain.service.limitedCoroutineScope
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.RegenerateThumbnailsUseCase
import com.sorrowblue.comicviewer.framework.common.IoDispatcher
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

@ContributesBinding(AppScope::class)
internal class RegenerateThumbnailsInteractor(
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

private const val MaxParallelCoroutines = 6
