package com.sorrowblue.comicviewer.domain.service.interactor.file

import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.service.BookFileReaderManager
import com.sorrowblue.comicviewer.domain.usecase.file.CloseBookUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

@ContributesBinding(AppScope::class)
@Inject
internal class CloseBookInteractor(private val bookFileReaderManager: BookFileReaderManager) :
    CloseBookUseCase() {
    override suspend fun run(request: Request): Resource<Unit, Unit> {
        bookFileReaderManager.close(request.book)
        return Resource.Success(Unit)
    }
}
