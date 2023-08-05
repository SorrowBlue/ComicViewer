package com.sorrowblue.comicviewer.data.reader.impl

import android.content.Context
import com.sorrowblue.comicviewer.data.reader.FileReader
import com.sorrowblue.comicviewer.data.reader.FileReaderFactory
import com.sorrowblue.comicviewer.data.reader.SeekableInputStream
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Qualifier

internal class FileReaderFactoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    @ZipFileReaderFactory private val zipFileReaderFactory: FileReader.Factory
) : FileReaderFactory {

    override suspend fun create(
        extension: String,
        seekableInputStream: SeekableInputStream
    ): FileReader? {
        return when (extension) {
            "pdf" -> loadReader("PdfFileReader", context, seekableInputStream)
            "epub" -> loadReader("EpubFileReader", context, seekableInputStream)
            "xps" -> loadReader("XpsFileReader", context, seekableInputStream)
            "oxps" -> loadReader("OxpsFileReader", context, seekableInputStream)
            else -> loadZipReader(seekableInputStream)
        }
    }

    private fun loadReader(
        name: String,
        context: Context,
        seekableInputStream: SeekableInputStream
    ): FileReader? {
        return Class.forName("com.sorrowblue.comicviewer.data.reader.document.$name")
            .getDeclaredConstructor(Context::class.java, SeekableInputStream::class.java)
            .newInstance(context, seekableInputStream) as? FileReader
    }

    private fun loadZipReader(seekableInputStream: SeekableInputStream) =
        zipFileReaderFactory.create(seekableInputStream)
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ZipFileReaderFactory

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ImageExtension
