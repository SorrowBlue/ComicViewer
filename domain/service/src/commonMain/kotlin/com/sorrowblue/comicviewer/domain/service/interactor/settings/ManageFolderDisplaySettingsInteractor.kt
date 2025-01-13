package com.sorrowblue.comicviewer.domain.service.interactor.settings

import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderDisplaySettings
import com.sorrowblue.comicviewer.domain.service.datasource.DatastoreDataSource
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageFolderDisplaySettingsUseCase
import di.Inject
import org.koin.core.annotation.Singleton

@Singleton
internal class ManageFolderDisplaySettingsInteractor @Inject constructor(
    private val datastoreDataSource: DatastoreDataSource,
) : ManageFolderDisplaySettingsUseCase {

    override val settings = datastoreDataSource.folderDisplaySettings

    override suspend fun edit(action: (FolderDisplaySettings) -> FolderDisplaySettings) {
        datastoreDataSource.updateFolderDisplaySettings(action::invoke)
    }
}
