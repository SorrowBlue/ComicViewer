package com.sorrowblue.comicviewer.domain.usecase.interactor

import com.sorrowblue.comicviewer.domain.entity.ReadLater
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.domain.repository.FileRepository
import com.sorrowblue.comicviewer.domain.repository.ReadLaterRepository
import com.sorrowblue.comicviewer.domain.usecase.AddReadLaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.GetFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.GetLibraryInfoError
import com.sorrowblue.comicviewer.framework.Result
import javax.inject.Inject

internal class GetFileInteractor @Inject constructor(
    private val bookshelfRepository: FileRepository,
) : GetFileUseCase() {

    override suspend fun run(request: Request): Result<File, GetLibraryInfoError> {
        return bookshelfRepository.get(request.serverId, request.path).fold({
            Result.Success(it!!)
        }, {
            Result.Error(GetLibraryInfoError.NOT_FOUND)
        })
    }
}

internal class AddReadLaterIteractor @Inject constructor(
    private val readLaterRepository: ReadLaterRepository
) : AddReadLaterUseCase() {

    override suspend fun run(request: Request): Result<ReadLater, Unit> {
        return readLaterRepository.add(ReadLater(request.serverId, request.path)).fold({
            Result.Success(it)
        }, {
            Result.Error(it)
        }, { Result.Exception(it) })
    }
}
