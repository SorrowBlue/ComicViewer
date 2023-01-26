package com.sorrowblue.comicviewer.domain.usecase.interactor

import com.sorrowblue.comicviewer.domain.entity.ServerBookshelf
import com.sorrowblue.comicviewer.domain.entity.file.Bookshelf
import com.sorrowblue.comicviewer.framework.Result
import com.sorrowblue.comicviewer.domain.repository.FileRepository
import com.sorrowblue.comicviewer.domain.repository.ServerRepository
import com.sorrowblue.comicviewer.domain.usecase.GetLibraryInfoError
import com.sorrowblue.comicviewer.domain.usecase.GetServerBookshelfUseCase
import javax.inject.Inject

internal class GetServerBookshelfInteractor @Inject constructor(
    private val serverRepository: ServerRepository,
    private val fileRepository: FileRepository
) : GetServerBookshelfUseCase() {

    override suspend fun run(request: Request): Result<ServerBookshelf, GetLibraryInfoError> {
        return serverRepository.get(request.serverId).fold({ server ->
            if (server != null) {
                fileRepository.get2(server.id, request.path).fold({
                    Result.Success(ServerBookshelf(server to it as Bookshelf))
                }, {
                    Result.Error(GetLibraryInfoError.NOT_FOUND)
                }, {
                    Result.Error(GetLibraryInfoError.NOT_FOUND)
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
