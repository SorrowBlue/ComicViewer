package com.sorrowblue.comicviewer.domain.usecase.interactor

import com.sorrowblue.comicviewer.domain.entity.ServerFile
import com.sorrowblue.comicviewer.domain.repository.FileRepository
import com.sorrowblue.comicviewer.domain.repository.ServerRepository
import com.sorrowblue.comicviewer.domain.usecase.GetLibraryFileResult
import com.sorrowblue.comicviewer.domain.usecase.GetServerFileUseCase
import com.sorrowblue.comicviewer.framework.Result
import com.sorrowblue.comicviewer.framework.Unknown
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class GetServerFileInteractor @Inject constructor(
    private val serverRepository: ServerRepository,
    private val fileRepository: FileRepository,
) : GetServerFileUseCase() {

    override fun run(request: Request): Flow<Result<ServerFile, GetLibraryFileResult>> {
        return serverRepository.get(request.serverId).map { result ->
            result.fold({ server ->
                fileRepository.get(server.id, request.path).fold({ file ->
                    if (file != null) {
                        Result.Success(ServerFile(server to file))
                    } else {
                        Result.Error(GetLibraryFileResult.NO_FILE)
                    }
                }, {
                    Result.Exception(Unknown(it))
                })
            }, {
                Result.Error(GetLibraryFileResult.NO_LIBRARY)
            }, {
                Result.Exception(it)
            })
        }
    }
}
