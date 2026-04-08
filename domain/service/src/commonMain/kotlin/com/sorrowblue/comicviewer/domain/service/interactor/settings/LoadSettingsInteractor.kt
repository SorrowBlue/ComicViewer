package com.sorrowblue.comicviewer.domain.service.interactor.settings

import com.sorrowblue.comicviewer.domain.model.settings.Settings
import com.sorrowblue.comicviewer.domain.service.datasource.DatastoreDataSource
import com.sorrowblue.comicviewer.domain.usecase.settings.LoadSettingsUseCase
import com.sorrowblue.comicviewer.framework.common.scope.DataScope
import dev.zacsweers.metro.ContributesBinding
import kotlinx.coroutines.flow.Flow

@ContributesBinding(DataScope::class)
internal class LoadSettingsInteractor(private val datastoreDataSource: DatastoreDataSource) :
    LoadSettingsUseCase {
    override val settings: Flow<Settings> = datastoreDataSource.settings

    override suspend fun edit(action: (Settings) -> Settings) {
        datastoreDataSource.updateSettings(action)
    }
}
