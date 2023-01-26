package com.sorrowblue.comicviewer.data.remote.reader.impl

import com.sorrowblue.comicviewer.data.remote.reader.FileReader
import com.sorrowblue.comicviewer.data.remote.reader.FileReaderFactory
import com.sorrowblue.comicviewer.data.remote.reader.SeekableInputStream
import com.sorrowblue.comicviewer.data.remote.reader.qualifier.EpubReaderFactory
import com.sorrowblue.comicviewer.data.remote.reader.qualifier.OxpsReaderFactory
import com.sorrowblue.comicviewer.data.remote.reader.qualifier.PdfReaderFactory
import com.sorrowblue.comicviewer.data.remote.reader.qualifier.XpsReaderFactory
import com.sorrowblue.comicviewer.data.remote.reader.qualifier.ZipReaderFactory
import javax.inject.Inject

internal class FileReaderFactoryImpl @Inject constructor(
    @PdfReaderFactory private val pdfReaderFactory: FileReader.Factory,
    @EpubReaderFactory private val epubReaderFactory: FileReader.Factory,
    @XpsReaderFactory private val xpsReaderFactory: FileReader.Factory,
    @OxpsReaderFactory private val oxpsReaderFactory: FileReader.Factory,
    @ZipReaderFactory private val zipReaderFactory: FileReader.Factory
) : FileReaderFactory {

    override suspend fun create(
        extension: String,
        seekableInputStream: SeekableInputStream
    ): FileReader {
        return when (extension) {
            "pdf" -> pdfReaderFactory.create(seekableInputStream)
            "epub" -> epubReaderFactory.create(seekableInputStream)
            "xps" -> xpsReaderFactory.create(seekableInputStream)
            "oxps" -> oxpsReaderFactory.create(seekableInputStream)
            else -> zipReaderFactory.create(seekableInputStream)
        }
    }
}
