package com.sorrowblue.comicviewer.data.reader.di

import com.sorrowblue.comicviewer.data.reader.FileReaderFactory
import com.sorrowblue.comicviewer.data.reader.impl.FileReaderFactoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface SingletonBindsModule {

    @Binds
    fun bindFileReaderFactory(factory: FileReaderFactoryImpl): FileReaderFactory
}
