package com.sorrowblue.comicviewer.data.datastore.di

import com.sorrowblue.comicviewer.data.datastore.DatastoreDataSourceImpl
import com.sorrowblue.comicviewer.data.infrastructure.datasource.DatastoreDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface SingletonBindsModule {

    @Singleton
    @Binds
    fun bindDatastoreDataSource(datastoreDataSource: DatastoreDataSourceImpl): DatastoreDataSource
}
