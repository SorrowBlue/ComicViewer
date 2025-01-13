package com.sorrowblue.comicviewer.domain.service.interactor.settings

import com.sorrowblue.comicviewer.domain.model.settings.SecuritySettings
import com.sorrowblue.comicviewer.domain.service.datasource.DatastoreDataSource
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageSecuritySettingsUseCase
import di.Inject
import org.koin.core.annotation.Singleton

@Singleton
internal class ManageSecuritySettingsInteractor @Inject constructor(
    private val datastoreDataSource: DatastoreDataSource,
) : ManageSecuritySettingsUseCase {

    override val settings = datastoreDataSource.securitySettings

    override suspend fun edit(action: (SecuritySettings) -> SecuritySettings) {
        datastoreDataSource.updateSecuritySettings(action::invoke)
    }
}
