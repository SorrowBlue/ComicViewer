package com.sorrowblue.comicviewer.data.remote.reader.impl

import android.content.Context
import com.sorrowblue.comicviewer.data.remote.reader.FileReader
import com.sorrowblue.comicviewer.data.remote.reader.FileReaderFactory
import com.sorrowblue.comicviewer.data.remote.reader.SeekableInputStream
import com.sorrowblue.comicviewer.data.remote.reader.qualifier.ZipReaderFactory
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

internal class FileReaderFactoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    @ZipReaderFactory private val zipReaderFactory: FileReader.Factory
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
            else -> zipReaderFactory.create(seekableInputStream)
        }
    }

    private fun loadReader(
        name: String,
        context: Context,
        seekableInputStream: SeekableInputStream
    ): FileReader? {
        return Class.forName("com.sorrowblue.extention.document.$name")
            .getDeclaredConstructor(Context::class.java, SeekableInputStream::class.java)
            .newInstance(context, seekableInputStream) as? FileReader
    }
}
