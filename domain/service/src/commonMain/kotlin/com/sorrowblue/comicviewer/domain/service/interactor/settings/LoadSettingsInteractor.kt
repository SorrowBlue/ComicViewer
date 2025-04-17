package com.sorrowblue.comicviewer.domain.service.interactor.settings

import com.sorrowblue.comicviewer.domain.model.settings.Settings
import com.sorrowblue.comicviewer.domain.service.datasource.DatastoreDataSource
import com.sorrowblue.comicviewer.domain.usecase.settings.LoadSettingsUseCase
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Singleton

@Singleton
internal class LoadSettingsInteractor(
    private val datastoreDataSource: DatastoreDataSource,
) : LoadSettingsUseCase {

    override val settings: Flow<Settings> = datastoreDataSource.settings

    override suspend fun edit(action: (Settings) -> Settings) {
        datastoreDataSource.updateSettings(action)
    }
}
