package com.sorrowblue.comicviewer.domain.usecase.interactor

import com.sorrowblue.comicviewer.domain.entity.ServerFolder
import com.sorrowblue.comicviewer.domain.entity.file.Folder
import com.sorrowblue.comicviewer.domain.repository.FileRepository
import com.sorrowblue.comicviewer.domain.repository.ServerRepository
import com.sorrowblue.comicviewer.domain.usecase.GetLibraryInfoError
import com.sorrowblue.comicviewer.domain.usecase.GetServerFolderUseCase
import com.sorrowblue.comicviewer.framework.Result
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class GetServerFolderInteractor @Inject constructor(
    private val serverRepository: ServerRepository,
    private val fileRepository: FileRepository
) : GetServerFolderUseCase() {

    override fun run(request: Request): Flow<Result<ServerFolder, GetLibraryInfoError>> {
        return serverRepository.get(request.serverId).map { result ->
            result.fold({ server ->
                fileRepository.get2(server.id, request.path).fold({
                    Result.Success(ServerFolder(server to it as Folder))
                }, {
                    Result.Error(GetLibraryInfoError.NOT_FOUND)
                }, {
                    Result.Error(GetLibraryInfoError.NOT_FOUND)
                })
            }, {
                Result.Error(GetLibraryInfoError.NOT_FOUND)
            }, {
                Result.Error(GetLibraryInfoError.SYSTEM_ERROR)
            })

        }
    }
}
