/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.settings

import com.sorrowblue.comicviewer.domain.model.settings.BookSettings
import com.sorrowblue.comicviewer.domain.repository.SettingsRepository
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow

@Inject
class ManageBookSettingsUseCase(private val settingsRepository: SettingsRepository) :
    ManageSettingsUseCase<BookSettings> {
    override val settings: Flow<BookSettings> = settingsRepository.bookSettings

    override suspend fun edit(action: (BookSettings) -> BookSettings) {
        settingsRepository.updateBookSettings(action)
    }
}
