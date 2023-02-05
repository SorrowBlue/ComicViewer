package com.sorrowblue.comicviewer.domain.usecase.interactor

import com.sorrowblue.comicviewer.domain.entity.server.Server
import com.sorrowblue.comicviewer.domain.repository.FileRepository
import com.sorrowblue.comicviewer.domain.repository.FileRepositoryError
import com.sorrowblue.comicviewer.domain.repository.ServerRepository
import com.sorrowblue.comicviewer.domain.repository.ServerRepositoryError
import com.sorrowblue.comicviewer.domain.usecase.RegisterServerError
import com.sorrowblue.comicviewer.domain.usecase.RegisterServerUseCase
import com.sorrowblue.comicviewer.framework.Result
import javax.inject.Inject

/**
 * サーバに接続して、接続できたら登録/更新する
 *
 * @property serverRepository
 */
internal class RegisterServerInteractor @Inject constructor(
    private val serverRepository: ServerRepository,
    private val fileRepository: FileRepository
) : RegisterServerUseCase() {

    override suspend fun run(request: Request): Result<Server, RegisterServerError> {
        return serverRepository.connect(request.server, request.path).fold({
            fileRepository.getFolder(request.server, request.path).fold({
                serverRepository.register(request.server, it).fold({
                    Result.Success(it)
                }, {
                    when (it) {
                        ServerRepositoryError.AuthenticationFailure -> Result.Error(
                            RegisterServerError.InvalidAuth
                        )
                        ServerRepositoryError.PathDoesNotExist -> Result.Error(RegisterServerError.InvalidPath)
                        ServerRepositoryError.IncorrectServerInfo -> Result.Error(
                            RegisterServerError.InvalidServerInfo
                        )
                    }
                }, {
                    Result.Exception(it)
                })
            }, {
                when (it) {
                    FileRepositoryError.AuthenticationFailure -> Result.Error(RegisterServerError.InvalidAuth)
                    FileRepositoryError.PathDoesNotExist -> Result.Error(RegisterServerError.InvalidPath)
                    FileRepositoryError.IncorrectServerInfo -> Result.Error(RegisterServerError.InvalidServerInfo)
                }
            }, {
                Result.Exception(it)
            })
        }, {
            when (it) {
                ServerRepositoryError.AuthenticationFailure -> Result.Error(RegisterServerError.InvalidAuth)
                ServerRepositoryError.PathDoesNotExist -> Result.Error(RegisterServerError.InvalidPath)
                ServerRepositoryError.IncorrectServerInfo -> Result.Error(RegisterServerError.InvalidServerInfo)
            }
        }, { Result.Exception(it) })
    }
}
