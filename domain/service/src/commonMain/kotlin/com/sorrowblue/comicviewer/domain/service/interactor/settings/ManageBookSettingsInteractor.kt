package com.sorrowblue.comicviewer.domain.service.interactor.settings

import com.sorrowblue.comicviewer.domain.model.settings.BookSettings
import com.sorrowblue.comicviewer.domain.service.datasource.DatastoreDataSource
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageBookSettingsUseCase
import com.sorrowblue.comicviewer.framework.common.scope.DataScope
import dev.zacsweers.metro.ContributesBinding

@ContributesBinding(DataScope::class)
internal class ManageBookSettingsInteractor(private val datastoreDataSource: DatastoreDataSource) :
    ManageBookSettingsUseCase {
    override val settings = datastoreDataSource.bookSettings

    override suspend fun edit(action: (BookSettings) -> BookSettings) {
        datastoreDataSource.updateBookSettings(action)
    }
}
