package com.sorrowblue.comicviewer.domain.usecase.interactor

import com.sorrowblue.comicviewer.domain.entity.ServerBookshelf
import com.sorrowblue.comicviewer.domain.entity.file.Bookshelf
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
    private val bookshelfRepository: FileRepository,
) : GetServerInfoUseCase() {

    override fun run(request: Request): Flow<Result<ServerBookshelf, GetLibraryInfoError>> {
        return serverRepository.get(request.serverId).map { result ->
            result.fold({ server ->
                bookshelfRepository.getRoot(server.id).fold({
                    if (it is Bookshelf) {
                        Result.Success(ServerBookshelf(server to it))
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
