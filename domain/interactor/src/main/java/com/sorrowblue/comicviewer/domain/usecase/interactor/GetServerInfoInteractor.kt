package com.sorrowblue.comicviewer.domain.usecase.interactor

import com.sorrowblue.comicviewer.domain.entity.ServerFolder
import com.sorrowblue.comicviewer.domain.entity.file.Folder
import com.sorrowblue.comicviewer.domain.repository.FileRepository
import com.sorrowblue.comicviewer.domain.repository.ServerRepository
import com.sorrowblue.comicviewer.domain.usecase.GetLibraryInfoError
import com.sorrowblue.comicviewer.domain.usecase.GetServerInfoUseCase
import com.sorrowblue.comicviewer.framework.Result
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class GetServerInfoInteractor @Inject constructor(
    private val serverRepository: ServerRepository,
    private val fileRepository: FileRepository,
) : GetServerInfoUseCase() {

    override fun run(request: Request): Flow<Result<ServerFolder, GetLibraryInfoError>> {
        return serverRepository.get(request.serverId).map { result ->
            result.fold({ server ->
                fileRepository.getRoot(server.id).fold({
                    if (it is Folder) {
                        Result.Success(ServerFolder(server to it))
                    } else {
                        Result.Error(GetLibraryInfoError.NOT_FOUND)
                    }
                }, {
                    Result.Error(GetLibraryInfoError.NOT_FOUND)
                }, {
                    Result.Exception(it)
                })
            }, {
                Result.Error(GetLibraryInfoError.NOT_FOUND)
            }, {
                Result.Exception(it)
            })
        }
    }
}
