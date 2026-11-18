/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.data.datastore.di

import androidx.datastore.core.DataStore
import com.sorrowblue.comicviewer.data.datastore.DataStoreMaker
import com.sorrowblue.comicviewer.data.datastore.serializer.OkioKSerializer
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

/**
 * JVM DataStore module
 */
/*
@ContributesTo(AppScope::class)
@BindingContainer
object JvmDataStoreModule {

    */
/**
     * Provide window settings data store
     *
     * @return Window settings data store
     *//*

    @SingleIn(AppScope::class)
    @Provides
    private fun <T> DataStoreMaker.dataStore3(serializer: OkioKSerializer<T>): DataStore<T> =
        createDataStore(serializer)
}
*/
