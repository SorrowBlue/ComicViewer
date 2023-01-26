package com.sorrowblue.comicviewer.domain.usecase.interactor

import com.sorrowblue.comicviewer.framework.Result
import com.sorrowblue.comicviewer.domain.repository.ServerRepository
import com.sorrowblue.comicviewer.domain.usecase.RemoveLibraryUseCase
import javax.inject.Inject

internal class RemoveLibraryInteractor @Inject constructor(
    private val serverRepository: ServerRepository
) : RemoveLibraryUseCase() {

    override suspend fun run(request: Request): Result<Boolean, Unit> {
        // TODO(キャッシュファイルの削除)
        return serverRepository.delete(request.server).fold({
            Result.Success(it)
        }, {
            Result.Error(Unit)
        })
    }
}
