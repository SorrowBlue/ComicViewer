package com.sorrowblue.comicviewer.domain.usecase.interactor

import com.sorrowblue.comicviewer.framework.Result
import com.sorrowblue.comicviewer.domain.repository.SettingsCommonRepository
import com.sorrowblue.comicviewer.domain.usecase.UpdateHistoryUseCase
import javax.inject.Inject

internal class UpdateHistoryInteractor @Inject constructor(
    private val commonSettingsCommonRepository: SettingsCommonRepository,
) : UpdateHistoryUseCase() {

    override suspend fun run(request: Request): Result<Unit, Unit> {
        commonSettingsCommonRepository.updateHistory {
            it.copy(request.history.serverId, request.history.path, request.history.position)
        }
        return Result.Success(Unit)
    }
}
