package com.sorrowblue.comicviewer.domain.usecase.settings.interactor

import com.sorrowblue.comicviewer.domain.entity.settings.ViewerSettings
import com.sorrowblue.comicviewer.domain.repository.SettingsCommonRepository
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageViewerSettingsUseCase
import javax.inject.Inject

internal class ManageViewerSettingsInteractor @Inject constructor(
    private val settingsCommonRepository: SettingsCommonRepository,
) : ManageViewerSettingsUseCase() {

    override val settings = settingsCommonRepository.viewerSettings

    override suspend fun edit(action: (ViewerSettings) -> ViewerSettings) {
        settingsCommonRepository.updateViewerSettings(action)
    }
}
