package com.sorrowblue.comicviewer.domain.usecase.interactor

import com.sorrowblue.comicviewer.domain.entity.ServerBook
import com.sorrowblue.comicviewer.framework.Result
import com.sorrowblue.comicviewer.framework.Unknown
import com.sorrowblue.comicviewer.domain.repository.FileRepository
import com.sorrowblue.comicviewer.domain.repository.ServerRepository
import com.sorrowblue.comicviewer.domain.usecase.GetLibraryFileResult
import com.sorrowblue.comicviewer.domain.usecase.GetServerBookUseCase
import javax.inject.Inject

internal class GetServerBookInteractor @Inject constructor(
    private val serverRepository: ServerRepository,
    private val fileRepository: FileRepository,
) : GetServerBookUseCase() {

    override suspend fun run(request: Request): Result<ServerBook, GetLibraryFileResult> {
        return serverRepository.get(request.serverId).fold({ server ->
            if (server != null) {
                fileRepository.getBook(server.id, request.path).fold({ file ->
                    if (file != null) {
                        Result.Success(ServerBook(server to file))
                    } else {
                        Result.Error(GetLibraryFileResult.NO_FILE)
                    }
                }, {
                    Result.Exception(Unknown(it))
                })
            } else {
                Result.Error(GetLibraryFileResult.NO_LIBRARY)
            }
        }, {
            Result.Error(GetLibraryFileResult.NO_LIBRARY)
        }, {
            Result.Exception(it)
        })
    }
}
