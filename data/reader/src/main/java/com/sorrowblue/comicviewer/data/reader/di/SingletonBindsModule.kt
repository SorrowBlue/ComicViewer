package com.sorrowblue.comicviewer.data.reader.di

import com.sorrowblue.comicviewer.data.reader.FileReaderFactory
import com.sorrowblue.comicviewer.data.reader.impl.FileReaderFactoryImpl
import com.sorrowblue.comicviewer.data.reader.impl.ImageExtension
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class SingletonBindsModule {

    @Binds
    abstract fun bindFileReaderFactory(factory: FileReaderFactoryImpl): FileReaderFactory
}
