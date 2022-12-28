package com.sorrowblue.comicviewer.data.remote.reader.di

import com.sorrowblue.comicviewer.data.remote.reader.FileReaderFactory
import com.sorrowblue.comicviewer.data.remote.reader.FileReaderFactoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class SingletonBindsModule {

    @Binds
    abstract fun bindFileReaderFactory(factory: FileReaderFactoryImpl): FileReaderFactory
}
