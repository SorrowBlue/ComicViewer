package com.sorrowblue.comicviewer.data.reader.zip.impl

import com.sorrowblue.comicviewer.data.storage.client.FileReaderFactory
import com.sorrowblue.comicviewer.data.storage.client.SeekableInputStream
import com.sorrowblue.comicviewer.data.storage.client.qualifier.ImageExtension
import com.sorrowblue.comicviewer.domain.service.FileReader
import com.sorrowblue.comicviewer.domain.service.IoDispatcher
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okio.BufferedSink

@AssistedInject
internal actual class ZipFileReader(
    @Assisted mimeType: String,
    @Assisted private val seekableInputStream: SeekableInputStream,
    @ImageExtension supportedException: Set<String>,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
) : FileReader {
    @AssistedFactory
    actual fun interface Factory : FileReaderFactory {
        actual override fun create(
            mimeType: String,
            seekableInputStream: SeekableInputStream,
        ): ZipFileReader
    }

    val iosZipFileReader = IosZipFileReader.factory.create(seekableInputStream, supportedException)

    private val mutex = Mutex()

    actual override fun close() {
        iosZipFileReader.close()
    }

    actual override suspend fun pageCount(): Int = iosZipFileReader.pageCount()

    actual override suspend fun copyTo(pageIndex: Int, bufferedSink: BufferedSink) {
        mutex.withLock {
            iosZipFileReader.copyTo(pageIndex, bufferedSink)
        }
    }

    actual override suspend fun fileSize(pageIndex: Int): Long =
        iosZipFileReader.fileSize(pageIndex)

    actual override suspend fun fileName(pageIndex: Int): String =
        iosZipFileReader.fileName(pageIndex)
}
