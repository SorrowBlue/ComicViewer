package com.sorrowblue.comicviewer.data.remote.reader

import com.sorrowblue.comicviewer.data.common.FileModel
import com.sorrowblue.comicviewer.data.remote.client.FileClient
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

    override suspend fun create(fileClient: FileClient, fileModel: FileModel): FileReader {
        return when (fileModel) {
            is FileModel.File -> when (fileModel.extension) {
                "pdf" -> pdfReaderFactory.create(fileClient.seekableInputStream(fileModel))
                "epub" -> epubReaderFactory.create(fileClient.seekableInputStream(fileModel))
                "xps" -> xpsReaderFactory.create(fileClient.seekableInputStream(fileModel))
                "oxps" -> oxpsReaderFactory.create(fileClient.seekableInputStream(fileModel))
                else -> zipReaderFactory.create(fileClient.seekableInputStream(fileModel))
            }
            is FileModel.Folder -> TODO()
            is FileModel.ImageFolder -> ImageFolderFileReader(fileClient, fileModel)
        }
    }
}
