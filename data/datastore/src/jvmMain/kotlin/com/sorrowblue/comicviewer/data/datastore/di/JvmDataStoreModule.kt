/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.data.datastore.di

import androidx.datastore.core.DataStore
import com.sorrowblue.comicviewer.data.datastore.DataStoreMaker
import com.sorrowblue.comicviewer.data.datastore.qualifier.WindowSettings
import com.sorrowblue.comicviewer.data.datastore.serializer.WindowSettingsSerializer
import com.sorrowblue.comicviewer.domain.model.settings.WindowSettings as DomainWindowSettings
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

/**
 * JVM DataStore module
 */
@ContributesTo(AppScope::class)
interface JvmDataStoreModule {

    /**
     * Provide window settings data store
     *
     * @param dataStoreMaker DataStore maker
     * @return Window settings data store
     */
    @WindowSettings
    @SingleIn(AppScope::class)
    @Provides
    private fun provideWindowSettingsDataStore(
        dataStoreMaker: DataStoreMaker,
    ): DataStore<DomainWindowSettings> = dataStoreMaker.createDataStore(WindowSettingsSerializer)
}
