package com.sorrowblue.comicviewer.domain.usecase.interactor

import com.sorrowblue.comicviewer.domain.model.settings.Settings
import com.sorrowblue.comicviewer.domain.repository.SettingsCommonRepository
import com.sorrowblue.comicviewer.domain.usecase.settings.LoadSettingsUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

internal class LoadSettingsInteractor @Inject constructor(
    private val repository: SettingsCommonRepository,
) : LoadSettingsUseCase() {
    override val settings: Flow<Settings> = repository.settings
    override suspend fun edit(action: (Settings) -> Settings) {
        repository.updateSettings(action)
    }
}
