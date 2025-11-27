package com.sorrowblue.comicviewer.data.datastore.di

import com.sorrowblue.comicviewer.domain.service.datasource.DatastoreDataSource
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Scope

@Scope
annotation class DataStoreScope

@GraphExtension(DataStoreScope::class)
interface DataStoreGraph {
    val datastoreDataSource: DatastoreDataSource

    @ContributesTo(AppScope::class)
    @GraphExtension.Factory
    interface Factory {
        fun createDataStoreGraph(): DataStoreGraph
    }
}
