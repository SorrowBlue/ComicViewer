package com.sorrowblue.comicviewer.domain.service.interactor.settings

import com.sorrowblue.comicviewer.domain.model.settings.ViewerSettings
import com.sorrowblue.comicviewer.domain.service.datasource.DatastoreDataSource
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageViewerSettingsUseCase
import dev.zacsweers.metro.Inject

@Inject
internal class ManageViewerSettingsInteractor(
    private val datastoreDataSource: DatastoreDataSource,
) : ManageViewerSettingsUseCase {

    override val settings = datastoreDataSource.viewerSettings

    override suspend fun edit(action: (ViewerSettings) -> ViewerSettings) {
        datastoreDataSource.updateViewerSettings(action)
    }
}
