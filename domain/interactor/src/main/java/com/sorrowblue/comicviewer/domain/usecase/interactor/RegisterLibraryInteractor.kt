package com.sorrowblue.comicviewer.domain.usecase.interactor

import com.sorrowblue.comicviewer.domain.model.NoConnection
import com.sorrowblue.comicviewer.domain.model.Result
import com.sorrowblue.comicviewer.domain.entity.RegisterServerRequest
import com.sorrowblue.comicviewer.domain.entity.Server
import com.sorrowblue.comicviewer.domain.repository.FileRepository
import com.sorrowblue.comicviewer.domain.repository.LibraryStatus
import com.sorrowblue.comicviewer.domain.repository.ServerRepository
import com.sorrowblue.comicviewer.domain.usecase.RegisterLibraryError
import com.sorrowblue.comicviewer.domain.usecase.RegisterLibraryUseCase
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class RegisterLibraryInteractor @Inject constructor(
    private val repository: ServerRepository,
) : RegisterLibraryUseCase() {

    override suspend fun run(request: RegisterServerRequest): Result<Server, RegisterLibraryError> {
        return withContext(Dispatchers.IO) {
            when (val res = repository.exists(request.server, request.path)) {
                is Result.Success -> if (res.data) {
                    repository.registerOrUpdate(request.server, request.path).fold({
                        Result.Success(it)
                    }, {
                        Result.Error(RegisterLibraryError.UNKNOWN)
                    }, {
                        Result.Error(RegisterLibraryError.UNKNOWN)
                    })
                } else {
                    Result.Error(RegisterLibraryError.NO_EXISTS)
                }
                is Result.Error -> {
                    when (res.error) {
                        LibraryStatus.NOT_AUTH -> Result.Error(RegisterLibraryError.LOGON_FAILURE)
                        LibraryStatus.UNKNOWN -> Result.Error(RegisterLibraryError.UNKNOWN)
                        LibraryStatus.NO_NETWORK -> Result.Error(RegisterLibraryError.UNKNOWN)
                        LibraryStatus.FAILED_CONNECT -> Result.Error(RegisterLibraryError.UNKNOWN)
                    }
                }
                is Result.Exception -> {
                    Result.Exception(NoConnection)
                }
            }
        }
    }
}
