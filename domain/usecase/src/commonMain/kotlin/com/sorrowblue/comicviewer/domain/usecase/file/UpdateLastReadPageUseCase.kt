/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.file

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.repository.FileRepository
import com.sorrowblue.comicviewer.domain.usecase.OneShotUseCase
import dev.zacsweers.metro.Inject
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Inject
class UpdateLastReadPageUseCase(private val fileRepository: FileRepository) :
    OneShotUseCase<UpdateLastReadPageUseCase.Request, Unit, Unit>() {

    @OptIn(ExperimentalTime::class)
    data class Request(
        val bookshelfId: BookshelfId,
        val path: String,
        val lastReadPage: Int,
        val timestamp: Long = Clock.System.now().toEpochMilliseconds(),
    ) : OneShotUseCase.Request

    override suspend fun run(request: Request): Resource<Unit, Unit> {
        fileRepository.updateHistory(
            request.path,
            request.bookshelfId,
            request.lastReadPage,
            request.timestamp,
        )
        return Resource.Success(Unit)
    }
}
