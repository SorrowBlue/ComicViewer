package com.sorrowblue.comicviewer.domain.service.interactor.settings

import com.sorrowblue.comicviewer.domain.model.settings.BookSettings
import com.sorrowblue.comicviewer.domain.model.settings.ViewerSettings
import com.sorrowblue.comicviewer.domain.service.repository.SettingsCommonRepository
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageBookSettingsUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageViewerSettingsUseCase
import javax.inject.Inject

internal class ManageViewerSettingsInteractor @Inject constructor(
    private val settingsCommonRepository: SettingsCommonRepository,
) : ManageViewerSettingsUseCase {

    override val settings = settingsCommonRepository.viewerSettings

    override suspend fun edit(action: (ViewerSettings) -> ViewerSettings) {
        settingsCommonRepository.updateViewerSettings(action)
    }
}

internal class ManageBookSettingsInteractor @Inject constructor(
    private val settingsCommonRepository: SettingsCommonRepository,
) : ManageBookSettingsUseCase {

    override val settings = settingsCommonRepository.bookSettings
    override suspend fun edit(action: (BookSettings) -> BookSettings) {
        settingsCommonRepository.updateBookSettings(action)
    }
}
