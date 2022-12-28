package com.sorrowblue.comicviewer.domain.usecase.interactor

import com.sorrowblue.comicviewer.domain.model.settings.BookshelfDisplaySettings
import com.sorrowblue.comicviewer.domain.repository.SettingsCommonRepository
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageBookshelfDisplaySettingsUseCase
import javax.inject.Inject

internal class ManageBookshelfDisplaySettingsInteractor @Inject constructor(
    private val bookShelfSettingsRepository: SettingsCommonRepository,
) : ManageBookshelfDisplaySettingsUseCase() {

    override val settings = bookShelfSettingsRepository.bookshelfDisplaySettings

    override suspend fun edit(action: (BookshelfDisplaySettings) -> BookshelfDisplaySettings) {
        bookShelfSettingsRepository.updateBookshelfSettings(action::invoke)
    }
}
