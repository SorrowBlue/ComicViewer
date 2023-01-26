package com.sorrowblue.comicviewer.domain.usecase.settings.interactor

import com.sorrowblue.comicviewer.domain.entity.settings.BookshelfSettings
import com.sorrowblue.comicviewer.domain.repository.SettingsCommonRepository
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageBookshelfSettingsUseCase
import javax.inject.Inject

internal class ManageBookshelfSettings2Interactor @Inject constructor(
    private val bookShelfSettingsRepository: SettingsCommonRepository,
) : ManageBookshelfSettingsUseCase() {

    override val settings = bookShelfSettingsRepository.bookshelfSettings

    override suspend fun edit(action: (BookshelfSettings) -> BookshelfSettings) {
        bookShelfSettingsRepository.updateBookshelfSettings2(action::invoke)
    }
}
