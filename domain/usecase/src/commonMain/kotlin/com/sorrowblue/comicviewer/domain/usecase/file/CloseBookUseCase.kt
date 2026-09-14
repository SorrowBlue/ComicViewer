/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.file

import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.repository.file.BookFileReaderManager
import com.sorrowblue.comicviewer.domain.usecase.OneShotUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

abstract class CloseBookUseCase : OneShotUseCase<Book, Unit, Unit>()

@Inject
@ContributesBinding(AppScope::class)
internal class CloseBookUseCaseImpl(private val bookFileReaderManager: BookFileReaderManager) :
    CloseBookUseCase() {

    override suspend fun run(request: Book): Resource<Unit, Unit> {
        bookFileReaderManager.close(request)
        return Resource.Success(Unit)
    }
}
