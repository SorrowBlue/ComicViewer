package com.sorrowblue.comicviewer.domain.usecase.interactor

import com.sorrowblue.comicviewer.domain.entity.Bookshelf
import com.sorrowblue.comicviewer.domain.entity.ServerBookshelf
import com.sorrowblue.comicviewer.domain.model.Result
import com.sorrowblue.comicviewer.domain.repository.FileRepository
import com.sorrowblue.comicviewer.domain.repository.ServerRepository
import com.sorrowblue.comicviewer.domain.usecase.GetLibraryInfoError
import com.sorrowblue.comicviewer.domain.usecase.GetLibraryInfoRequest
import com.sorrowblue.comicviewer.domain.usecase.ServerBookshelfUseCase
import javax.inject.Inject

internal class ServerBookshelfInteractor @Inject constructor(
    private val serverRepository: ServerRepository,
    private val bookshelfRepository: FileRepository,
) : ServerBookshelfUseCase() {

    override suspend fun run(request: GetLibraryInfoRequest): Result<ServerBookshelf, GetLibraryInfoError> {
        return serverRepository.get(request.serverId).fold({ library ->
            if (library != null) {
                bookshelfRepository.getRoot(library.id).fold({
                    if (it is Bookshelf) {
                        Result.Success(ServerBookshelf(library to it))
                    } else {
                        Result.Error(GetLibraryInfoError.NOT_FOUND)
                    }
                }, {
                    Result.Error(GetLibraryInfoError.NOT_FOUND)
                }, {
                    Result.Error(GetLibraryInfoError.SYSTEM_ERROR)
                })
            } else {
                Result.Error(GetLibraryInfoError.NOT_FOUND)
            }
        }, {
            Result.Error(GetLibraryInfoError.NOT_FOUND)
        }, {
            Result.Error(GetLibraryInfoError.SYSTEM_ERROR)
        })
    }
}
