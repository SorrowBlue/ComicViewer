package com.sorrowblue.comicviewer.domain.service.interactor.settings

import com.sorrowblue.comicviewer.domain.model.settings.CollectionSettings
import com.sorrowblue.comicviewer.domain.service.datasource.DatastoreDataSource
import com.sorrowblue.comicviewer.domain.usecase.settings.CollectionSettingsUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import kotlinx.coroutines.flow.Flow

@ContributesBinding(AppScope::class)
internal class CollectionSettingsInteractor(private val dataSource: DatastoreDataSource) :
    CollectionSettingsUseCase {
    override val settings: Flow<CollectionSettings> = dataSource.collectionSettings

    override suspend fun edit(action: (CollectionSettings) -> CollectionSettings) {
        dataSource.updateCollectionSettings(action)
    }
}
