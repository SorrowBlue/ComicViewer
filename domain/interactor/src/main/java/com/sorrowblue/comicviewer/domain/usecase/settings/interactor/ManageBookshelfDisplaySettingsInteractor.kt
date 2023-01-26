package com.sorrowblue.comicviewer.domain.usecase.settings.interactor

import com.sorrowblue.comicviewer.domain.entity.settings.BookshelfDisplaySettings
import com.sorrowblue.comicviewer.domain.entity.settings.SecuritySettings
import com.sorrowblue.comicviewer.domain.repository.SettingsCommonRepository
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageBookshelfDisplaySettingsUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageSecuritySettingsUseCase
import javax.inject.Inject

internal class ManageBookshelfDisplaySettingsInteractor @Inject constructor(
    private val bookShelfSettingsRepository: SettingsCommonRepository,
) : ManageBookshelfDisplaySettingsUseCase() {

    override val settings = bookShelfSettingsRepository.bookshelfDisplaySettings

    override suspend fun edit(action: (BookshelfDisplaySettings) -> BookshelfDisplaySettings) {
        bookShelfSettingsRepository.updateBookshelfSettings(action::invoke)
    }
}

internal class ManageSecuritySettingsInteractor @Inject constructor(
    private val bookShelfSettingsRepository: SettingsCommonRepository,
) : ManageSecuritySettingsUseCase() {

    override val settings = bookShelfSettingsRepository.securitySettings

    override suspend fun edit(action: (SecuritySettings) -> SecuritySettings) {
        bookShelfSettingsRepository.updateSecuritySettings(action::invoke)
    }
}
