package com.sorrowblue.comicviewer.domain.service.interactor.settings

import com.sorrowblue.comicviewer.domain.model.settings.CollectionSettings
import com.sorrowblue.comicviewer.domain.service.datasource.DatastoreDataSource
import com.sorrowblue.comicviewer.domain.usecase.settings.CollectionSettingsUseCase
import com.sorrowblue.comicviewer.framework.common.scope.DataScope
import dev.zacsweers.metro.ContributesBinding
import kotlinx.coroutines.flow.Flow

@ContributesBinding(DataScope::class)
internal class CollectionSettingsInteractor(private val dataSource: DatastoreDataSource) :
    CollectionSettingsUseCase {
    override val settings: Flow<CollectionSettings> = dataSource.collectionSettings

    override suspend fun edit(action: (CollectionSettings) -> CollectionSettings) {
        dataSource.updateCollectionSettings(action)
    }
}
