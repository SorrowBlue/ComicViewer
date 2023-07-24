package com.sorrowblue.comicviewer.data.reader.zip.di

import com.sorrowblue.comicviewer.data.reader.FileReader
import com.sorrowblue.comicviewer.data.reader.impl.ZipFileReaderFactory
import com.sorrowblue.comicviewer.data.reader.zip.ZipFileReader
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
