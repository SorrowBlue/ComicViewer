/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.data.datastore.impl

import androidx.datastore.core.DataStore
import com.sorrowblue.comicviewer.data.datastore.qualifier.WindowSettings
import com.sorrowblue.comicviewer.domain.service.datasource.JvmDatastoreDataSource
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import com.sorrowblue.comicviewer.domain.model.settings.WindowSettings as DomainWindowSettings

/**
 * JVM Datastore data source implementation
 *
 * @property windowSettingsDataStore Window settings data store
 */
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
internal class JvmDatastoreDataSourceImpl(
    @param:WindowSettings private val windowSettingsDataStore: DataStore<DomainWindowSettings>,
) : JvmDatastoreDataSource {

    override val windowSettings: Flow<DomainWindowSettings> = windowSettingsDataStore.data

    override suspend fun updateWindowSettings(
        transform: suspend (DomainWindowSettings) -> DomainWindowSettings,
    ): DomainWindowSettings = windowSettingsDataStore.updateData(transform)
}
