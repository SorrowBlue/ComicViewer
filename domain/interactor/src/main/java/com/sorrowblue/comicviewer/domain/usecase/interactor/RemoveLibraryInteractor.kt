package com.sorrowblue.comicviewer.domain.usecase.interactor

import com.sorrowblue.comicviewer.domain.model.Response
import com.sorrowblue.comicviewer.domain.repository.ServerRepository
import com.sorrowblue.comicviewer.domain.usecase.RemoveLibraryRequest
import com.sorrowblue.comicviewer.domain.usecase.RemoveLibraryUseCase
import javax.inject.Inject

internal class RemoveLibraryInteractor @Inject constructor(
    private val serverRepository: ServerRepository
) : RemoveLibraryUseCase() {

    override suspend fun run(request: RemoveLibraryRequest): Response<Boolean> {
        // TODO(キャッシュファイルの削除)
        return serverRepository.delete(request.server)
    }
}
