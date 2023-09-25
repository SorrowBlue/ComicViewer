package com.sorrowblue.comicviewer.domain.service.interactor

import com.sorrowblue.comicviewer.domain.service.repository.SettingsCommonRepository
import com.sorrowblue.comicviewer.domain.usecase.UpdateHistoryUseCase
import com.sorrowblue.comicviewer.framework.Result
import javax.inject.Inject

internal class UpdateHistoryInteractor @Inject constructor(
    private val settingsCommonRepository: SettingsCommonRepository,
) : UpdateHistoryUseCase() {

    override suspend fun run(request: Request): Result<Unit, Unit> {
        settingsCommonRepository.updateHistory {
            it.copy(request.history.bookshelfId, request.history.path, request.history.position)
        }
        return Result.Success(Unit)
    }
}
