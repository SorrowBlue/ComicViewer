package com.sorrowblue.comicviewer.domain.usecase.settings.interactor

import com.sorrowblue.comicviewer.domain.entity.settings.ViewerOperationSettings
import com.sorrowblue.comicviewer.domain.repository.SettingsCommonRepository
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageViewerOperationSettingsUseCase
import javax.inject.Inject

internal class ManageViewerOperationSettingsInteractor @Inject constructor(
    private val repository: SettingsCommonRepository,
) : ManageViewerOperationSettingsUseCase() {

    override val settings = repository.viewerOperationSettings

    override suspend fun edit(action: (ViewerOperationSettings) -> ViewerOperationSettings) {
        repository.updateViewerOperationSettings(action)
    }
}
