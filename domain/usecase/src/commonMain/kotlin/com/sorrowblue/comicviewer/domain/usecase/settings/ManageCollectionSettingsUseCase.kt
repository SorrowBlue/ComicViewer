/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.settings

import com.sorrowblue.comicviewer.domain.model.settings.CollectionSettings
import com.sorrowblue.comicviewer.domain.repository.SettingsRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow

/**
 * Use case for observing and updating [CollectionSettings].
 */
interface ManageCollectionSettingsUseCase : ManageSettingsUseCase<CollectionSettings>

@Inject
@ContributesBinding(AppScope::class)
internal class ManageCollectionSettingsUseCaseImpl(
    private val settingsRepository: SettingsRepository,
) : ManageCollectionSettingsUseCase {

    override val settings: Flow<CollectionSettings> = settingsRepository.collectionSettings

    override suspend fun edit(action: (CollectionSettings) -> CollectionSettings) {
        settingsRepository.updateCollectionSettings(action)
    }
}
