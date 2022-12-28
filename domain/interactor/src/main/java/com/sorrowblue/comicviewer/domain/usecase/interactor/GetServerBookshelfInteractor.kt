package com.sorrowblue.comicviewer.domain.usecase.interactor

import com.sorrowblue.comicviewer.domain.entity.Bookshelf
import com.sorrowblue.comicviewer.domain.entity.ServerBookshelf
import com.sorrowblue.comicviewer.domain.model.Result
import com.sorrowblue.comicviewer.domain.model.Unknown
import com.sorrowblue.comicviewer.domain.repository.FileRepository
import com.sorrowblue.comicviewer.domain.repository.ServerRepository
import com.sorrowblue.comicviewer.domain.usecase.GetLibraryFileResult
import com.sorrowblue.comicviewer.domain.usecase.GetServerBookshelfRequest
import com.sorrowblue.comicviewer.domain.usecase.GetServerBookshelfUseCase
import javax.inject.Inject

internal class GetServerBookshelfInteractor @Inject constructor(
    private val serverRepository: ServerRepository,
    private val fileRepository: FileRepository,
) : GetServerBookshelfUseCase() {

    override suspend fun run(request: GetServerBookshelfRequest): Result<ServerBookshelf, GetLibraryFileResult> {
        return serverRepository.get(request.serverId).fold({ server ->
            if (server != null) {
                fileRepository.get(server.id, request.path).fold({ file ->
                    if (file as? Bookshelf != null) {
                        Result.Success(ServerBookshelf(server to file))
                    } else {
                        Result.Error(GetLibraryFileResult.NO_FILE)
                    }
                }, {
                    Result.Exception(Unknown)
                })
            } else {
                Result.Error(GetLibraryFileResult.NO_LIBRARY)
            }
        }, {
            Result.Exception(Unknown)
        }, {
            Result.Exception(Unknown)
        })
    }
}
