package com.sorrowblue.comicviewer.data.reader.document

import com.sorrowblue.comicviewer.data.storage.client.FileReaderFactory
import com.sorrowblue.comicviewer.data.storage.client.SeekableInputStream
import com.sorrowblue.comicviewer.domain.service.FileReader
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import okio.BufferedSink

@AssistedInject
internal actual class DocumentFileReader(
    @Assisted private val mimeType: String,
    @Assisted private val seekableInputStream: SeekableInputStream,
) : FileReader {
    @AssistedFactory
    actual fun interface Factory : FileReaderFactory {
        actual override fun create(
            mimeType: String,
            seekableInputStream: SeekableInputStream,
        ): DocumentFileReader
    }

    actual override fun close() {
        TODO("Not yet implemented")
    }

    actual override suspend fun pageCount(): Int {
        TODO("Not yet implemented")
    }

    actual override suspend fun copyTo(pageIndex: Int, bufferedSink: BufferedSink) {
        TODO("Not yet implemented")
    }

    actual override suspend fun fileSize(pageIndex: Int): Long {
        TODO("Not yet implemented")
    }

    actual override suspend fun fileName(pageIndex: Int): String {
        TODO("Not yet implemented")
    }
}
