package com.sorrowblue.comicviewer.data.remote.reader.impl

import android.content.Context
import com.sorrowblue.comicviewer.data.remote.reader.FileReader
import com.sorrowblue.comicviewer.data.remote.reader.FileReaderFactory
import com.sorrowblue.comicviewer.data.remote.reader.SeekableInputStream
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

internal class FileReaderFactoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
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
        return Class.forName("com.sorrowblue.comicviewer.data.remote.reader.document.$name")
            .getDeclaredConstructor(Context::class.java, SeekableInputStream::class.java)
            .newInstance(context, seekableInputStream) as? FileReader
    }
    private fun loadZipReader(
        seekableInputStream: SeekableInputStream
    ): FileReader? {
        return Class.forName("com.sorrowblue.comicviewer.data.remote.reader.zip.ZipFileReader")
            .getDeclaredConstructor(SeekableInputStream::class.java)
            .newInstance(seekableInputStream) as? FileReader
    }
}
