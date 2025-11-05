package com.sorrowblue.comicviewer.domain.service.interactor.settings

import com.sorrowblue.comicviewer.domain.model.settings.ViewerOperationSettings
import com.sorrowblue.comicviewer.domain.service.datasource.DatastoreDataSource
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageViewerOperationSettingsUseCase
import dev.zacsweers.metro.Inject

@Inject
internal class ManageViewerOperationSettingsInteractor(
    private val datastoreDataSource: DatastoreDataSource,
) : ManageViewerOperationSettingsUseCase {

    override val settings = datastoreDataSource.viewerOperationSettings

    override suspend fun edit(action: (ViewerOperationSettings) -> ViewerOperationSettings) {
        datastoreDataSource.updateViewerOperationSettings(action)
    }
}
