package com.sorrowblue.comicviewer.data.remote.client.di

import com.sorrowblue.comicviewer.data.remote.client.FileClientFactory
import com.sorrowblue.comicviewer.data.remote.client.impl.FileClientFactoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class SingletonBindsModule {

    @Binds
    abstract fun bindFileClientFactory(factory: FileClientFactoryImpl): FileClientFactory
}
