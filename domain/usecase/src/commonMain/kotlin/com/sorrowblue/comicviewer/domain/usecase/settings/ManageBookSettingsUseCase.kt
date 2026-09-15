/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.settings

import com.sorrowblue.comicviewer.domain.model.settings.BookSettings
import com.sorrowblue.comicviewer.domain.repository.SettingsRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow

interface ManageBookSettingsUseCase : ManageSettingsUseCase<BookSettings>

@Inject
@ContributesBinding(AppScope::class)
internal class ManageBookSettingsUseCaseImpl(private val settingsRepository: SettingsRepository) :
    ManageBookSettingsUseCase {

    override val settings: Flow<BookSettings> = settingsRepository.bookSettings

    override suspend fun edit(action: (BookSettings) -> BookSettings) {
        settingsRepository.updateBookSettings(action)
    }
}
