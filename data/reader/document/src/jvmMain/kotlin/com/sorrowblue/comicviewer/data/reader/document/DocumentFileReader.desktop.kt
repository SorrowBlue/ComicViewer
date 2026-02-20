package com.sorrowblue.comicviewer.data.reader.document

import com.sorrowblue.comicviewer.data.storage.client.FileReaderFactory
import com.sorrowblue.comicviewer.data.storage.client.SeekableInputStream
import com.sorrowblue.comicviewer.domain.service.FileReader
import com.sorrowblue.comicviewer.domain.service.datasource.DatastoreDataSource
import com.sorrowblue.comicviewer.plugin.pdf.PdfFileReader
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
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

    private val readImpl = RandomAccessReadImpl(seekableInputStream)
    private val reader: PdfFileReader =
        OutsideDocumentFileReader.getReader(readImpl, mimeType)

    actual override suspend fun pageCount(): Int = reader.pageCount()

    actual override suspend fun fileName(pageIndex: Int): String = ""

    actual override suspend fun fileSize(pageIndex: Int): Long = 0

    private val mutex = Mutex()

    actual override suspend fun copyTo(pageIndex: Int, bufferedSink: BufferedSink) {
        mutex.withLock {
            reader.loadPage(pageIndex, bufferedSink.outputStream())
        }
    }

    actual override fun close() {
        reader.close()
    }
}
