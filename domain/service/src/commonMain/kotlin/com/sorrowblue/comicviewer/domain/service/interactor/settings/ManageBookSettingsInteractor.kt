package com.sorrowblue.comicviewer.domain.service.interactor.settings

import com.sorrowblue.comicviewer.domain.model.settings.BookSettings
import com.sorrowblue.comicviewer.domain.service.datasource.DatastoreDataSource
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageBookSettingsUseCase
import dev.zacsweers.metro.Inject

@Inject
internal class ManageBookSettingsInteractor(private val datastoreDataSource: DatastoreDataSource) :
    ManageBookSettingsUseCase {
    override val settings = datastoreDataSource.bookSettings

    override suspend fun edit(action: (BookSettings) -> BookSettings) {
        datastoreDataSource.updateBookSettings(action)
    }
}
