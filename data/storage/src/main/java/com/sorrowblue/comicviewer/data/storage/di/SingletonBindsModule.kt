package com.sorrowblue.comicviewer.data.storage.di

import com.sorrowblue.comicviewer.data.reader.impl.ImageExtension
import com.sorrowblue.comicviewer.data.storage.impl.RemoteDataSourceImpl
import com.sorrowblue.comicviewer.domain.model.SUPPORTED_IMAGE
import com.sorrowblue.comicviewer.domain.service.datasource.RemoteDataSource
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface SingletonBindsModule {

    @Binds
    fun bindFileModelRemoteDataSourceFactory(factory: RemoteDataSourceImpl.Factory): RemoteDataSource.Factory
}

@Module
@InstallIn(SingletonComponent::class)
internal object SingletonProvidesModule {

    @ImageExtension
    @Singleton
    @Provides
    fun bindFileReaderFactory(): Set<String> = SUPPORTED_IMAGE
}
