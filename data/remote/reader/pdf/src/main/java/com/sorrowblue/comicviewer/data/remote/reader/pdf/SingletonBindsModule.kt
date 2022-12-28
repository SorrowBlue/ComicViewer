package com.sorrowblue.comicviewer.data.remote.reader.pdf

import com.sorrowblue.comicviewer.data.remote.reader.FileReader
import com.sorrowblue.comicviewer.data.remote.reader.qualifier.EpubReaderFactory
import com.sorrowblue.comicviewer.data.remote.reader.qualifier.OxpsReaderFactory
import com.sorrowblue.comicviewer.data.remote.reader.qualifier.PdfReaderFactory
import com.sorrowblue.comicviewer.data.remote.reader.qualifier.XpsReaderFactory
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class SingletonBindsModule {

    @PdfReaderFactory
    @Binds
    abstract fun bindPdfFileReaderFactory(factory: PdfFileReader.Factory): FileReader.Factory

    @EpubReaderFactory
    @Binds
    abstract fun bindEpubFileReaderFactory(factory: EpubFileReader.Factory): FileReader.Factory

    @XpsReaderFactory
    @Binds
    abstract fun bindXpsFileReaderFactory(factory: XpsFileReader.Factory): FileReader.Factory

    @OxpsReaderFactory
    @Binds
    abstract fun bindOxpsFileReaderFactory(factory: OxpsFileReader.Factory): FileReader.Factory
}
