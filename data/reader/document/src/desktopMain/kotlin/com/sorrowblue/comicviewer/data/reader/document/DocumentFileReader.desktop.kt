package com.sorrowblue.comicviewer.data.reader.document

import com.sorrowblue.comicviewer.data.storage.client.FileReaderFactory
import com.sorrowblue.comicviewer.data.storage.client.SeekableInputStream
import com.sorrowblue.comicviewer.domain.service.FileReader
import com.sorrowblue.comicviewer.domain.service.datasource.DatastoreDataSource
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import logcat.logcat
import okio.BufferedSink

@AssistedInject
internal actual class DocumentFileReader(
    @Assisted mimeType: String,
    @Assisted seekableInputStream: SeekableInputStream,
    private val dataSource: DatastoreDataSource,
) : FileReader {

    @AssistedFactory
    actual fun interface Factory : FileReaderFactory {
        actual override fun create(
            mimeType: String,
            seekableInputStream: SeekableInputStream,
        ): DocumentFileReader
    }

    init {
        runBlocking {
            val path = dataSource.pdfPluginSettings.first().pluginJarPath
            OutsideDocumentFileReader.initLoad(path)
        }
    }

    private val readImpl = RandomAccessReadImpl(seekableInputStream)
    private val reader: com.sorrowblue.comicviewer.plugin.pdf.FileReader =
        OutsideDocumentFileReader.getReader(readImpl, mimeType)

    actual override suspend fun pageCount(): Int {
        return reader.pageCount()
    }

    actual override suspend fun fileName(pageIndex: Int): String {
        return ""
    }

    actual override suspend fun fileSize(pageIndex: Int): Long {
        return 0
    }

    private val mutex = Mutex()

    actual override suspend fun copyTo(pageIndex: Int, bufferedSink: BufferedSink) {
        mutex.withLock {
            logcat { "copyTo start pageIndex: $pageIndex" }
            reader.loadPage(pageIndex, bufferedSink.outputStream())
            logcat { "copyTo end  pageIndex: $pageIndex" }
        }
    }

    actual override fun close() {
        reader.close()
    }
}
