package com.sorrowblue.comicviewer.data.datastore.di

import com.sorrowblue.comicviewer.data.datasource.DatastoreDataSource
import com.sorrowblue.comicviewer.data.datastore.DatastoreDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class SingletonBindsModule {

    @Singleton
    @Binds
    abstract fun bindDatastoreDataSource(datastoreDataSource: DatastoreDataSourceImpl): DatastoreDataSource
}
