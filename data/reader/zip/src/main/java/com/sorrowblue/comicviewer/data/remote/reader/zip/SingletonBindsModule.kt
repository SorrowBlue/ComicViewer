package com.sorrowblue.comicviewer.data.remote.reader.zip

import com.sorrowblue.comicviewer.data.remote.reader.FileReader
import com.sorrowblue.comicviewer.data.remote.reader.qualifier.ZipReaderFactory
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class SingletonBindsModule {

    @ZipReaderFactory
    @Binds
    abstract fun bindZipFileReaderFactory(factory: ZipFileReader.Factory): FileReader.Factory
}
