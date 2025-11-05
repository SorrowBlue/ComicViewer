package com.sorrowblue.comicviewer.domain.service.interactor.settings

import com.sorrowblue.comicviewer.domain.model.settings.DisplaySettings
import com.sorrowblue.comicviewer.domain.service.datasource.DatastoreDataSource
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageDisplaySettingsUseCase
import dev.zacsweers.metro.Inject

@Inject
internal class ManageDisplaySettingsInteractor(
    private val datastoreDataSource: DatastoreDataSource,
) : ManageDisplaySettingsUseCase {

    override val settings = datastoreDataSource.displaySettings

    override suspend fun edit(action: (DisplaySettings) -> DisplaySettings) {
        datastoreDataSource.updateDisplaySettings(action)
    }
}
