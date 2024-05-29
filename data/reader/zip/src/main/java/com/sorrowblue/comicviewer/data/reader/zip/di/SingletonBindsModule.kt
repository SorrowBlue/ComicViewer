package com.sorrowblue.comicviewer.data.reader.zip.di

import com.sorrowblue.comicviewer.data.reader.zip.ZipFileReader
import com.sorrowblue.comicviewer.data.storage.client.FileReader_Factory
import com.sorrowblue.comicviewer.data.storage.client.ZipFileReaderFactory
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface SingletonBindsModule {

    @ZipFileReaderFactory
    @Binds
    fun bindFileReaderFactory(factory: ZipFileReader.Factory): FileReader_Factory
}
