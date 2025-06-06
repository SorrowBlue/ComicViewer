package com.sorrowblue.comicviewer.domain.service.interactor.bookshelf

import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.service.datasource.BookshelfLocalDataSource
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.UpdateDeletionFlagUseCase
import org.koin.core.annotation.Factory

@Factory
internal class UpdateDeletionFlagInteractor(
    private val bookshelfLocalDataSource: BookshelfLocalDataSource,
) : UpdateDeletionFlagUseCase() {
    override suspend fun run(request: Request): Resource<Unit, Unit> {
        bookshelfLocalDataSource.updateDeleted(request.bookshelfId, request.deleted)
        return Resource.Success(Unit)
    }
}
