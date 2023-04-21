package com.sorrowblue.comicviewer.data.reader.zip.di

import com.sorrowblue.comicviewer.data.reader.zip.ZipFileReader
import com.sorrowblue.comicviewer.data.remote.reader.FileReader
import com.sorrowblue.comicviewer.data.remote.reader.impl.ZipFileReaderFactory
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class SingletonBindsModule {

    @ZipFileReaderFactory
    @Binds
    abstract fun bindFileReaderFactory(factory: ZipFileReader.Factory): FileReader.Factory
}
