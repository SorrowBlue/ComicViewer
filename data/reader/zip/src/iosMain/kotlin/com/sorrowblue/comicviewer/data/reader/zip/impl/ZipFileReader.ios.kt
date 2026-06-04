package com.sorrowblue.comicviewer.data.reader.zip.impl

import com.sorrowblue.comicviewer.data.storage.client.FileReader
import com.sorrowblue.comicviewer.data.storage.client.FileReaderFactory
import com.sorrowblue.comicviewer.data.storage.client.FileReaderKey
import com.sorrowblue.comicviewer.data.storage.client.FileReaderType
import com.sorrowblue.comicviewer.data.storage.client.SeekableInputStream
import com.sorrowblue.comicviewer.data.storage.client.qualifier.ImageExtension
import com.sorrowblue.comicviewer.framework.common.IoDispatcher
import com.sorrowblue.comicviewer.framework.common.annotation.VisibleForAssistedInject
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.io.Source
import kotlinx.io.buffered
import kotlinx.io.okio.asKotlinxIoRawSource

@VisibleForAssistedInject
@AssistedInject
actual class ZipFileReader(
    @Assisted mimeType: String,
    @Assisted private val seekableInputStream: SeekableInputStream,
    @ImageExtension supportedException: Set<String>,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
) : FileReader {
    @ContributesIntoMap(AppScope::class)
    @FileReaderKey(FileReaderType.Zip)
    @AssistedFactory
    actual fun interface Factory : FileReaderFactory {
        actual override fun create(
            seekableInputStream: SeekableInputStream,
        ): ZipFileReader
    }

    val iosZipFileReader = IosZipFileReader.factory.create(seekableInputStream, supportedException)

    private val mutex = Mutex()

    actual override fun close() {
        iosZipFileReader.close()
    }

    actual override suspend fun pageCount(): Int = iosZipFileReader.pageCount()

    actual override suspend fun source(pageIndex: Int): Source {
        return mutex.withLock {
            Buffer().also {
                iosZipFileReader.copyTo(pageIndex, it)
            }.asKotlinxIoRawSource().buffered()
        }
    }

    actual override suspend fun fileSize(pageIndex: Int): Long =
        iosZipFileReader.fileSize(pageIndex)

    actual override suspend fun fileName(pageIndex: Int): String =
        iosZipFileReader.fileName(pageIndex)
}
