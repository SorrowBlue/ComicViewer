package com.sorrowblue.comicviewer.domain.service.interactor.settings

import com.sorrowblue.comicviewer.domain.model.settings.FolderSettings
import com.sorrowblue.comicviewer.domain.service.datasource.DatastoreDataSource
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageFolderSettingsUseCase
import dev.zacsweers.metro.Inject

@Inject
internal class ManageFolderSettingsInteractor(
    private val datastoreDataSource: DatastoreDataSource,
) : ManageFolderSettingsUseCase {

    override val settings = datastoreDataSource.folderSettings

    override suspend fun edit(action: (FolderSettings) -> FolderSettings) {
        datastoreDataSource.updateFolderSettings(action::invoke)
    }
}
