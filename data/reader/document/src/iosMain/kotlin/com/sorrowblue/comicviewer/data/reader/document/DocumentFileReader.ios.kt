package com.sorrowblue.comicviewer.data.reader.document

import com.sorrowblue.comicviewer.data.storage.client.FileReader
import com.sorrowblue.comicviewer.data.storage.client.FileReaderFactory
import com.sorrowblue.comicviewer.data.storage.client.FileReaderKey
import com.sorrowblue.comicviewer.data.storage.client.FileReaderType
import com.sorrowblue.comicviewer.data.storage.client.SeekableInputStream
import com.sorrowblue.comicviewer.framework.common.annotation.VisibleForAssistedInject
import com.sorrowblue.comicviewer.framework.common.scope.DataScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import okio.BufferedSink

@VisibleForAssistedInject
@AssistedInject
actual class DocumentFileReader(
    @Assisted private val mimeType: String,
    @Assisted private val seekableInputStream: SeekableInputStream,
) : FileReader {
    @ContributesIntoMap(DataScope::class)
    @FileReaderKey(FileReaderType.Document)
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
