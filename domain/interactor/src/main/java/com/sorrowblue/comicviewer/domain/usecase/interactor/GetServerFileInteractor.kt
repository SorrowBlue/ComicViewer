package com.sorrowblue.comicviewer.domain.usecase.interactor

import com.sorrowblue.comicviewer.domain.entity.ServerFile
import com.sorrowblue.comicviewer.domain.model.Result
import com.sorrowblue.comicviewer.domain.model.Unknown
import com.sorrowblue.comicviewer.domain.repository.FileRepository
import com.sorrowblue.comicviewer.domain.repository.ServerRepository
import com.sorrowblue.comicviewer.domain.usecase.GetLibraryFileResult
import com.sorrowblue.comicviewer.domain.usecase.GetServerFileRequest
import com.sorrowblue.comicviewer.domain.usecase.GetServerFileUseCase
import javax.inject.Inject

internal class GetServerFileInteractor @Inject constructor(
    private val serverRepository: ServerRepository,
    private val fileRepository: FileRepository,
) : GetServerFileUseCase() {

    override suspend fun run(request: GetServerFileRequest): Result<ServerFile, GetLibraryFileResult> {
        return serverRepository.get(request.serverId).fold({ server ->
            if (server != null) {
                fileRepository.get(server.id, request.path).fold({ file ->
                    if (file != null) {
                        Result.Success(ServerFile(server to file))
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
