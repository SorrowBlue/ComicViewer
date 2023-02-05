package com.sorrowblue.comicviewer.domain.usecase.interactor

import com.sorrowblue.comicviewer.domain.entity.ServerBook
import com.sorrowblue.comicviewer.domain.entity.file.Book
import com.sorrowblue.comicviewer.domain.repository.FileRepository
import com.sorrowblue.comicviewer.domain.repository.ServerRepository
import com.sorrowblue.comicviewer.domain.usecase.GetBookUseCase
import com.sorrowblue.comicviewer.domain.usecase.GetLibraryFileResult
import com.sorrowblue.comicviewer.domain.usecase.GetServerBookUseCase
import com.sorrowblue.comicviewer.framework.Result
import com.sorrowblue.comicviewer.framework.Unknown
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class GetServerBookInteractor @Inject constructor(
    private val serverRepository: ServerRepository,
    private val fileRepository: FileRepository,
) : GetServerBookUseCase() {

    override fun run(request: Request): Flow<Result<ServerBook, GetLibraryFileResult>> {
        return serverRepository.get(request.serverId).map { result ->
            result.fold({ server ->
                fileRepository.getBook(server.id, request.path).fold({ file ->
                    if (file != null) {
                        Result.Success(ServerBook(server to file))
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

internal class GetBookInteractor @Inject constructor(private val fileRepository: FileRepository) :
    GetBookUseCase() {

    override fun run(request: Request): Flow<Result<Book, Unit>> {
        return fileRepository.getFile(request.serverId, request.path).map { result ->
            result.fold({
                if (it is Book) {
                    Result.Success(it)
                } else {
                    Result.Error(Unit)
                }
            }, {
                Result.Error(Unit)
            }, {
                Result.Exception(it)
            })
        }
    }
}
