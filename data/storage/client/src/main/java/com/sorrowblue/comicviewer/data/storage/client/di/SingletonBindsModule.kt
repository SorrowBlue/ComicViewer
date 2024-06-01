package com.sorrowblue.comicviewer.data.storage.client.di

import com.sorrowblue.comicviewer.data.storage.client.impl.RemoteDataSourceImpl
import com.sorrowblue.comicviewer.domain.service.datasource.RemoteDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface SingletonBindsModule {

    @Binds
    fun bindFileModelRemoteDataSourceFactory(factory: RemoteDataSourceImpl.Factory): RemoteDataSource.Factory
}
