package com.sorrowblue.comicviewer.domain.service.interactor.settings

import com.sorrowblue.comicviewer.domain.model.settings.Settings
import com.sorrowblue.comicviewer.domain.service.datasource.DatastoreDataSource
import com.sorrowblue.comicviewer.domain.usecase.settings.LoadSettingsUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

internal class LoadSettingsInteractor @Inject constructor(
    private val datastoreDataSource: DatastoreDataSource,
) : LoadSettingsUseCase {

    override val settings: Flow<Settings> = datastoreDataSource.settings

    override suspend fun edit(action: (Settings) -> Settings) {
        datastoreDataSource.updateSettings(action)
    }
}
