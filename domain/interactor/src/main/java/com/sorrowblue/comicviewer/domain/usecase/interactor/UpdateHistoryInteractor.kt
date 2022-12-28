package com.sorrowblue.comicviewer.domain.usecase.interactor

import com.sorrowblue.comicviewer.domain.model.Response
import com.sorrowblue.comicviewer.domain.model.UpdateHistoryRequest
import com.sorrowblue.comicviewer.domain.repository.SettingsCommonRepository
import com.sorrowblue.comicviewer.domain.usecase.UpdateHistoryUseCase
import javax.inject.Inject

internal class UpdateHistoryInteractor @Inject constructor(
    private val commonSettingsCommonRepository: SettingsCommonRepository,
) : UpdateHistoryUseCase() {
    override suspend fun run(request: UpdateHistoryRequest): Response<Unit> {
        commonSettingsCommonRepository.updateHistory {
            it.copy(request.history.serverId, request.history.path, request.history.position)
        }
        return Response.Success(Unit)
    }
}
