package com.sorrowblue.comicviewer.domain.usecase.interactor

import com.sorrowblue.comicviewer.domain.model.ViewerSettings
import com.sorrowblue.comicviewer.domain.repository.SettingsCommonRepository
import com.sorrowblue.comicviewer.domain.usecase.settings.ViewerSettingsUseCase
import javax.inject.Inject

internal class ViewerSettingsInteractor @Inject constructor(
    private val repository: SettingsCommonRepository,
) : ViewerSettingsUseCase() {

    override val settings = repository.viewerSettings

    override suspend fun edit(action: (ViewerSettings) -> ViewerSettings) {
        repository.updateViewerSettings(action)
    }
}
