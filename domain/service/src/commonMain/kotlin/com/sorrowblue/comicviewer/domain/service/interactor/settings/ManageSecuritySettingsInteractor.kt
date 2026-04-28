package com.sorrowblue.comicviewer.domain.service.interactor.settings

import com.sorrowblue.comicviewer.domain.model.settings.SecuritySettings
import com.sorrowblue.comicviewer.domain.service.datasource.DatastoreDataSource
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageSecuritySettingsUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding

@ContributesBinding(AppScope::class)
internal class ManageSecuritySettingsInteractor(
    private val datastoreDataSource: DatastoreDataSource,
) : ManageSecuritySettingsUseCase {
    override val settings = datastoreDataSource.securitySettings

    override suspend fun edit(action: (SecuritySettings) -> SecuritySettings) {
        datastoreDataSource.updateSecuritySettings(action::invoke)
    }
}
