package com.sorrowblue.comicviewer.domain.usecase.interactor

import com.sorrowblue.comicviewer.domain.model.ViewerOperationSettings
import com.sorrowblue.comicviewer.domain.repository.SettingsCommonRepository
import com.sorrowblue.comicviewer.domain.usecase.settings.ViewerOperationSettingsUseCase
import javax.inject.Inject

internal class ViewerOperationSettingsInteractor @Inject constructor(
    private val repository: SettingsCommonRepository,
) : ViewerOperationSettingsUseCase() {

    override val settings = repository.viewerOperationSettings

    override suspend fun edit(action: (ViewerOperationSettings) -> ViewerOperationSettings) {
        repository.updateViewerOperationSettings(action)
    }
}
