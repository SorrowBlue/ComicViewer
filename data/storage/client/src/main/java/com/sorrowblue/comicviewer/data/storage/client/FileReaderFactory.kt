package com.sorrowblue.comicviewer.data.storage.client

import android.content.Context
import com.sorrowblue.comicviewer.domain.reader.FileReader
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.ServiceLoader
import javax.inject.Inject
import javax.inject.Qualifier
import logcat.asLog
import logcat.logcat

interface FileReader_Factory {
    fun create(seekableInputStream: SeekableInputStream): FileReader
}

interface FileReader_Provider {
    fun get(context: Context, seekableInputStream: SeekableInputStream): FileReader
    val extension: String
}

class FileReaderFactory @Inject constructor(
    @ApplicationContext private val context: Context,
    @ZipFileReaderFactory private val zipFileReaderFactory: FileReader_Factory,
) {

    suspend fun create(
        extension: String,
        seekableInputStream: SeekableInputStream,
    ): FileReader? {
        return when (extension) {
            "pdf" -> loadReader("pdf", context, seekableInputStream)
            "epub" -> loadReader("epub", context, seekableInputStream)
            "xps" -> loadReader("xps", context, seekableInputStream)
            "oxps" -> loadReader("oxps", context, seekableInputStream)
            else -> loadZipReader(seekableInputStream)
        }
    }

    var reader: FileReader? = null

    private fun loadReader(
        extension: String,
        context: Context,
        seekableInputStream: SeekableInputStream,
    ): FileReader? {
        return reader ?: run {
            val serviceLoader = ServiceLoader.load(
                FileReader_Provider::class.java,
                FileReader_Provider::class.java.classLoader
            )
            kotlin.runCatching {
                serviceLoader.firstOrNull { it.extension == extension }?.get(context, seekableInputStream)
            }.onFailure {
                logcat { it.asLog() }
            }.getOrNull()?.also {
                reader = it
            }
        }
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
