package com.sorrowblue.comicviewer.domain.service.interactor.settings

import com.sorrowblue.comicviewer.domain.model.settings.BookSettings
import com.sorrowblue.comicviewer.domain.model.settings.ViewerSettings
import com.sorrowblue.comicviewer.domain.service.datasource.DatastoreDataSource
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageBookSettingsUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageViewerSettingsUseCase
import di.Inject
import org.koin.core.annotation.Singleton

@Singleton
internal class ManageViewerSettingsInteractor @Inject constructor(
    private val datastoreDataSource: DatastoreDataSource,
) : ManageViewerSettingsUseCase {

    override val settings = datastoreDataSource.viewerSettings

    override suspend fun edit(action: (ViewerSettings) -> ViewerSettings) {
        datastoreDataSource.updateViewerSettings(action)
    }
}

@Singleton
internal class ManageBookSettingsInteractor @Inject constructor(
    private val datastoreDataSource: DatastoreDataSource,
) : ManageBookSettingsUseCase {

    override val settings = datastoreDataSource.bookSettings
    override suspend fun edit(action: (BookSettings) -> BookSettings) {
        datastoreDataSource.updateBookSettings(action)
    }
}
